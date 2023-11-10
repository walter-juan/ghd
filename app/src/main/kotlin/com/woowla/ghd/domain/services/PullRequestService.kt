package com.woowla.ghd.domain.services

import com.woowla.ghd.data.local.LocalDataSource
import com.woowla.ghd.data.remote.RemoteDataSource
import com.woowla.ghd.domain.entities.AppSettings
import com.woowla.ghd.data.remote.type.PullRequestState as ApiPullRequestState
import com.woowla.ghd.domain.entities.PullRequest
import com.woowla.ghd.domain.entities.PullRequestState
import com.woowla.ghd.domain.entities.RepoToCheck
import com.woowla.ghd.domain.entities.SyncResultEntry
import com.woowla.ghd.domain.entities.SyncSettings
import com.woowla.ghd.domain.mappers.toPullRequest
import com.woowla.ghd.domain.mappers.toUpsertPullRequestRequest
import com.woowla.ghd.domain.mappers.toUpsertReviewRequests
import com.woowla.ghd.domain.requests.UpsertSyncResultEntryRequest
import com.woowla.ghd.domain.requests.toUpsertSyncResultEntryRequest
import com.woowla.ghd.domain.synchronization.SynchronizableService
import com.woowla.ghd.notifications.NotificationsSender
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration

class PullRequestService(
    private val localDataSource: LocalDataSource = LocalDataSource(),
    private val remoteDataSource: RemoteDataSource = RemoteDataSource(),
    private val notificationsSender: NotificationsSender = NotificationsSender(),
    private val appSettingsService: AppSettingsService = AppSettingsService(),
) : SynchronizableService {
    suspend fun getAll(): Result<List<PullRequest>> {
        return localDataSource.getAllPullRequests()
            .mapCatching { dbPullRequests ->
                dbPullRequests.map { it.toPullRequest() }
            }.mapCatching { pullRequests ->
                pullRequests.sorted()
            }
    }

    suspend fun markAsSeen(id: String, appSeenAt: Instant?): Result<Unit> {
        return localDataSource.updateAppSeenAt(id = id, appSeenAt = appSeenAt)
    }

    override suspend fun synchronize(syncResultId: Long, syncSettings: SyncSettings, repoToCheckList: List<RepoToCheck>): List<UpsertSyncResultEntryRequest> {
        val pullRequestsBefore = getAll().getOrDefault(listOf())

        val syncApiResults = coroutineScope {
            val fetchOpenPullRequests = repoToCheckList.map { repoToCheck ->
                async { fetchPullRequests(syncResultId, repoToCheck, ApiPullRequestState.OPEN) }
            }
            val fetchMergedPullRequests = repoToCheckList.map { repoToCheck ->
                async { fetchPullRequests(syncResultId, repoToCheck, ApiPullRequestState.MERGED) }
            }
            val fetchClosedPullRequests = repoToCheckList.map { repoToCheck ->
                async { fetchPullRequests(syncResultId, repoToCheck, ApiPullRequestState.CLOSED) }
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
        val cleanUpTimeout = syncSettings.getValidPullRequestCleanUpTimeout()
        getAll()
            .mapCatching { pullRequests ->
                pullRequests
                    .filter { pullRequest ->
                        val isOld = if (pullRequest.state == PullRequestState.CLOSED || pullRequest.state == PullRequestState.MERGED) {
                            val duration: Duration = Clock.System.now() - pullRequest.updatedAt
                            duration.inWholeHours > cleanUpTimeout
                        } else {
                            false
                        }

                        val headRef = pullRequest.headRef
                        val regexStr = pullRequest.repoToCheck.pullBranchRegex
                        val hasBranchToExclude = if (!headRef.isNullOrBlank() && !regexStr.isNullOrBlank()) {
                            !headRef.matches(regexStr.toRegex())
                        } else {
                            false
                        }

                        isOld || hasBranchToExclude
                    }
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

    private suspend fun fetchPullRequests(syncResultId: Long, repoToCheck: RepoToCheck, state: ApiPullRequestState): UpsertSyncResultEntryRequest {
        val startAt = Clock.System.now()
        val pullRequestsResult = remoteDataSource.getPullRequests(owner = repoToCheck.owner, repo = repoToCheck.name, state = state)

        // pull requests
        pullRequestsResult
            .mapCatching { apiPullRequests ->
                apiPullRequests.map { apiPullRequest ->
                    val appSeenAt = localDataSource.getPullRequest(apiPullRequest.id).getOrNull()?.appSeenAt
                    apiPullRequest.toUpsertPullRequestRequest(appSeenAt = appSeenAt, repoToCheckId = repoToCheck.id)
                }
            }
            .onSuccess { pullUpsertRequests ->
                localDataSource.upsertPullRequests(pullUpsertRequests)
            }

        // reviews
        pullRequestsResult
            .mapCatching { apiPullRequests ->
                apiPullRequests.map { apiPullRequest ->
                    apiPullRequest.latestReviews?.toUpsertReviewRequests(pullRequestId = apiPullRequest.id) ?: listOf()
                }.flatten()
            }
            .onSuccess { reviewUpsertRequests ->
                localDataSource.removeReviewsByPullRequest(reviewUpsertRequests.map { it.pullRequestId })
            }
            .onSuccess { reviewUpsertRequests ->
                localDataSource.upsertReviews(reviewUpsertRequests)
            }

        return pullRequestsResult.toUpsertSyncResultEntryRequest(
            syncResultId = syncResultId,
            repoToCheckId = repoToCheck.id,
            origin = SyncResultEntry.Origin.PULL,
            startAt = startAt
        )
    }
}