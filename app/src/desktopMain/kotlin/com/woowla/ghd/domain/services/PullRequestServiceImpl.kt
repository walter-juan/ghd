package com.woowla.ghd.domain.services

import com.woowla.ghd.core.AppLogger
import com.woowla.ghd.domain.data.LocalDataSource
import com.woowla.ghd.domain.data.RemoteDataSource
import com.woowla.ghd.domain.entities.AppSettings
import com.woowla.ghd.domain.entities.NotificationsSettings
import com.woowla.ghd.domain.entities.PullRequest
import com.woowla.ghd.domain.entities.PullRequestStateExtended
import com.woowla.ghd.domain.entities.PullRequestWithRepoAndReviews
import com.woowla.ghd.domain.entities.RepoToCheck
import com.woowla.ghd.domain.entities.Review
import com.woowla.ghd.domain.entities.ReviewState
import com.woowla.ghd.domain.entities.SyncResultEntry
import com.woowla.ghd.domain.entities.SyncSettings
import com.woowla.ghd.domain.entities.filterNotSyncValid
import com.woowla.ghd.domain.entities.filterSyncValid
import com.woowla.ghd.domain.mappers.toSyncResultEntry
import com.woowla.ghd.domain.notifications.NotificationsSender
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlin.time.Clock

class PullRequestServiceImpl(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource,
    private val notificationsSender: NotificationsSender,
    private val appSettingsService: AppSettingsService,
    private val appLogger: AppLogger,
) : PullRequestService {
    override suspend fun getAll(): Result<List<PullRequestWithRepoAndReviews>> {
        return localDataSource.getAllPullRequests()
            .mapCatching { pullRequests ->
                pullRequests.sorted()
            }
    }

    override suspend fun synchronize(syncResultId: Long, syncSettings: SyncSettings, repoToCheckList: List<RepoToCheck>): List<SyncResultEntry> {
        appLogger.d("Synchronizer :: sync :: pulls :: start")
        val prSyncStartAt = Clock.System.now()
        val pullRequestsBefore = getAll().getOrDefault(listOf())
        val enabledRepoToCheckList = repoToCheckList.filter { it.arePullRequestsEnabled }

        // fetch all remote pull requests
        val apiPullRequestResultsDeferred = coroutineScope {
            enabledRepoToCheckList.map { repoToCheck ->
                val startAt = Clock.System.now()
                async {
                    val pulls = remoteDataSource.getAllStatesPullRequests(repoToCheck)
                    Triple(repoToCheck, startAt, pulls)
                }
            }
        }
        val apiResponseResults = apiPullRequestResultsDeferred.awaitAll()
        appLogger.d("Synchronizer :: sync :: pulls :: fetch remote took ${(Clock.System.now() - prSyncStartAt).inWholeMilliseconds} ms")

        // map to sync results
        val syncResultEntries = apiResponseResults.map { (repoToCheck, startAt, apiResponseResult) ->
            apiResponseResult.toSyncResultEntry(
                syncResultId = syncResultId,
                repoToCheckId = repoToCheck.id,
                origin = SyncResultEntry.Origin.PULL,
                startAt = startAt
            )
        }
        // update the local pull requests
        val successfulApiResponseResults = apiResponseResults.filter { (_, _, apiResponseResult) ->
            apiResponseResult.isSuccess
        }
        val pullRequestsWithRepos = successfulApiResponseResults
            .flatMap { (_, _, apiResponseResult) ->
                requireNotNull(apiResponseResult.getOrNull()).data
            }
            .filterSyncValid(syncSettings = syncSettings)
        localDataSource.upsertPullRequests(pullRequestsWithRepos.map { it.pullRequest })
        localDataSource.removeReviewsByPullRequest(pullRequestsWithRepos.map { it.pullRequest.id })
        val reviews = pullRequestsWithRepos.map { it.reviews }.flatten()
        localDataSource.upsertReviews(reviews)
        localDataSource.removeReviewRequestsByPullRequest(pullRequestsWithRepos.map { it.pullRequest.id })
        val reviewRequests = pullRequestsWithRepos.map { it.reviewRequests }.flatten()
        localDataSource.upsertReviewRequests(reviewRequests)

        // remove pull requests non returned from remote
        val successfulRepoIds = successfulApiResponseResults.map { (repoToCheck, _, _) -> repoToCheck.id }.toSet()
        val pullRequestIdsToRemove = pullRequestsBefore
            .filter { it.pullRequest.repoToCheckId in successfulRepoIds }
            .map { it.pullRequest.id } - pullRequestsWithRepos.map { it.pullRequest.id }.toSet()
        localDataSource.removePullRequests(pullRequestIdsToRemove)
        cleanUp(syncSettings)

        // send the notifications
        val pullRequestsAfter = getAll().getOrDefault(listOf())
        appSettingsService.get().onSuccess {  appSettings ->
            sendNotifications(appSettings = appSettings, oldPullRequestsWithReviews = pullRequestsBefore, newPullRequestsWithReviews = pullRequestsAfter)
        }

        appLogger.d("Synchronizer :: sync :: pulls :: finish took ${(Clock.System.now() - prSyncStartAt).inWholeMilliseconds} ms")
        return syncResultEntries
    }

    override suspend fun cleanUp(syncSettings: SyncSettings) {
        getAll()
            .mapCatching { pullRequests ->
                pullRequests.filterNotSyncValid(syncSettings = syncSettings)
            }
            .mapCatching { pullRequests ->
                pullRequests.map { it.pullRequest.id }
            }
            .onSuccess { pullRequestIds ->
                localDataSource.removePullRequests(pullRequestIds)
            }
    }

    override suspend fun sendNotifications(appSettings: AppSettings, oldPullRequestsWithReviews: List<PullRequestWithRepoAndReviews>, newPullRequestsWithReviews: List<PullRequestWithRepoAndReviews>): Result<Unit> {
        sendStateNotifications(appSettings, oldPullRequestsWithReviews, newPullRequestsWithReviews)
        sendActivityNotifications(appSettings, oldPullRequestsWithReviews, newPullRequestsWithReviews)
        return Result.success(Unit)
    }

    override suspend fun sendStateNotifications(appSettings: AppSettings, oldPullRequestsWithReviews: List<PullRequestWithRepoAndReviews>, newPullRequestsWithReviews: List<PullRequestWithRepoAndReviews>): Result<Unit> {
        val settings = appSettings.notificationsSettings
        newPullRequestsWithReviews.forEach { pullRequestWithRepo ->
            val outcome = when {
                settings.stateEnabledOption == NotificationsSettings.EnabledOption.NONE -> "suppressed:global-disabled"
                !pullRequestWithRepo.repoToCheck.arePullRequestsNotificationsEnabled -> "suppressed:repository-disabled"
                pullRequestWithRepo.pullRequest.stateExtended == PullRequestStateExtended.UNKNOWN -> "suppressed:unknown-state"
                oldPullRequestsWithReviews.firstOrNull { it.pullRequest.id == pullRequestWithRepo.pullRequest.id }
                    ?.pullRequest?.stateExtended == pullRequestWithRepo.pullRequest.stateExtended -> "suppressed:state-unchanged"
                settings.stateEnabledOption == NotificationsSettings.EnabledOption.FILTERED &&
                    !pullRequestWithRepo.isStateNotificationEnabled(settings) -> "suppressed:state-filter-disabled"
                settings.stateEnabledOption == NotificationsSettings.EnabledOption.FILTERED &&
                    pullRequestWithRepo.pullRequest.author?.login?.trim() == settings.filterUsername.trim() -> "suppressed:author-filtered"
                else -> "dispatched"
            }
            logNotificationDecision("pull-request-state", pullRequestWithRepo, settings.stateEnabledOption, outcome)
            if (outcome == "dispatched") {
                notificationsSender.newPullRequest(pullRequestWithRepo.pullRequest)
            }
        }

        return Result.success(Unit)
    }

    override suspend fun sendActivityNotifications(appSettings: AppSettings, oldPullRequestsWithReviews: List<PullRequestWithRepoAndReviews>, newPullRequestsWithReviews: List<PullRequestWithRepoAndReviews>): Result<Unit> {
        appLogger.d("Synchronizer :: sync :: pulls :: send activity notification :: ${appSettings.notificationsSettings.activityEnabledOption} option selected")
        val settings = appSettings.notificationsSettings
        newPullRequestsWithReviews.forEach { pullRequestWithRepo ->
            val oldPullRequestWithRepo = oldPullRequestsWithReviews
                .firstOrNull { it.pullRequest.id == pullRequestWithRepo.pullRequest.id }

            val reviewOutcome = when {
                settings.activityEnabledOption == NotificationsSettings.EnabledOption.NONE -> "suppressed:global-disabled"
                !pullRequestWithRepo.repoToCheck.arePullRequestsNotificationsEnabled -> "suppressed:repository-disabled"
                oldPullRequestWithRepo == null -> "suppressed:new-pull-request"
                settings.activityEnabledOption == NotificationsSettings.EnabledOption.FILTERED &&
                    !settings.activityReviewsFromYourPullRequestsEnabled -> "suppressed:activity-filter-disabled"
                settings.activityEnabledOption == NotificationsSettings.EnabledOption.FILTERED &&
                    pullRequestWithRepo.pullRequest.author?.login?.trim() != settings.filterUsername.trim() -> "suppressed:author-filtered"
                else -> "dispatched"
            }
            pullRequestWithRepo.changedReviewsFrom(oldPullRequestWithRepo).forEach { review ->
                val outcome = if (review.isReRequestedReview()) "suppressed:re-review-requested" else reviewOutcome
                logActivityNotificationDecision("pull-request-review", pullRequestWithRepo, settings, outcome)
                if (outcome == "dispatched") {
                    notificationsSender.newPullRequestReview(pullRequestWithRepo.pullRequest, review)
                }
            }

            val dismissedReviewOutcome = when {
                settings.activityEnabledOption != NotificationsSettings.EnabledOption.FILTERED -> "suppressed:not-filtered-mode"
                !pullRequestWithRepo.repoToCheck.arePullRequestsNotificationsEnabled -> "suppressed:repository-disabled"
                !settings.activityReviewsFromYouDismissedEnabled -> "suppressed:activity-filter-disabled"
                oldPullRequestWithRepo == null -> "suppressed:new-pull-request"
                else -> "dispatched"
            }
            if (pullRequestWithRepo.hasDismissedOwnReview(oldPullRequestWithRepo, settings.filterUsername)) {
                logActivityNotificationDecision("pull-request-review-dismissed", pullRequestWithRepo, settings, dismissedReviewOutcome)
                if (dismissedReviewOutcome == "dispatched") {
                    notificationsSender.yourPullRequestReviewDismissed(pullRequestWithRepo.pullRequest)
                }
            }

            val checksOutcome = when {
                settings.activityEnabledOption == NotificationsSettings.EnabledOption.NONE -> "suppressed:global-disabled"
                !pullRequestWithRepo.repoToCheck.arePullRequestsNotificationsEnabled -> "suppressed:repository-disabled"
                oldPullRequestWithRepo == null -> "suppressed:new-pull-request"
                settings.activityEnabledOption == NotificationsSettings.EnabledOption.FILTERED &&
                    !settings.activityChecksFromYourPullRequestsEnabled -> "suppressed:activity-filter-disabled"
                settings.activityEnabledOption == NotificationsSettings.EnabledOption.FILTERED &&
                    pullRequestWithRepo.pullRequest.author?.login?.trim() != settings.filterUsername.trim() -> "suppressed:author-filtered"
                else -> "dispatched"
            }
            if (oldPullRequestWithRepo != null && oldPullRequestWithRepo.pullRequest.lastCommitCheckRollupStatus != pullRequestWithRepo.pullRequest.lastCommitCheckRollupStatus) {
                logActivityNotificationDecision("pull-request-checks", pullRequestWithRepo, settings, checksOutcome)
                if (checksOutcome == "dispatched") {
                    notificationsSender.pullRequestChecksChanged(pullRequestWithRepo.pullRequest)
                }
            }

            val mergeableOutcome = when {
                settings.activityEnabledOption == NotificationsSettings.EnabledOption.NONE -> "suppressed:global-disabled"
                !pullRequestWithRepo.repoToCheck.arePullRequestsNotificationsEnabled -> "suppressed:repository-disabled"
                oldPullRequestWithRepo == null -> "suppressed:new-pull-request"
                settings.activityEnabledOption == NotificationsSettings.EnabledOption.FILTERED &&
                    !settings.activityMergeableFromYourPullRequestsEnabled -> "suppressed:activity-filter-disabled"
                settings.activityEnabledOption == NotificationsSettings.EnabledOption.FILTERED &&
                    pullRequestWithRepo.pullRequest.author?.login?.trim() != settings.filterUsername.trim() -> "suppressed:author-filtered"
                else -> "dispatched"
            }
            if (oldPullRequestWithRepo != null &&
                oldPullRequestWithRepo.pullRequest.mergeStateStatus != pullRequestWithRepo.pullRequest.mergeStateStatus &&
                pullRequestWithRepo.pullRequest.canBeMerged
            ) {
                logActivityNotificationDecision("pull-request-mergeable", pullRequestWithRepo, settings, mergeableOutcome)
                if (mergeableOutcome == "dispatched") {
                    notificationsSender.mergeablePullRequest(pullRequestWithRepo.pullRequest)
                }
            }
        }

        return Result.success(Unit)
    }

    /**
     * Returns a list containing all pull requests that are also in the old list
     */
    private fun List<PullRequestWithRepoAndReviews>.filterNotNewPullRequests(oldPullRequestsWithReviews: List<PullRequestWithRepoAndReviews>): List<PullRequestWithRepoAndReviews> {
        return this
            .filter { newPullRequestWithRepo ->
                oldPullRequestsWithReviews.any { it.pullRequest.id == newPullRequestWithRepo.pullRequest.id }
            }
    }

    /**
     * Returns a list containing all pull requests that has reviews that changed the state
     * @return a list of pairs with the pull request and the reviews that changed
     */
    private fun List<PullRequestWithRepoAndReviews>.filterByReviewStateChanged(oldPullRequestsWithReviews: List<PullRequestWithRepoAndReviews>): List<Pair<PullRequest, List<Review>>> {
        return this
            .map { newPullRequestWithRepo ->
                // return a pair with the pull request and a list of the reviews that changed or are new
                val oldPullRequestWithRepo = oldPullRequestsWithReviews.firstOrNull { it.pullRequest.id == newPullRequestWithRepo.pullRequest.id }
                val oldReviews = oldPullRequestWithRepo?.reviews ?: listOf()
                val newReviews = newPullRequestWithRepo.reviews
                val reviewsChanged = newReviews.filter { newReview ->
                    val oldReview = oldReviews.firstOrNull { it.author?.login == newReview.author?.login }
                    if (oldReview != null) {
                        oldReview.state != newReview.state
                    } else {
                        true
                    }
                }
                newPullRequestWithRepo.pullRequest to reviewsChanged
            }
            .filter { (_, reviews) ->
                reviews.isNotEmpty()
            }
    }

    /**
     * Returns a list containing all pull requests that has the checks has changed
     */
    private fun List<PullRequestWithRepoAndReviews>.filterByPullRequestChecksChanged(oldPullRequestsWithReviews: List<PullRequestWithRepoAndReviews>): List<PullRequestWithRepoAndReviews> {
        return this
            .filter { newPullRequestWithRepo ->
                val oldPullRequestWithRepo = oldPullRequestsWithReviews.firstOrNull { it.pullRequest.id == newPullRequestWithRepo.pullRequest.id }
                if (oldPullRequestWithRepo != null) {
                    oldPullRequestWithRepo.pullRequest.lastCommitCheckRollupStatus != newPullRequestWithRepo.pullRequest.lastCommitCheckRollupStatus
                } else {
                    true
                }
            }
    }

    /**
     * Returns a list containing all pull requests that changed the [PullRequest.mergeStateStatus] and it can be merged
     */
    private fun List<PullRequestWithRepoAndReviews>.filterByPullRequestMergeableChangedToCanBeMerged(oldPullRequestsWithReviews: List<PullRequestWithRepoAndReviews>): List<PullRequestWithRepoAndReviews> {
        return this
            .filter { newPullRequestWithRepo ->
                val oldPullRequestWithRepo = oldPullRequestsWithReviews.firstOrNull { it.pullRequest.id == newPullRequestWithRepo.pullRequest.id }
                if (oldPullRequestWithRepo != null) {
                    val mergeStateStatusChanged = oldPullRequestWithRepo.pullRequest.mergeStateStatus != newPullRequestWithRepo.pullRequest.mergeStateStatus
                    mergeStateStatusChanged && newPullRequestWithRepo.pullRequest.canBeMerged
                } else {
                    true
                }
            }
    }

    /**
     * Returns a list containing all pull requests that have changed his state
     */
    private fun List<PullRequestWithRepoAndReviews>.filterByPullRequestStateChangedOrNew(oldPullRequestsWithReviews: List<PullRequestWithRepoAndReviews>): List<PullRequestWithRepoAndReviews> {
        return this
            .filter { newPullRequestWithRepo ->
                newPullRequestWithRepo.pullRequest.stateExtended != PullRequestStateExtended.UNKNOWN
            }
            .filter { newPullRequestWithRepo ->
                val oldPullRequestWithRepo = oldPullRequestsWithReviews.firstOrNull { it.pullRequest.id == newPullRequestWithRepo.pullRequest.id }
                if (oldPullRequestWithRepo != null) {
                    oldPullRequestWithRepo.pullRequest.stateExtended != newPullRequestWithRepo.pullRequest.stateExtended
                } else {
                    true
                }
            }
    }

    /**
     * Returns a list containing all pull requests where their status is enabled in the filtered notifications.
     * For example a pull request with [PullRequestStateExtended.OPEN] will be returned if the [NotificationsSettings.stateOpenFromOthersPullRequestsEnabled] is true
     */
    private fun List<PullRequestWithRepoAndReviews>.filterByPullRequestStateWithStateNotificationsEnabled(notificationsSettings: NotificationsSettings): List<PullRequestWithRepoAndReviews> {
        return this
            .filter { newPullRequestWithRepo ->
                val notificationsEnabled = when(newPullRequestWithRepo.pullRequest.stateExtended) {
                    PullRequestStateExtended.UNKNOWN -> false
                    PullRequestStateExtended.OPEN -> notificationsSettings.stateOpenFromOthersPullRequestsEnabled
                    PullRequestStateExtended.CLOSED -> notificationsSettings.stateClosedFromOthersPullRequestsEnabled
                    PullRequestStateExtended.MERGED -> notificationsSettings.stateMergedFromOthersPullRequestsEnabled
                    PullRequestStateExtended.DRAFT -> notificationsSettings.stateDraftFromOthersPullRequestsEnabled
                }
                notificationsEnabled
            }
    }

    /**
     * Returns a list containing all pull requests that have the notification enabled
     */
    private fun List<PullRequestWithRepoAndReviews>.filterByPullRequestNotificationsEnabled(): List<PullRequestWithRepoAndReviews> {
        return this
            .filter { newPullRequestWithRepo ->
                newPullRequestWithRepo.repoToCheck.arePullRequestsNotificationsEnabled
            }
    }

    private fun PullRequestWithRepoAndReviews.isStateNotificationEnabled(notificationsSettings: NotificationsSettings): Boolean {
        return when (pullRequest.stateExtended) {
            PullRequestStateExtended.UNKNOWN -> false
            PullRequestStateExtended.OPEN -> notificationsSettings.stateOpenFromOthersPullRequestsEnabled
            PullRequestStateExtended.CLOSED -> notificationsSettings.stateClosedFromOthersPullRequestsEnabled
            PullRequestStateExtended.MERGED -> notificationsSettings.stateMergedFromOthersPullRequestsEnabled
            PullRequestStateExtended.DRAFT -> notificationsSettings.stateDraftFromOthersPullRequestsEnabled
        }
    }

    private fun PullRequestWithRepoAndReviews.changedReviewsFrom(oldPullRequestWithRepo: PullRequestWithRepoAndReviews?): List<Review> {
        val oldReviews = oldPullRequestWithRepo?.reviews ?: return listOf()
        return reviews.filter { newReview ->
            val oldReview = oldReviews.firstOrNull { it.author?.login == newReview.author?.login }
            oldReview == null || oldReview.state != newReview.state
        }
    }

    private fun PullRequestWithRepoAndReviews.hasDismissedOwnReview(
        oldPullRequestWithRepo: PullRequestWithRepoAndReviews?,
        filterUsername: String,
    ): Boolean {
        val oldReview = oldPullRequestWithRepo?.reviews
            ?.firstOrNull { it.author?.login?.trim() == filterUsername.trim() }
            ?: return false
        val newReview = reviews.firstOrNull { it.author?.login?.trim() == filterUsername.trim() }
        return newReview == null || (oldReview.state != newReview.state && newReview.state == ReviewState.DISMISSED)
    }

    private fun logNotificationDecision(
        eventClass: String,
        pullRequestWithRepo: PullRequestWithRepoAndReviews,
        globalSetting: NotificationsSettings.EnabledOption,
        outcome: String,
    ) {
        val repository = pullRequestWithRepo.repoToCheck.repository
        appLogger.d(
            "Notification :: decision :: event=$eventClass :: repository=${repository?.owner}/${repository?.name} " +
                ":: global=$globalSetting :: repositoryEnabled=${pullRequestWithRepo.repoToCheck.arePullRequestsNotificationsEnabled} :: outcome=$outcome"
        )
    }

    private fun logActivityNotificationDecision(
        eventClass: String,
        pullRequestWithRepo: PullRequestWithRepoAndReviews,
        settings: NotificationsSettings,
        outcome: String,
    ) {
        val repository = pullRequestWithRepo.repoToCheck.repository
        appLogger.d(
            "Notification :: decision :: event=$eventClass :: repository=${repository?.owner}/${repository?.name} " +
                ":: global=${settings.activityEnabledOption} " +
                ":: repositoryEnabled=${pullRequestWithRepo.repoToCheck.arePullRequestsNotificationsEnabled} :: outcome=$outcome"
        )
    }
}