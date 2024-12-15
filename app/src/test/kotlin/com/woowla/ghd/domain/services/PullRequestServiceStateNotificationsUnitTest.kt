package com.woowla.ghd.domain.services

import com.woowla.ghd.RandomEntities
import com.woowla.ghd.TestNotificationsSender
import com.woowla.ghd.domain.entities.NotificationsSettings.EnabledOption
import com.woowla.ghd.domain.entities.PullRequestState
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk

class PullRequestServiceStateNotificationsUnitTest: StringSpec({
    "when stateEnabledOption is NONE then sendNotifications should not send any notifications for pull requests" {
        // Given
        val testNotificationSender = TestNotificationsSender()
        val pullRequestService = PullRequestService(
            localDataSource = mockk(),
            remoteDataSource = mockk(),
            notificationsSender = testNotificationSender,
            appSettingsService = mockk()
        )
        val appSettings = RandomEntities.appSettings().copy(
            notificationsSettings = RandomEntities.notificationsSettings().copy(
                stateEnabledOption = EnabledOption.NONE,
            )
        )

        // When
        pullRequestService.sendNotifications(
            appSettings = appSettings,
            oldPullRequestsWithReviews = listOf(),
            newPullRequestsWithReviews = listOf(RandomEntities.pullRequestWithRepoAndReviews()),
        )

        // Then
        testNotificationSender.newPullRequestCount shouldBe 0
    }

    "when stateEnabledOption is ALL then sendNotifications should send notifications for all new pull requests" {
        // Given
        val testNotificationSender = TestNotificationsSender()
        val pullRequestService = PullRequestService(
            localDataSource = mockk(),
            remoteDataSource = mockk(),
            notificationsSender = testNotificationSender,
            appSettingsService = mockk()
        )
        val appSettings = RandomEntities.appSettings().copy(
            notificationsSettings = RandomEntities.notificationsSettings().copy(
                stateEnabledOption = EnabledOption.ALL
            )
        )

        // pull request without changes
        val oldPullRequestWithoutChanges = RandomEntities.pullRequest(repoToCheckId = 1)
        val pullRequestWithoutChanges = oldPullRequestWithoutChanges.copy()
        // pull request that changed from draft to open
        val oldDraftPullRequest = RandomEntities.pullRequest(repoToCheckId = 1).copy(
            isDraft = true,
            state = PullRequestState.OPEN,
        )
        val openPullRequest = oldDraftPullRequest.copy(
            isDraft = false,
            state = PullRequestState.OPEN,
        )
        // new pull requests
        val draftPullRequest = RandomEntities.pullRequest(repoToCheckId = 2).copy(
            isDraft = true,
            state = PullRequestState.OPEN,
        )
        val closedPullRequest = RandomEntities.pullRequest(repoToCheckId = 3).copy(
            isDraft = false,
            state = PullRequestState.CLOSED,
        )
        val mergedPullRequest = RandomEntities.pullRequest(repoToCheckId = 4).copy(
            isDraft = false,
            state = PullRequestState.MERGED,
        )

        val oldPullRequestWithoutChangesWithReviews = RandomEntities.pullRequestWithRepoAndReviews(pullRequest = oldPullRequestWithoutChanges)
        val pullRequestWithoutChangesWithReviews = RandomEntities.pullRequestWithRepoAndReviews(pullRequest = pullRequestWithoutChanges)
        val oldDraftPullRequestWithReviews = RandomEntities.pullRequestWithRepoAndReviews(pullRequest = oldDraftPullRequest)
        val openPullRequestWithReviews = RandomEntities.pullRequestWithRepoAndReviews(pullRequest = openPullRequest)
        val draftPullRequestWithReviews = RandomEntities.pullRequestWithRepoAndReviews(pullRequest = draftPullRequest)
        val closedPullRequestWithReviews = RandomEntities.pullRequestWithRepoAndReviews(pullRequest = closedPullRequest)
        val mergedPullRequestWithReviews = RandomEntities.pullRequestWithRepoAndReviews(pullRequest = mergedPullRequest)

        // When
        pullRequestService.sendNotifications(
            appSettings = appSettings,
            oldPullRequestsWithReviews = listOf(
                oldPullRequestWithoutChangesWithReviews,
                oldDraftPullRequestWithReviews,
            ),
            newPullRequestsWithReviews = listOf(
                pullRequestWithoutChangesWithReviews,
                openPullRequestWithReviews,
                draftPullRequestWithReviews,
                closedPullRequestWithReviews,
                mergedPullRequestWithReviews,
            ),
        )

        // Then
        testNotificationSender.newPullRequestCount shouldBe 4
    }

    "when stateEnabledOption is FILTERED then sendNotifications should send notifications for filtered username new pull requests" {
        // Given
        val otherUsername = "other-user-name"
        val filteredUsername = "test-user-name"
        val testNotificationSender = TestNotificationsSender()
        val pullRequestService = PullRequestService(
            localDataSource = mockk(),
            remoteDataSource = mockk(),
            notificationsSender = testNotificationSender,
            appSettingsService = mockk()
        )
        val appSettings = RandomEntities.appSettings().copy(
            notificationsSettings = RandomEntities.notificationsSettings().copy(
                filterUsername = filteredUsername,
                stateEnabledOption = EnabledOption.FILTERED
            )
        )

        val otherAuthor = RandomEntities.author().copy(login = otherUsername)
        val filteredAuthor = RandomEntities.author().copy(login = filteredUsername)

        // pull request without changes
        val oldPullRequestWithoutChanges = RandomEntities.pullRequest(repoToCheckId = 1, otherAuthor)
        val pullRequestWithoutChanges = oldPullRequestWithoutChanges.copy()
        // new pull request from the filtered username
        val draftPullRequestFromFilteredUsername = RandomEntities.pullRequest(repoToCheckId = 2, filteredAuthor).copy(
            isDraft = true,
            state = PullRequestState.OPEN,
        )
        // pull request that changed from draft to open
        val oldDraftPullRequest = RandomEntities.pullRequest(repoToCheckId = 1, otherAuthor).copy(
            isDraft = true,
            state = PullRequestState.OPEN,
        )
        val openPullRequest = oldDraftPullRequest.copy(
            isDraft = false,
            state = PullRequestState.OPEN,
        )
        // new pull requests
        val draftPullRequest = RandomEntities.pullRequest(repoToCheckId = 2, otherAuthor).copy(
            isDraft = true,
            state = PullRequestState.OPEN,
        )
        val closedPullRequest = RandomEntities.pullRequest(repoToCheckId = 3, otherAuthor).copy(
            isDraft = false,
            state = PullRequestState.CLOSED,
        )
        val mergedPullRequest = RandomEntities.pullRequest(repoToCheckId = 4, otherAuthor).copy(
            isDraft = false,
            state = PullRequestState.MERGED,
        )

        val oldPullRequestWithoutChangesWithReviews = RandomEntities.pullRequestWithRepoAndReviews(pullRequest = oldPullRequestWithoutChanges)
        val pullRequestWithoutChangesWithReviews = RandomEntities.pullRequestWithRepoAndReviews(pullRequest = pullRequestWithoutChanges)
        val draftPullRequestWithReviewsFromFilteredUsername = RandomEntities.pullRequestWithRepoAndReviews(pullRequest = draftPullRequestFromFilteredUsername)
        val oldDraftPullRequestWithReviews = RandomEntities.pullRequestWithRepoAndReviews(pullRequest = oldDraftPullRequest)
        val openPullRequestWithReviews = RandomEntities.pullRequestWithRepoAndReviews(pullRequest = openPullRequest)
        val draftPullRequestWithReviews = RandomEntities.pullRequestWithRepoAndReviews(pullRequest = draftPullRequest)
        val closedPullRequestWithReviews = RandomEntities.pullRequestWithRepoAndReviews(pullRequest = closedPullRequest)
        val mergedPullRequestWithReviews = RandomEntities.pullRequestWithRepoAndReviews(pullRequest = mergedPullRequest)

        // When
        pullRequestService.sendNotifications(
            appSettings = appSettings,
            oldPullRequestsWithReviews = listOf(
                oldPullRequestWithoutChangesWithReviews,
                oldDraftPullRequestWithReviews,
            ),
            newPullRequestsWithReviews = listOf(
                pullRequestWithoutChangesWithReviews,
                draftPullRequestWithReviewsFromFilteredUsername,
                draftPullRequestWithReviews,
                openPullRequestWithReviews,
                closedPullRequestWithReviews,
                mergedPullRequestWithReviews,
            ),
        )

        // Then
        testNotificationSender.newPullRequestCount shouldBe 4
    }
})