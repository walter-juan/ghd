package com.woowla.ghd.domain.services

import com.woowla.ghd.data.local.LocalDataSource
import com.woowla.ghd.data.remote.RemoteDataSource
import com.woowla.ghd.domain.entities.AppSettings
import com.woowla.ghd.data.remote.type.PullRequestState as ApiPullRequestState
import com.woowla.ghd.domain.entities.PullRequest
import com.woowla.ghd.domain.entities.RepoToCheck
import com.woowla.ghd.domain.entities.SyncResultEntry
import com.woowla.ghd.domain.entities.SyncSettings
import com.woowla.ghd.data.remote.mappers.toPullRequest
import com.woowla.ghd.domain.entities.filterNotSyncValid
import com.woowla.ghd.domain.entities.filterSyncValid
import com.woowla.ghd.domain.mappers.toUpsertSyncResultEntryRequest
import com.woowla.ghd.domain.requests.UpsertSyncResultEntryRequest
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
    suspend fun getAll(): Result<List<PullRequest>> {
        return localDataSource.getAllPullRequests()
            .mapCatching { pullRequests ->
                pullRequests.sorted()
            }
    }

    suspend fun markAsSeen(id: String, appSeenAt: Instant?): Result<Unit> {
        return localDataSource.updateAppSeenAt(id = id, appSeenAt = appSeenAt)
    }

    override suspend fun synchronize(syncResultId: Long, syncSettings: SyncSettings, repoToCheckList: List<RepoToCheck>): List<UpsertSyncResultEntryRequest> {
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
            sendNotifications(appSettings = appSettings, oldPullRequests = pullRequestsBefore, newPullRequests = pullRequestsAfter)
        }

        return syncApiResults
    }

    suspend fun cleanUp(syncSettings: SyncSettings) {
        getAll()
            .mapCatching { pullRequests ->
                pullRequests.filterNotSyncValid(syncSettings = syncSettings)
            }
            .mapCatching { pullRequests ->
                pullRequests.map { it.id }
            }
            .onSuccess { pullRequestIds ->
                localDataSource.removePullRequests(pullRequestIds)
            }
    }

    suspend fun sendNotifications(appSettings: AppSettings, oldPullRequests: List<PullRequest>, newPullRequests: List<PullRequest>): Result<Unit> {
        val oldPullRequestIds = oldPullRequests.map { it.id }

        // notification for a new pull requests
        if (appSettings.newPullRequestsNotificationsEnabled) {
            newPullRequests
                .filterNot {
                    oldPullRequestIds.contains(it.id)
                }
                .forEach {
                    notificationsSender.newPullRequest(it)
                }
        }

        // notification for an update
        if (appSettings.updatedPullRequestsNotificationsEnabled) {
            newPullRequests
                .filter {
                    !it.appSeen
                }
                .filter { newPull ->
                    val oldRelease = oldPullRequests.firstOrNull { it.id == newPull.id }

                    if (oldRelease != null) {
                        oldRelease.updatedAt != newPull.updatedAt
                    } else {
                        false
                    }
                }
                .forEach { newPull ->
                    notificationsSender.updatePullRequest(newPull)
                }
        }

        return Result.success(Unit)
    }

    private suspend fun fetchPullRequests(syncSettings: SyncSettings, syncResultId: Long, repoToCheck: RepoToCheck, state: ApiPullRequestState): UpsertSyncResultEntryRequest {
        val startAt = Clock.System.now()
        val pullRequestsResult = remoteDataSource.getPullRequests(owner = repoToCheck.owner, repo = repoToCheck.name, state = state)

        pullRequestsResult
            .mapCatching { apiPullRequests ->
                apiPullRequests.map { apiPullRequest ->
                    val appSeenAt = localDataSource.getPullRequest(apiPullRequest.id).getOrNull()?.appSeenAt
                    apiPullRequest.toPullRequest(repoToCheck = repoToCheck, appSeenAt = appSeenAt)
                }
            }
            .mapCatching { pullRequests ->
                pullRequests.filterSyncValid(syncSettings = syncSettings)
            }
            .onSuccess { pullRequests ->
                localDataSource.upsertPullRequests(pullRequests)
            }
            .onSuccess { pullRequests ->
                localDataSource.removeReviewsByPullRequest(pullRequests.map { it.id })
            }
            .onSuccess { pullRequests ->
                val reviews = pullRequests.map { it.reviews }.flatten()
                localDataSource.upsertReviews(reviews)
            }

        return pullRequestsResult.toUpsertSyncResultEntryRequest(
            syncResultId = syncResultId,
            repoToCheckId = repoToCheck.id,
            origin = SyncResultEntry.Origin.PULL,
            startAt = startAt
        )
    }
}