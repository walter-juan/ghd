package com.woowla.ghd.domain.services

import com.woowla.ghd.data.local.LocalDataSource
import com.woowla.ghd.data.remote.RemoteDataSource
import com.woowla.ghd.data.remote.type.PullRequestState as ApiPullRequestState
import com.woowla.ghd.domain.entities.PullRequest
import com.woowla.ghd.domain.entities.PullRequestState
import com.woowla.ghd.domain.entities.RepoToCheck
import com.woowla.ghd.domain.entities.SyncSettings
import com.woowla.ghd.domain.mappers.ApiMappers
import com.woowla.ghd.domain.mappers.DbMappers
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
) : SynchronizableService {
    suspend fun getAll(): Result<List<PullRequest>> {
        return localDataSource.getAllPullRequests()
            .mapCatching { dbPullRequests ->
                DbMappers.INSTANCE.dbPullRequestToPullRequest(dbPullRequests)
            }.mapCatching { pullRequests ->
                pullRequests.sorted()
            }
    }

    suspend fun markAsSeen(id: String, appSeenAt: Instant?): Result<Unit> {
        return localDataSource.updateAppSeenAt(id = id, appSeenAt = appSeenAt)
    }

    override suspend fun synchronize(syncSettings: SyncSettings, repoToCheckList: List<RepoToCheck>) {
        val pullRequestsBefore = getAll().getOrDefault(listOf())

        coroutineScope {
            val fetchOpenPullRequests = repoToCheckList.map { repoToCheck ->
                async { fetchPullRequests(repoToCheck, ApiPullRequestState.OPEN) }
            }
            val fetchMergedPullRequests = repoToCheckList.map { repoToCheck ->
                async { fetchPullRequests(repoToCheck, ApiPullRequestState.MERGED) }
            }
            val fetchClosedPullRequests = repoToCheckList.map { repoToCheck ->
                async { fetchPullRequests(repoToCheck, ApiPullRequestState.CLOSED) }
            }

            fetchOpenPullRequests.awaitAll()
            fetchMergedPullRequests.awaitAll()
            fetchClosedPullRequests.awaitAll()

            cleanUp(syncSettings)
        }

        val pullRequestsAfter = getAll().getOrDefault(listOf())
        sendNotifications(oldPullRequests = pullRequestsBefore, newPullRequests = pullRequestsAfter)
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

    suspend fun sendNotifications(oldPullRequests: List<PullRequest>, newPullRequests: List<PullRequest>): Result<Unit> {
        val oldPullRequestIds = oldPullRequests.map { it.id }

        // notification for a new pull requests
        newPullRequests
            .filter {
                it.repoToCheck.pullNotificationsEnabled
            }
            .filterNot {
                oldPullRequestIds.contains(it.id)
            }
            .forEach {
                notificationsSender.newPullRequest(it)
            }

        // notification for an update
        newPullRequests
            .filter {
                it.repoToCheck.pullNotificationsEnabled
            }
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

        return Result.success(Unit)
    }

    private suspend fun fetchPullRequests(repoToCheck: RepoToCheck, state: ApiPullRequestState) {
        val apiMappers = ApiMappers.INSTANCE
        remoteDataSource
            .getPullRequests(owner = repoToCheck.owner, repo = repoToCheck.name, state = state)
            .mapCatching { apiPullRequests ->
                apiPullRequests.map { apiPullRequest ->
                    val appSeenAt = localDataSource.getPullRequest(apiPullRequest.id).getOrNull()?.appSeenAt
                    apiMappers.pullRequestNodeToUpsertRequest(pullRequestNode = apiPullRequest, appSeenAt = appSeenAt, repoToCheckId = repoToCheck.id)
                }
            }
            .onSuccess { pullUpsertRequests ->
                localDataSource.upsertPullRequests(pullUpsertRequests)
            }
    }
}