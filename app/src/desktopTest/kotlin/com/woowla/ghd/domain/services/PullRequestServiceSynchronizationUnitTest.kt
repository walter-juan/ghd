package com.woowla.ghd.domain.services

import com.woowla.ghd.RandomEntities
import com.woowla.ghd.TestNotificationsSender
import com.woowla.ghd.domain.data.LocalDataSource
import com.woowla.ghd.domain.data.RemoteDataSource
import com.woowla.ghd.domain.entities.ApiResponse
import com.woowla.ghd.domain.entities.NotificationsSettings
import com.woowla.ghd.domain.entities.PullRequestState
import com.woowla.ghd.domain.entities.PullRequestWithRepoAndReviews
import com.woowla.ghd.domain.entities.RateLimit
import com.woowla.ghd.domain.entities.SyncSettings
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk

class PullRequestServiceSynchronizationUnitTest : StringSpec({
    "a failed repository fetch preserves its baseline and recovery sends no new pull request notification" {
        val repoToCheck = RandomEntities.repoToCheck().copy(
            id = 1,
            pullBranchRegex = null,
            arePullRequestsEnabled = true,
            arePullRequestsNotificationsEnabled = true,
        )
        val pullRequest = RandomEntities.pullRequest(repoToCheckId = repoToCheck.id).copy(
            state = PullRequestState.OPEN,
            isDraft = false,
        )
        val pullRequestWithRepo = RandomEntities.pullRequestWithRepoAndReviews(
            repoToCheck = repoToCheck,
            pullRequest = pullRequest,
            reviews = emptyList(),
            reviewRequests = emptyList(),
        )
        val storedPullRequests = mutableListOf(pullRequestWithRepo)
        val localDataSource = mockk<LocalDataSource>()
        val remoteDataSource = mockk<RemoteDataSource>()
        val appSettingsService = mockk<AppSettingsService>()
        val notificationsSender = TestNotificationsSender()

        coEvery { localDataSource.getAllPullRequests() } answers { Result.success(storedPullRequests.toList()) }
        coEvery { localDataSource.upsertPullRequests(any()) } answers {
            val pullRequests = firstArg<List<com.woowla.ghd.domain.entities.PullRequest>>()
            pullRequests.forEach { updatedPullRequest ->
                val index = storedPullRequests.indexOfFirst { it.pullRequest.id == updatedPullRequest.id }
                val updated = pullRequestWithRepo.copy(pullRequest = updatedPullRequest)
                if (index >= 0) storedPullRequests[index] = updated else storedPullRequests += updated
            }
            Result.success(Unit)
        }
        coEvery { localDataSource.removePullRequests(any()) } answers {
            storedPullRequests.removeAll { it.pullRequest.id in firstArg<List<String>>() }
            Result.success(Unit)
        }
        coEvery { localDataSource.removeReviewsByPullRequest(any()) } returns Result.success(Unit)
        coEvery { localDataSource.upsertReviews(any()) } returns Result.success(Unit)
        coEvery { localDataSource.removeReviewRequestsByPullRequest(any()) } returns Result.success(Unit)
        coEvery { localDataSource.upsertReviewRequests(any()) } returns Result.success(Unit)
        coEvery { appSettingsService.get() } returns Result.success(
            RandomEntities.appSettings().copy(
                notificationsSettings = RandomEntities.notificationsSettings().copy(
                    stateEnabledOption = NotificationsSettings.EnabledOption.ALL,
                ),
            ),
        )
        coEvery { remoteDataSource.getAllStatesPullRequests(repoToCheck) } returnsMany listOf(
            Result.failure(IllegalStateException("temporary GitHub failure")),
            Result.success(ApiResponse(listOf(pullRequestWithRepo), RateLimit(null, null, null, null, null))),
        )

        val service = PullRequestServiceImpl(
            localDataSource = localDataSource,
            remoteDataSource = remoteDataSource,
            notificationsSender = notificationsSender,
            appSettingsService = appSettingsService,
            appLogger = mockk(relaxed = true),
        )
        val syncSettings = SyncSettings(githubPatToken = "", checkTimeout = null, pullRequestCleanUpTimeout = null)

        service.synchronize(syncResultId = 1, syncSettings = syncSettings, repoToCheckList = listOf(repoToCheck))
        storedPullRequests.shouldContainExactly(pullRequestWithRepo)

        service.synchronize(syncResultId = 2, syncSettings = syncSettings, repoToCheckList = listOf(repoToCheck))

        storedPullRequests.shouldContainExactly(pullRequestWithRepo)
        notificationsSender.newPullRequestCount shouldBe 0
    }
})
