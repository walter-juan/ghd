package com.woowla.ghd.domain.usecases

import com.woowla.ghd.KermitLogger
import com.woowla.ghd.data.local.DbRepoToCheck
import com.woowla.ghd.data.local.LocalDataSource
import com.woowla.ghd.data.remote.RemoteDataSource
import com.woowla.ghd.domain.entities.AppSettings
import com.woowla.ghd.domain.entities.PullRequestState
import com.woowla.ghd.domain.mappers.ApiMappers
import com.woowla.ghd.domain.mappers.InstantMapper
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
    private val appSettingsUseCase: GetAppSettingsUseCase = GetAppSettingsUseCase(),
    private val saveAppSettingsUseCase: SaveAppSettingsUseCase = SaveAppSettingsUseCase(),
    private val getAllPullRequestsUseCase: GetAllPullRequestsUseCase = GetAllPullRequestsUseCase(),
    private val getAllReleasesUseCase: GetAllReleasesUseCase = GetAllReleasesUseCase(),
    private val sendPullRequestsNotificationsUseCase: SendPullRequestsNotificationsUseCase = SendPullRequestsNotificationsUseCase(),
    private val sendReleasesNotificationsUseCase: SendReleasesNotificationsUseCase = SendReleasesNotificationsUseCase(),
    private val localDataSource: LocalDataSource = LocalDataSource(),
    private val remoteDataSource: RemoteDataSource = RemoteDataSource()
) : UseCaseWithoutParams<Unit>() {
    override suspend fun perform(): Result<Unit> {
        val appSettingsResult = appSettingsUseCase.execute()
        val appSettings = appSettingsResult.getOrNull()
        val githubPatToken = appSettings?.githubPatToken
        val pullRequestCleanUpTimeout = AppSettings.getValidPullRequestCleanUpTimeout(appSettings?.pullRequestCleanUpTimeout)
        KermitLogger.d("SynchronizationUseCase :: is github token null or blank? ${githubPatToken.isNullOrBlank()}")
        if (githubPatToken.isNullOrBlank()) {
            return Result.success(Unit)
        }
        val allReposToCheck = localDataSource.getAllReposToCheck().getOrDefault(listOf())
        val pullRequestsBefore = getAllPullRequestsUseCase.execute().getOrDefault(listOf())
        val releasesBefore = getAllReleasesUseCase.execute().getOrDefault(listOf())

        val measuredTime = coroutineScope {
            measureTimeMillis {
                val fetchPullRequests = allReposToCheck.map { dbRepoToCheck ->
                    async { fetchPullRequests(dbRepoToCheck) }
                }
                val fetchReleases = allReposToCheck.map { dbRepoToCheck ->
                    async { fetchLastReleases(dbRepoToCheck) }
                }

                fetchReleases.awaitAll()
                fetchPullRequests.awaitAll()

                cleanUpPullRequests(cleanUpTimeout = pullRequestCleanUpTimeout)
            }
        }

        val synchronizedAt = Clock.System.now()
        appSettingsResult.onSuccess { appSettings ->
            saveAppSettingsUseCase.execute(appSettings.copy(synchronizedAt = synchronizedAt))
        }

        KermitLogger.d("SynchronizationUseCase :: sync at $synchronizedAt and it took $measuredTime millis to download the pull requests and repositories")

        EventBus.publish(Event.SYNCHRONIZED)

        val pullRequestsAfter = getAllPullRequestsUseCase.execute().getOrDefault(listOf())
        val releasesAfter = getAllReleasesUseCase.execute().getOrDefault(listOf())

        sendPullRequestsNotificationsUseCase.execute(SendPullRequestsNotificationsUseCase.Params(oldPullRequests = pullRequestsBefore, newPullRequests = pullRequestsAfter))
        sendReleasesNotificationsUseCase.execute(SendReleasesNotificationsUseCase.Params(oldReleases = releasesBefore, newReleases = releasesAfter))

        return Result.success(Unit)
    }

    private suspend fun fetchPullRequests(dbRepoToCheck: DbRepoToCheck) {
        val apiMappers = ApiMappers.INSTANCE
        remoteDataSource
            .getAllPullRequests(owner = dbRepoToCheck.owner, repo = dbRepoToCheck.name)
            .map { apiPullRequests ->
                // insert
                val pullUpsertRequests = apiPullRequests.map { apiPullRequest ->
                    val instantMapper = InstantMapper()
                    val appSeenAt = localDataSource.getPullRequest(apiPullRequest.id).getOrNull()?.appSeenAt
                    apiMappers.pullRequestNodeToUpsertRequest(pullRequestNode = apiPullRequest, appSeenAt = instantMapper.stringToInstant(appSeenAt), repoToCheckId = dbRepoToCheck.id)
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
                localDataSource.removeReleaseByRepoToCheck(repoToCheckId = dbRepoToCheck.id)
                // insert the new one
                try {
                    val releaseUpsertRequest = apiMappers.lastReleaseToUpsertRequest(apiRelease, dbRepoToCheck.id)
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