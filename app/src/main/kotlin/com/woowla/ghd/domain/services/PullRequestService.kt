package com.woowla.ghd.domain.services

import com.woowla.ghd.AppLogger
import com.woowla.ghd.data.local.LocalDataSource
import com.woowla.ghd.data.remote.RemoteDataSource
import com.woowla.ghd.domain.entities.AppSettings
import com.woowla.ghd.domain.entities.RepoToCheck
import com.woowla.ghd.domain.entities.SyncResultEntry
import com.woowla.ghd.domain.entities.SyncSettings
import com.woowla.ghd.data.remote.mappers.toPullRequest
import com.woowla.ghd.domain.entities.PullRequest
import com.woowla.ghd.domain.entities.PullRequestStateExtended
import com.woowla.ghd.domain.entities.PullRequestWithRepoAndReviews
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
        val apiPullRequestResults = apiPullRequestResultsDeferred.awaitAll()
        AppLogger.d("Synchronizer :: sync :: pulls :: fetch remote took ${(Clock.System.now() - prSyncStartAt).inWholeMilliseconds} ms")

        // map to sync results
        val syncResultEntries = apiPullRequestResults.map { (repoToCheck, startAt, pullRequestResults) ->
            pullRequestResults.toSyncResultEntry(
                syncResultId = syncResultId,
                repoToCheckId = repoToCheck.id,
                origin = SyncResultEntry.Origin.PULL,
                startAt = startAt
            )
        }
        // update the local pull requests
        val pullRequests = apiPullRequestResults.mapNotNull { (repoToCheck, _, pullRequestResults) ->
            val apiPullRequests = pullRequestResults.getOrElse { listOf() }
            if (apiPullRequests.isEmpty()) {
                null
            } else {
                val pullRequests = apiPullRequests.map { apiPullRequest ->
                    val asdf = localDataSource.getPullRequest(apiPullRequest.id).getOrNull()
                    val pullRequestSeen = asdf?.pullRequestSeen
                    val reviewSeen = asdf?.reviewsSeen ?: listOf()
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
        // notification for state change
        val pullRequestsWithStateChange = newPullRequestsWithReviews
            .filter { newPull ->
                val oldRelease = oldPullRequestsWithReviews.firstOrNull { it.pullRequest.id == newPull.pullRequest.id }
                if (oldRelease != null) {
                    shouldNotifyPullRequestStateChange(appSettings, oldRelease.pullRequest, newPull.pullRequest)
                } else {
                    shouldNotifyPullRequestStateChange(appSettings, newPull.pullRequest)
                }
            }
        pullRequestsWithStateChange.forEach { notificationsSender.newPullRequest(it.pullRequest) }

        // notification for activity change
        if (appSettings.pullRequestActivityNotificationsEnabled) {
            val nonNotified = newPullRequestsWithReviews - pullRequestsWithStateChange
            nonNotified
                .filter { newPull ->
                    val oldRelease = oldPullRequestsWithReviews.firstOrNull { it.pullRequest.id == newPull.pullRequest.id }
                    if (oldRelease != null) {
                        shouldNotifyPullRequestActivityChange(appSettings, oldRelease, newPull)
                    } else {
                        false
                    }
                }
                .forEach { newPull ->
                    notificationsSender.updatePullRequest(newPull.pullRequest)
                }
        }

        return Result.success(Unit)
    }

    private fun shouldNotifyPullRequestStateChange(appSettings: AppSettings, pullRequest: PullRequest): Boolean {
        val validState = appSettings.pullRequestNotificationsFilterOptions.let {
            when (pullRequest.stateExtended) {
                PullRequestStateExtended.OPEN -> it.open
                PullRequestStateExtended.CLOSED -> it.closed
                PullRequestStateExtended.MERGED -> it.merged
                PullRequestStateExtended.DRAFT -> it.draft
                PullRequestStateExtended.UNKNOWN -> false
            }
        }
        return appSettings.pullRequestStateNotificationsEnabled && validState
    }

    private fun shouldNotifyPullRequestStateChange(appSettings: AppSettings, oldPullRequest: PullRequest, newPullRequest: PullRequest): Boolean {
        val stateChanged = oldPullRequest.state != newPullRequest.state
        val validState = appSettings.pullRequestNotificationsFilterOptions.let {
            when (newPullRequest.stateExtended) {
                PullRequestStateExtended.OPEN -> it.open
                PullRequestStateExtended.CLOSED -> it.closed
                PullRequestStateExtended.MERGED -> it.merged
                PullRequestStateExtended.DRAFT -> it.draft
                PullRequestStateExtended.UNKNOWN -> false
            }
        }
        return appSettings.pullRequestStateNotificationsEnabled && stateChanged && validState
    }

    private fun shouldNotifyPullRequestActivityChange(appSettings: AppSettings, oldPullRequest: PullRequestWithRepoAndReviews, newPullRequest: PullRequestWithRepoAndReviews): Boolean {
        val updated = oldPullRequest.pullRequest.updatedAt != newPullRequest.pullRequest.updatedAt
        return appSettings.pullRequestActivityNotificationsEnabled && updated
    }
}