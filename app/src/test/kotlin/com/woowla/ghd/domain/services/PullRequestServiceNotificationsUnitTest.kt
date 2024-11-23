package com.woowla.ghd.domain.services

import com.woowla.ghd.RandomEntities
import com.woowla.ghd.TestNotificationsSender
import com.woowla.ghd.domain.entities.AppSettings
import com.woowla.ghd.domain.entities.PullRequest
import com.woowla.ghd.domain.entities.PullRequestState
import com.woowla.ghd.domain.entities.PullRequestStateWithDraft
import com.woowla.ghd.domain.entities.PullRequestWithRepoAndReviews
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk

class PullRequestServiceNotificationsUnitTest : FreeSpec({
    fun buildAppSettings(
        newPullRequest: Boolean = false,
        updatePullRequest: Boolean = false,
    ) = AppSettings(
        darkTheme = null,
        newPullRequestsNotificationsEnabled = newPullRequest,
        updatedPullRequestsNotificationsEnabled = updatePullRequest,
        newReleaseNotificationsEnabled = false,
        updatedReleaseNotificationsEnabled = false
    )

    fun buildPullRequestState(stateWithDraft: PullRequestStateWithDraft): PullRequestState {
        return when(stateWithDraft) {
            PullRequestStateWithDraft.UNKNOWN -> PullRequestState.UNKNOWN
            PullRequestStateWithDraft.OPEN -> PullRequestState.OPEN
            PullRequestStateWithDraft.CLOSED -> PullRequestState.CLOSED
            PullRequestStateWithDraft.MERGED -> PullRequestState.MERGED
            PullRequestStateWithDraft.DRAFT -> PullRequestState.entries.random()
        }
    }

    fun buildPullRequests(repoToCheckId: Long): List<PullRequest> {
        val asDraft = PullRequestStateWithDraft.entries.map { stateWithDraft ->
            RandomEntities.pullRequest().copy(
                repoToCheckId = repoToCheckId,
                state = buildPullRequestState(stateWithDraft),
                isDraft = true
            )
        }
        val notDraft = PullRequestStateWithDraft.entries.map { stateWithDraft ->
            RandomEntities.pullRequest().copy(
                repoToCheckId = repoToCheckId,
                state = buildPullRequestState(stateWithDraft),
                isDraft = false
            )
        }
        return (asDraft + notDraft).distinctBy { "${it.isDraft}${it.state}" }
    }

    "When new PR created and notification is disabled DO NOT send the notification" - {
        val appSettings = buildAppSettings()
        val repoToCheck = RandomEntities.repoToCheck()
        val pullRequests = buildPullRequests(repoToCheckId = repoToCheck.id)

        pullRequests.sortedWith(compareBy({ it.state }, { it.isDraft })).forEach { pullRequest ->
            "with PR as ${pullRequest.state} and draft ${pullRequest.isDraft}" {
                val testNotificationsSender = TestNotificationsSender()
                val pullRequestWithRepoAndReviews = PullRequestWithRepoAndReviews(repoToCheck = repoToCheck, pullRequest = pullRequest, reviews = listOf())
                val service = PullRequestService(
                    localDataSource = mockk(),
                    remoteDataSource = mockk(),
                    notificationsSender = testNotificationsSender,
                    appSettingsService = mockk(),
                )

                service.sendNotifications(
                    appSettings = appSettings,
                    oldPullRequestsWithReviews = listOf(),
                    newPullRequestsWithReviews = listOf(pullRequestWithRepoAndReviews)
                )

                testNotificationsSender.newPullRequestCount shouldBe 0
            }
        }
    }

    "When new PR created and notification is enabled SEND the notification ALWAYS" - {
        val appSettings = buildAppSettings(newPullRequest = true)
        val repoToCheck = RandomEntities.repoToCheck()
        val pullRequests = buildPullRequests(repoToCheckId = repoToCheck.id)

        pullRequests.sortedWith(compareBy({ it.state }, { it.isDraft })).forEach { pullRequest ->
            "with PR as ${pullRequest.state} and draft ${pullRequest.isDraft}" {
                val testNotificationsSender = TestNotificationsSender()
                val pullRequestWithRepoAndReviews = PullRequestWithRepoAndReviews(repoToCheck = repoToCheck, pullRequest = pullRequest, reviews = listOf())
                val service = PullRequestService(
                    localDataSource = mockk(),
                    remoteDataSource = mockk(),
                    notificationsSender = testNotificationsSender,
                    appSettingsService = mockk(),
                )

                service.sendNotifications(
                    appSettings = appSettings,
                    oldPullRequestsWithReviews = listOf(),
                    newPullRequestsWithReviews = listOf(pullRequestWithRepoAndReviews)
                )

                testNotificationsSender.newPullRequestCount shouldBe 1
            }
        }
    }
})
