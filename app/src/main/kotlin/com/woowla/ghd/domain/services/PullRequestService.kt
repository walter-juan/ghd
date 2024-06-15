package com.woowla.ghd.domain.services

import com.woowla.ghd.AppLogger
import com.woowla.ghd.data.local.LocalDataSource
import com.woowla.ghd.data.remote.RemoteDataSource
import com.woowla.ghd.domain.entities.AppSettings
import com.woowla.ghd.domain.entities.RepoToCheck
import com.woowla.ghd.domain.entities.SyncResultEntry
import com.woowla.ghd.domain.entities.SyncSettings
import com.woowla.ghd.data.remote.mappers.toPullRequest
import com.woowla.ghd.domain.entities.PullRequestWithRepoAndReviews
import com.woowla.ghd.domain.entities.filterNotSyncValid
import com.woowla.ghd.domain.entities.filterSyncValid
import com.woowla.ghd.domain.mappers.toSyncResultEntry
import com.woowla.ghd.domain.synchronization.SynchronizableService
import com.woowla.ghd.notifications.NotificationsSender
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class PullRequestService(
    private val localDataSource: LocalDataSource = LocalDataSource(),
    private val remoteDataSource: RemoteDataSource = RemoteDataSource(),
    private val notificationsSender: NotificationsSender = NotificationsSender(),
    private val appSettingsService: AppSettingsService = AppSettingsService(),
) : SynchronizableService {
    suspend fun getAll(): Result<List<PullRequestWithRepoAndReviews>> {
        return localDataSource.getAllPullRequests()
            .mapCatching { pullRequests ->
                pullRequests.sorted()
            }
    }

    suspend fun markAsSeen(id: String, appSeenAt: Instant?): Result<Unit> {
        return localDataSource.updateAppSeenAt(id = id, appSeenAt = appSeenAt)
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
                    val appSeenAt = localDataSource.getPullRequest(apiPullRequest.id).getOrNull()?.pullRequest?.appSeenAt
                    apiPullRequest.toPullRequest(repoToCheck = repoToCheck, appSeenAt = appSeenAt)
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
        val oldPullRequestIds = oldPullRequestsWithReviews.map { it.pullRequest.id }

        // notification for a new pull requests
        if (appSettings.newPullRequestsNotificationsEnabled) {
            newPullRequestsWithReviews
                .filterNot {
                    oldPullRequestIds.contains(it.pullRequest.id)
                }
                .forEach {
                    notificationsSender.newPullRequest(it.pullRequest)
                }
        }

        // notification for an update
        if (appSettings.updatedPullRequestsNotificationsEnabled) {
            newPullRequestsWithReviews
                .filter {
                    !it.pullRequest.appSeen
                }
                .filter { newPull ->
                    val oldRelease = oldPullRequestsWithReviews.firstOrNull { it.pullRequest.id == newPull.pullRequest.id }

                    if (oldRelease != null) {
                        oldRelease.pullRequest.updatedAt != newPull.pullRequest.updatedAt
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
}