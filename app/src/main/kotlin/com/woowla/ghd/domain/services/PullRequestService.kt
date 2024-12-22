package com.woowla.ghd.domain.services

import com.woowla.ghd.AppLogger
import com.woowla.ghd.data.local.LocalDataSource
import com.woowla.ghd.data.remote.RemoteDataSource
import com.woowla.ghd.domain.entities.AppSettings
import com.woowla.ghd.domain.entities.RepoToCheck
import com.woowla.ghd.domain.entities.SyncResultEntry
import com.woowla.ghd.domain.entities.SyncSettings
import com.woowla.ghd.data.remote.mappers.toPullRequest
import com.woowla.ghd.domain.entities.NotificationsSettings
import com.woowla.ghd.domain.entities.PullRequest
import com.woowla.ghd.domain.entities.PullRequestStateExtended
import com.woowla.ghd.domain.entities.PullRequestWithRepoAndReviews
import com.woowla.ghd.domain.entities.Review
import com.woowla.ghd.domain.entities.filterNotSyncValid
import com.woowla.ghd.domain.entities.filterSyncValid
import com.woowla.ghd.domain.mappers.toPullRequestSeen
import com.woowla.ghd.domain.mappers.toReviewSeen
import com.woowla.ghd.domain.mappers.toSyncResultEntry
import com.woowla.ghd.domain.synchronization.SynchronizableService
import com.woowla.ghd.notifications.NotificationsSender
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.datetime.Clock

class PullRequestService(
    private val localDataSource: LocalDataSource = LocalDataSource(),
    private val remoteDataSource: RemoteDataSource = RemoteDataSource(),
    private val notificationsSender: NotificationsSender = NotificationsSender.getInstance(),
    private val appSettingsService: AppSettingsService = AppSettingsService(),
) : SynchronizableService {
    suspend fun getAll(): Result<List<PullRequestWithRepoAndReviews>> {
        return localDataSource.getAllPullRequests()
            .mapCatching { pullRequests ->
                pullRequests.sorted()
            }
    }

    suspend fun unmarkAsSeen(id: String): Result<Unit> {
        return localDataSource.removePullRequestSeen(id)
    }

    suspend fun markAsSeen(id: String): Result<Unit> {
        val now = Clock.System.now()
        return localDataSource
            .getPullRequest(id)
            .onSuccess { pr ->
                localDataSource.upsertPullRequestSeen(pr.pullRequest.toPullRequestSeen(now))
            }
            .onSuccess { pr ->
                localDataSource.upsertReviewsSeen(pr.reviews.map { it.toReviewSeen() })
            }
            .map { value -> Unit }
    }

    override suspend fun synchronize(syncResultId: Long, syncSettings: SyncSettings, repoToCheckList: List<RepoToCheck>): List<SyncResultEntry> {
        return newSync(syncResultId, syncSettings, repoToCheckList)
    }

    suspend fun newSync(syncResultId: Long, syncSettings: SyncSettings, repoToCheckList: List<RepoToCheck>): List<SyncResultEntry> {
        AppLogger.d("Synchronizer :: sync :: pulls :: start")
        val prSyncStartAt = Clock.System.now()
        val pullRequestsBefore = getAll().getOrDefault(listOf())
        val enabledRepoToCheckList = repoToCheckList.filter { it.arePullRequestsEnabled }

        // fetch all remote pull requests
        val apiPullRequestResultsDeferred = coroutineScope {
            enabledRepoToCheckList.map { repoToCheck ->
                val startAt = Clock.System.now()
                async {
                    val pulls = remoteDataSource.getAllStatesPullRequests(owner = repoToCheck.owner, repo = repoToCheck.name)
                    Triple(repoToCheck, startAt, pulls)
                }
            }
        }
        val apiResponseResults = apiPullRequestResultsDeferred.awaitAll()
        AppLogger.d("Synchronizer :: sync :: pulls :: fetch remote took ${(Clock.System.now() - prSyncStartAt).inWholeMilliseconds} ms")

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
        val pullRequests = apiResponseResults.mapNotNull { (repoToCheck, _, apiResponseResult) ->
            val apiResponse = apiResponseResult.getOrNull()
            val apiPullRequests = apiResponse?.data ?: listOf()
            if (apiPullRequests.isEmpty()) {
                null
            } else {
                val pullRequests = apiPullRequests.map { apiPullRequest ->
                    val pullRequestWithRepos = localDataSource.getPullRequest(apiPullRequest.id).getOrNull()
                    val pullRequestSeen = pullRequestWithRepos?.pullRequestSeen
                    val reviewSeen = pullRequestWithRepos?.reviewsSeen ?: listOf()
                    apiPullRequest.toPullRequest(repoToCheck = repoToCheck, pullRequestSeen = pullRequestSeen, reviewsSeen = reviewSeen)
                }
                pullRequests
            }
        }

        val validPullRequests = pullRequests.map { pullRequestsWithRepoAndReviews ->
            pullRequestsWithRepoAndReviews.filterSyncValid(syncSettings = syncSettings)
        }
        validPullRequests.map { pullRequestsWithRepoAndReviews ->
            localDataSource.upsertPullRequests(pullRequestsWithRepoAndReviews.map { it.pullRequest })
            localDataSource.removeReviewsByPullRequest(pullRequestsWithRepoAndReviews.map { it.pullRequest.id })
            val reviews = pullRequestsWithRepoAndReviews.map { it.reviews }.flatten()
            localDataSource.upsertReviews(reviews)
        }

        // remove pull requests non returned from remote
        val pullRequestIdsToRemove = pullRequestsBefore.map { it.pullRequest.id } - validPullRequests.flatten().map { it.pullRequest.id }.toSet()
        localDataSource.removePullRequests(pullRequestIdsToRemove)

        // send the notifications
        val pullRequestsAfter = getAll().getOrDefault(listOf())
        appSettingsService.get().onSuccess {  appSettings ->
            sendNotifications(appSettings = appSettings, oldPullRequestsWithReviews = pullRequestsBefore, newPullRequestsWithReviews = pullRequestsAfter)
        }

        AppLogger.d("Synchronizer :: sync :: pulls :: finish took ${(Clock.System.now() - prSyncStartAt).inWholeMilliseconds} ms")
        return syncResultEntries
    }

    suspend fun cleanUp(syncSettings: SyncSettings) {
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

    suspend fun sendNotifications(appSettings: AppSettings, oldPullRequestsWithReviews: List<PullRequestWithRepoAndReviews>, newPullRequestsWithReviews: List<PullRequestWithRepoAndReviews>): Result<Unit> {
        sendStateNotifications(appSettings, oldPullRequestsWithReviews, newPullRequestsWithReviews)
        sendActivityNotifications(appSettings, oldPullRequestsWithReviews, newPullRequestsWithReviews)
        return Result.success(Unit)
    }

    suspend fun sendStateNotifications(appSettings: AppSettings, oldPullRequestsWithReviews: List<PullRequestWithRepoAndReviews>, newPullRequestsWithReviews: List<PullRequestWithRepoAndReviews>): Result<Unit> {
        when(appSettings.notificationsSettings.stateEnabledOption) {
            NotificationsSettings.EnabledOption.NONE -> {
                // nothing to do
            }
            NotificationsSettings.EnabledOption.ALL -> {
                // state changes
                newPullRequestsWithReviews
                    .filterByPullRequestNotificationsEnabled()
                    .filterByPullRequestStateChangedOrNew(oldPullRequestsWithReviews)
                    .forEach { pullRequestWithRepo ->
                        notificationsSender.newPullRequest(pullRequestWithRepo.pullRequest)
                    }
            }
            NotificationsSettings.EnabledOption.FILTERED -> {
                // state changes, from others pull requests
                newPullRequestsWithReviews
                    .filterByPullRequestNotificationsEnabled()
                    .filterByPullRequestStateChangedOrNew(oldPullRequestsWithReviews)
                    .filter { newPullRequestWithRepo ->
                        newPullRequestWithRepo.pullRequest.author?.login?.trim() != appSettings.notificationsSettings.filterUsername.trim()
                    }
                    .forEach { pullRequestWithRepo ->
                        notificationsSender.newPullRequest(pullRequestWithRepo.pullRequest)
                    }
            }
        }

        return Result.success(Unit)
    }

    suspend fun sendActivityNotifications(appSettings: AppSettings, oldPullRequestsWithReviews: List<PullRequestWithRepoAndReviews>, newPullRequestsWithReviews: List<PullRequestWithRepoAndReviews>): Result<Unit> {
        when(appSettings.notificationsSettings.activityEnabledOption) {
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
                            notificationsSender.newPullRequestReview(pullRequest, review)
                        }
                    }
                // re-reviews
                // TODO [review re-request] disabled
//                newPullRequestsWithReviews
//                    .filterNotNewPullRequests(oldPullRequestsWithReviews)
//                    .filterByReviewStateChanged(oldPullRequestsWithReviews)
//                    .map { (pullRequest, reviews) ->
//                        pullRequest to reviews.filter { it.reRequestedReview() }
//                    }
//                    .filter { (_, reviews) ->
//                        reviews.isNotEmpty()
//                    }
//                    .forEach { (pullRequest, _) ->
//                        notificationsSender.newPullRequestReReview(pullRequest)
//                    }
                // checks
                newPullRequestsWithReviews
                    .filterByPullRequestNotificationsEnabled()
                    .filterNotNewPullRequests(oldPullRequestsWithReviews)
                    .filterByPullRequestChecksChanged(oldPullRequestsWithReviews)
                    .forEach { pullRequestWithRepo ->
                        notificationsSender.changePullRequestChecks(pullRequestWithRepo.pullRequest)
                    }
                // mergeable
                newPullRequestsWithReviews
                    .filterByPullRequestNotificationsEnabled()
                    .filterNotNewPullRequests(oldPullRequestsWithReviews)
                    .filterByPullRequestMergeableChangedToCanBeMerged(oldPullRequestsWithReviews)
                    .forEach { pullRequestWithRepo ->
                        notificationsSender.mergeablePullRequest(pullRequestWithRepo.pullRequest)
                    }
            }
            NotificationsSettings.EnabledOption.FILTERED -> {
                // new reviews or changed, from your pull requests
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
                    .filter { (pullRequest, _) ->
                        pullRequest.author?.login?.trim() == appSettings.notificationsSettings.filterUsername.trim()
                    }
                    .forEach { (pullRequest, reviews) ->
                        reviews.forEach { review ->
                            notificationsSender.newPullRequestReview(pullRequest, review)
                        }
                    }
                // re-reviews, from your reviews
                // TODO [review re-request]
//                newPullRequestsWithReviews
//                    .filterNotNewPullRequests(oldPullRequestsWithReviews)
//                    .filterByReviewStateChanged(oldPullRequestsWithReviews)
//                    .map { (pullRequest, reviews) ->
//                        pullRequest to reviews.filter { it.reRequestedReview() }
//                    }
//                    .map { (pullRequest, reviews) ->
//                        val yourReviews = reviews.filter { it.author?.login?.trim() == appSettings.notificationsSettings.filterUsername.trim() }
//                        pullRequest to yourReviews
//                    }
//                    .filter { (_, reviews) ->
//                        reviews.isNotEmpty()
//                    }
//                    .forEach { (pullRequest, _) ->
//                        notificationsSender.newPullRequestReReview(pullRequest)
//                    }
                // checks, from your pull requests
                newPullRequestsWithReviews
                    .filterByPullRequestNotificationsEnabled()
                    .filterNotNewPullRequests(oldPullRequestsWithReviews)
                    .filterByPullRequestChecksChanged(oldPullRequestsWithReviews)
                    .filter { newPullRequestWithRepo ->
                        newPullRequestWithRepo.pullRequest.author?.login?.trim() == appSettings.notificationsSettings.filterUsername.trim()
                    }
                    .forEach { pullRequestWithRepo ->
                        notificationsSender.changePullRequestChecks(pullRequestWithRepo.pullRequest)
                    }
                // mergeable, from your pull requests
                newPullRequestsWithReviews
                    .filterByPullRequestNotificationsEnabled()
                    .filterNotNewPullRequests(oldPullRequestsWithReviews)
                    .filterByPullRequestMergeableChangedToCanBeMerged(oldPullRequestsWithReviews)
                    .filter { newPullRequestWithRepo ->
                        newPullRequestWithRepo.pullRequest.author?.login?.trim() == appSettings.notificationsSettings.filterUsername.trim()
                    }
                    .forEach { pullRequestWithRepo ->
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
     * Returns a list containing all pull requests that have the notification enabled
     */
    private fun List<PullRequestWithRepoAndReviews>.filterByPullRequestNotificationsEnabled(): List<PullRequestWithRepoAndReviews> {
        return this
            .filter { newPullRequestWithRepo ->
                newPullRequestWithRepo.repoToCheck.arePullRequestsNotificationsEnabled
            }
    }
}