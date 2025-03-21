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
import kotlinx.datetime.Clock

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
        val pullRequestsWithRepos = apiResponseResults
            .map { (repoToCheck, _, apiResponseResult) ->
                apiResponseResult.getOrNull()?.data ?: listOf()
            }
            .flatten()
            .filterSyncValid(syncSettings = syncSettings)
        localDataSource.upsertPullRequests(pullRequestsWithRepos.map { it.pullRequest })
        localDataSource.removeReviewsByPullRequest(pullRequestsWithRepos.map { it.pullRequest.id })
        val reviews = pullRequestsWithRepos.map { it.reviews }.flatten()
        localDataSource.upsertReviews(reviews)

        // remove pull requests non returned from remote
        val pullRequestIdsToRemove = pullRequestsBefore.map { it.pullRequest.id } - pullRequestsWithRepos.map { it.pullRequest.id }.toSet()
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
        appLogger.d("Synchronizer :: sync :: pulls :: send state notification :: ${appSettings.notificationsSettings.stateEnabledOption} option selected")
        when (appSettings.notificationsSettings.stateEnabledOption) {
            NotificationsSettings.EnabledOption.NONE -> {
                // nothing to do
            }
            NotificationsSettings.EnabledOption.ALL -> {
                // state changes
                newPullRequestsWithReviews
                    .filterByPullRequestNotificationsEnabled()
                    .filterByPullRequestStateChangedOrNew(oldPullRequestsWithReviews)
                    .forEach { pullRequestWithRepo ->
                        appLogger.d("Synchronizer :: sync :: pulls :: send state notification :: send new pull request notification, pull id ${pullRequestWithRepo.pullRequest.id}")
                        notificationsSender.newPullRequest(pullRequestWithRepo.pullRequest)
                    }
            }
            NotificationsSettings.EnabledOption.FILTERED -> {
                // state changes, from others pull requests
                newPullRequestsWithReviews
                    .filterByPullRequestNotificationsEnabled()
                    .filterByPullRequestStateWithStateNotificationsEnabled(appSettings.notificationsSettings)
                    .filterByPullRequestStateChangedOrNew(oldPullRequestsWithReviews)
                    .filter { newPullRequestWithRepo ->
                        newPullRequestWithRepo.pullRequest.author?.login?.trim() != appSettings.notificationsSettings.filterUsername.trim()
                    }
                    .forEach { pullRequestWithRepo ->
                        appLogger.d("Synchronizer :: sync :: pulls :: send state notification :: send new pull request notification, pull id ${pullRequestWithRepo.pullRequest.id}")
                        notificationsSender.newPullRequest(pullRequestWithRepo.pullRequest)
                    }
            }
        }

        return Result.success(Unit)
    }

    override suspend fun sendActivityNotifications(appSettings: AppSettings, oldPullRequestsWithReviews: List<PullRequestWithRepoAndReviews>, newPullRequestsWithReviews: List<PullRequestWithRepoAndReviews>): Result<Unit> {
        appLogger.d("Synchronizer :: sync :: pulls :: send activity notification :: ${appSettings.notificationsSettings.activityEnabledOption} option selected")
        when (appSettings.notificationsSettings.activityEnabledOption) {
            NotificationsSettings.EnabledOption.NONE -> {
                // nothing to do
            }
            NotificationsSettings.EnabledOption.ALL -> {
                // new reviews or changed
                newPullRequestsWithReviews
                    .filterByPullRequestNotificationsEnabled()
                    .filterNotNewPullRequests(oldPullRequestsWithReviews)
                    .filterByReviewStateChanged(oldPullRequestsWithReviews)
                    .map { (pullRequest, reviews) ->
                        pullRequest to reviews.filter { !it.reRequestedReview() }
                    }
                    .filter { (_, reviews) ->
                        reviews.isNotEmpty()
                    }
                    .forEach { (pullRequest, reviews) ->
                        reviews.forEach { review ->
                            appLogger.d("Synchronizer :: sync :: pulls :: send activity notification :: send new review notification, pull id ${pullRequest.id}, review id ${review.id}, review state ${review.state}")
                            notificationsSender.newPullRequestReview(pullRequest, review)
                        }
                    }

                // checks
                newPullRequestsWithReviews
                    .filterByPullRequestNotificationsEnabled()
                    .filterNotNewPullRequests(oldPullRequestsWithReviews)
                    .filterByPullRequestChecksChanged(oldPullRequestsWithReviews)
                    .forEach { pullRequestWithRepo ->
                        appLogger.d("Synchronizer :: sync :: pulls :: send activity notification :: send checks notification, pull id ${pullRequestWithRepo.pullRequest.id}")
                        notificationsSender.pullRequestChecksChanged(pullRequestWithRepo.pullRequest)
                    }
                // mergeable
                newPullRequestsWithReviews
                    .filterByPullRequestNotificationsEnabled()
                    .filterNotNewPullRequests(oldPullRequestsWithReviews)
                    .filterByPullRequestMergeableChangedToCanBeMerged(oldPullRequestsWithReviews)
                    .forEach { pullRequestWithRepo ->
                        appLogger.d("Synchronizer :: sync :: pulls :: send activity notification :: send mergeable notification, pull id ${pullRequestWithRepo.pullRequest.id}")
                        notificationsSender.mergeablePullRequest(pullRequestWithRepo.pullRequest)
                    }
            }
            NotificationsSettings.EnabledOption.FILTERED -> {
                // new reviews or changed, from your pull requests
                newPullRequestsWithReviews
                    .filterByPullRequestNotificationsEnabled()
                    .filter { appSettings.notificationsSettings.activityReviewsFromYourPullRequestsEnabled }
                    .filterNotNewPullRequests(oldPullRequestsWithReviews)
                    .filterByReviewStateChanged(oldPullRequestsWithReviews)
                    .map { (pullRequest, reviews) ->
                        pullRequest to reviews.filter { !it.reRequestedReview() }
                    }
                    .filter { (_, reviews) ->
                        reviews.isNotEmpty()
                    }
                    .filter { (pullRequest, _) ->
                        pullRequest.author?.login?.trim() == appSettings.notificationsSettings.filterUsername.trim()
                    }
                    .forEach { (pullRequest, reviews) ->
                        reviews.forEach { review ->
                            appLogger.d("Synchronizer :: sync :: pulls :: send activity notification :: send new review notification, pull id ${pullRequest.id}, review id ${review.id}, review state ${review.state}")
                            notificationsSender.newPullRequestReview(pullRequest, review)
                        }
                    }

                // your review changed
                newPullRequestsWithReviews
                    .filterByPullRequestNotificationsEnabled()
                    .filter { appSettings.notificationsSettings.activityReviewsFromYouDismissedEnabled }
                    .filterNotNewPullRequests(oldPullRequestsWithReviews)
                    .filter { pullRequestWithRepo ->
                        val oldReview = oldPullRequestsWithReviews
                            .firstOrNull { it.pullRequest.id == pullRequestWithRepo.pullRequest.id }
                            ?.reviews
                            ?.firstOrNull { it.author?.login?.trim() == appSettings.notificationsSettings.filterUsername.trim() }
                        val newReview = pullRequestWithRepo
                            .reviews
                            .firstOrNull { it.author?.login?.trim() == appSettings.notificationsSettings.filterUsername.trim() }

                        if (oldReview != null) {
                            when {
                                newReview == null -> {
                                    // review deleted
                                    appLogger.d("Synchronizer :: sync :: pulls :: send activity notification :: send your review deleted notification, pull id ${pullRequestWithRepo.pullRequest.id}")
                                    notificationsSender.yourPullRequestReviewDismissed(pullRequestWithRepo.pullRequest)
                                    true
                                }
                                oldReview.state != newReview.state && newReview.state == ReviewState.DISMISSED -> {
                                    // review state changed
                                    appLogger.d("Synchronizer :: sync :: pulls :: send activity notification :: send your review state changed notification, pull id ${pullRequestWithRepo.pullRequest.id}")
                                    notificationsSender.yourPullRequestReviewDismissed(pullRequestWithRepo.pullRequest)
                                    true
                                }
                                else -> {
                                    false
                                }
                            }
                        } else {
                            false
                        }
                    }
                // checks, from your pull requests
                newPullRequestsWithReviews
                    .filterByPullRequestNotificationsEnabled()
                    .filter { appSettings.notificationsSettings.activityChecksFromYourPullRequestsEnabled }
                    .filterNotNewPullRequests(oldPullRequestsWithReviews)
                    .filterByPullRequestChecksChanged(oldPullRequestsWithReviews)
                    .filter { newPullRequestWithRepo ->
                        newPullRequestWithRepo.pullRequest.author?.login?.trim() == appSettings.notificationsSettings.filterUsername.trim()
                    }
                    .forEach { pullRequestWithRepo ->
                        appLogger.d("Synchronizer :: sync :: pulls :: send activity notification :: send checks notification, pull id ${pullRequestWithRepo.pullRequest.id}")
                        notificationsSender.pullRequestChecksChanged(pullRequestWithRepo.pullRequest)
                    }
                // mergeable, from your pull requests
                newPullRequestsWithReviews
                    .filterByPullRequestNotificationsEnabled()
                    .filter { appSettings.notificationsSettings.activityMergeableFromYourPullRequestsEnabled }
                    .filterNotNewPullRequests(oldPullRequestsWithReviews)
                    .filterByPullRequestMergeableChangedToCanBeMerged(oldPullRequestsWithReviews)
                    .filter { newPullRequestWithRepo ->
                        newPullRequestWithRepo.pullRequest.author?.login?.trim() == appSettings.notificationsSettings.filterUsername.trim()
                    }
                    .forEach { pullRequestWithRepo ->
                        appLogger.d("Synchronizer :: sync :: pulls :: send activity notification :: send mergeable notification, pull id ${pullRequestWithRepo.pullRequest.id}")
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
}