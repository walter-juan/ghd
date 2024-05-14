package com.woowla.ghd.domain.services

import com.woowla.ghd.data.local.LocalDataSource
import com.woowla.ghd.data.remote.RemoteDataSource
import com.woowla.ghd.domain.entities.AppSettings
import com.woowla.ghd.data.remote.type.PullRequestState as ApiPullRequestState
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
        val pullRequestsBefore = getAll().getOrDefault(listOf())
        val enabledRepoToCheckList = repoToCheckList.filter { it.arePullRequestsEnabled }

        val syncApiResults = coroutineScope {
            val fetchOpenPullRequests = enabledRepoToCheckList.map { repoToCheck ->
                async { fetchPullRequests(syncSettings, syncResultId, repoToCheck, ApiPullRequestState.OPEN) }
            }
            val fetchMergedPullRequests = enabledRepoToCheckList.map { repoToCheck ->
                async { fetchPullRequests(syncSettings, syncResultId, repoToCheck, ApiPullRequestState.MERGED) }
            }
            val fetchClosedPullRequests = enabledRepoToCheckList.map { repoToCheck ->
                async { fetchPullRequests(syncSettings, syncResultId, repoToCheck, ApiPullRequestState.CLOSED) }
            }

            val openSyncApiResults = fetchOpenPullRequests.awaitAll()
            val mergedSyncApiResults = fetchMergedPullRequests.awaitAll()
            val closedSyncApiResults = fetchClosedPullRequests.awaitAll()

            cleanUp(syncSettings)

            openSyncApiResults + mergedSyncApiResults + closedSyncApiResults
        }

        val pullRequestsAfter = getAll().getOrDefault(listOf())
        appSettingsService.get().onSuccess {  appSettings ->
            sendNotifications(appSettings = appSettings, oldPullRequestsWithReviews = pullRequestsBefore, newPullRequestsWithReviews = pullRequestsAfter)
        }

        return syncApiResults
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

    private suspend fun fetchPullRequests(syncSettings: SyncSettings, syncResultId: Long, repoToCheck: RepoToCheck, state: ApiPullRequestState): SyncResultEntry {
        val startAt = Clock.System.now()
        val pullRequestsResult = remoteDataSource.getPullRequests(owner = repoToCheck.owner, repo = repoToCheck.name, state = state)

        pullRequestsResult
            .mapCatching { apiPullRequests ->
                apiPullRequests.map { apiPullRequest ->
                    val appSeenAt = localDataSource.getPullRequest(apiPullRequest.id).getOrNull()?.pullRequest?.appSeenAt
                    apiPullRequest.toPullRequest(repoToCheck = repoToCheck, appSeenAt = appSeenAt)
                }
            }
            .mapCatching { pullRequestsWithReviews ->
                pullRequestsWithReviews.filterSyncValid(syncSettings = syncSettings)
            }
            .onSuccess { pullRequestsWithReviews ->
                localDataSource.upsertPullRequests(pullRequestsWithReviews.map { it.pullRequest })
            }
            .onSuccess { pullRequestsWithReviews ->
                localDataSource.removeReviewsByPullRequest(pullRequestsWithReviews.map { it.pullRequest.id })
            }
            .onSuccess { pullRequestsWithReviews ->
                val reviews = pullRequestsWithReviews.map { it.reviews }.flatten()
                localDataSource.upsertReviews(reviews)
            }

        return pullRequestsResult.toSyncResultEntry(
            syncResultId = syncResultId,
            repoToCheckId = repoToCheck.id,
            origin = SyncResultEntry.Origin.PULL,
            startAt = startAt
        )
    }
}