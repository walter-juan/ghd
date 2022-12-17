package com.woowla.ghd.domain.usecases

import com.woowla.ghd.KermitLogger
import com.woowla.ghd.data.local.LocalDataSource
import com.woowla.ghd.data.local.db.entities.DbRepoToCheck
import com.woowla.ghd.data.remote.RemoteDataSource
import com.woowla.ghd.data.remote.type.PullRequestState as RemotePullRequestState
import com.woowla.ghd.domain.entities.SyncSettings
import com.woowla.ghd.domain.entities.PullRequestState
import com.woowla.ghd.domain.mappers.ApiMappers
import com.woowla.ghd.eventbus.Event
import com.woowla.ghd.eventbus.EventBus
import com.woowla.ghd.utils.UseCaseWithoutParams
import kotlin.system.measureTimeMillis
import kotlin.time.Duration
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.datetime.Clock

class SynchronizationUseCase(
    private val getSyncSettingsUseCase: GetSyncSettingsUseCase = GetSyncSettingsUseCase(),
    private val saveSyncSettingsUseCase: SaveSyncSettingsUseCase = SaveSyncSettingsUseCase(),
    private val getAllPullRequestsUseCase: GetAllPullRequestsUseCase = GetAllPullRequestsUseCase(),
    private val getAllReleasesUseCase: GetAllReleasesUseCase = GetAllReleasesUseCase(),
    private val sendPullRequestsNotificationsUseCase: SendPullRequestsNotificationsUseCase = SendPullRequestsNotificationsUseCase(),
    private val sendReleasesNotificationsUseCase: SendReleasesNotificationsUseCase = SendReleasesNotificationsUseCase(),
    private val localDataSource: LocalDataSource = LocalDataSource(),
    private val remoteDataSource: RemoteDataSource = RemoteDataSource()
) : UseCaseWithoutParams<Unit>() {
    override suspend fun perform(): Result<Unit> {
        val getSyncSettingsResult = getSyncSettingsUseCase.execute()
        val getSyncSettings = getSyncSettingsResult.getOrNull()
        val githubPatToken = getSyncSettings?.githubPatToken
        val pullRequestCleanUpTimeout = SyncSettings.getValidPullRequestCleanUpTimeout(getSyncSettings?.pullRequestCleanUpTimeout)
        KermitLogger.d("SynchronizationUseCase :: is github token null or blank? ${githubPatToken.isNullOrBlank()}")
        if (githubPatToken.isNullOrBlank()) {
            return Result.success(Unit)
        }
        val allReposToCheck = localDataSource.getAllReposToCheck().getOrDefault(listOf())
        val pullRequestsBefore = getAllPullRequestsUseCase.execute().getOrDefault(listOf())
        val releasesBefore = getAllReleasesUseCase.execute().getOrDefault(listOf())

        val measuredTime = coroutineScope {
            measureTimeMillis {
                val fetchOpenPullRequests = allReposToCheck.map { dbRepoToCheck ->
                    async { fetchPullRequests(dbRepoToCheck, RemotePullRequestState.OPEN) }
                }
                val fetchMergedPullRequests = allReposToCheck.map { dbRepoToCheck ->
                    async { fetchPullRequests(dbRepoToCheck, RemotePullRequestState.MERGED) }
                }
                val fetchClosedPullRequests = allReposToCheck.map { dbRepoToCheck ->
                    async { fetchPullRequests(dbRepoToCheck, RemotePullRequestState.CLOSED) }
                }
                val fetchReleases = allReposToCheck.map { dbRepoToCheck ->
                    async { fetchLastReleases(dbRepoToCheck) }
                }

                fetchReleases.awaitAll()
                fetchOpenPullRequests.awaitAll()
                fetchMergedPullRequests.awaitAll()
                fetchClosedPullRequests.awaitAll()

                cleanUpPullRequests(cleanUpTimeout = pullRequestCleanUpTimeout)
            }
        }

        val synchronizedAt = Clock.System.now()
        getSyncSettingsResult.onSuccess { syncSettings ->
            saveSyncSettingsUseCase.execute(syncSettings.copy(synchronizedAt = synchronizedAt))
        }

        KermitLogger.d("SynchronizationUseCase :: sync at $synchronizedAt and it took $measuredTime millis to download the pull requests and repositories")

        EventBus.publish(Event.SYNCHRONIZED)

        val pullRequestsAfter = getAllPullRequestsUseCase.execute().getOrDefault(listOf())
        val releasesAfter = getAllReleasesUseCase.execute().getOrDefault(listOf())

        sendPullRequestsNotificationsUseCase.execute(SendPullRequestsNotificationsUseCase.Params(oldPullRequests = pullRequestsBefore, newPullRequests = pullRequestsAfter))
        sendReleasesNotificationsUseCase.execute(SendReleasesNotificationsUseCase.Params(oldReleases = releasesBefore, newReleases = releasesAfter))

        return Result.success(Unit)
    }

    private suspend fun fetchPullRequests(dbRepoToCheck: DbRepoToCheck, state: RemotePullRequestState) {
        val apiMappers = ApiMappers.INSTANCE
        remoteDataSource
            .getPullRequests(owner = dbRepoToCheck.owner, repo = dbRepoToCheck.name, state = state)
            .map { apiPullRequests ->
                // insert
                val pullUpsertRequests = apiPullRequests.map { apiPullRequest ->
                    val appSeenAt = localDataSource.getPullRequest(apiPullRequest.id).getOrNull()?.appSeenAt
                    apiMappers.pullRequestNodeToUpsertRequest(pullRequestNode = apiPullRequest, appSeenAt = appSeenAt, repoToCheckId = dbRepoToCheck.id.value)
                }.filter { upsertPullRequestRequest ->
                    val headRef = upsertPullRequestRequest.headRef
                    val regexStr = dbRepoToCheck.pullBranchRegex
                    if (!headRef.isNullOrBlank() && !regexStr.isNullOrBlank()) {
                        headRef.matches(regexStr.toRegex())
                    } else {
                        true
                    }
                }
                localDataSource.upsertPullRequests(pullUpsertRequests)
            }
    }

    private suspend fun fetchLastReleases(dbRepoToCheck: DbRepoToCheck) {
        val apiMappers = ApiMappers.INSTANCE

        remoteDataSource
            .getLastRelease(owner = dbRepoToCheck.owner, repo = dbRepoToCheck.name)
            .map { apiRelease ->
                // remove old one
                localDataSource.removeReleaseByRepoToCheck(repoToCheckId = dbRepoToCheck.id.value)
                // insert the new one
                try {
                    val releaseUpsertRequest = apiMappers.lastReleaseToUpsertRequest(apiRelease, dbRepoToCheck.id.value)
                    localDataSource.upsertRelease(releaseUpsertRequest)
                } catch (ex: Exception) {
                    KermitLogger.e("fetchLastReleases error", ex)
                }
            }
    }

    private suspend fun cleanUpPullRequests(cleanUpTimeout: Long) {
        getAllPullRequestsUseCase.execute()
            .map { pullRequests ->
                pullRequests.filter { pullRequest ->
                    if (pullRequest.state == PullRequestState.CLOSED || pullRequest.state == PullRequestState.MERGED) {
                        val duration: Duration = Clock.System.now() - pullRequest.updatedAt
                        duration.inWholeHours > cleanUpTimeout
                    } else {
                        false
                    }
                }
            }
            .map { pullRequests ->
                pullRequests.map { it.id }
            }
            .map { pullRequestIds ->
                localDataSource.removePullRequests(pullRequestIds)
            }
    }
}