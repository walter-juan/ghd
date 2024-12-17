package com.woowla.ghd.domain.services

import com.woowla.ghd.RandomEntities
import com.woowla.ghd.TestNotificationsSender
import com.woowla.ghd.domain.entities.NotificationsSettings.EnabledOption
import com.woowla.ghd.domain.entities.PullRequestState
import com.woowla.ghd.domain.entities.PullRequestStateExtended
import com.woowla.ghd.domain.entities.PullRequestWithRepoAndReviews
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk

class PullRequestServiceStateNotificationsUnitTest: StringSpec({
    /**
     * Return a list of old pull request and new pull request for the activity changes.
     * This will contain:
     * - 1 pull request by [otherUsername] without changes
     * - 1 new draft pull request by [filteredUsername]
     * - 1 pull request by [otherUsername] that changed from draft to open
     * - 1 new draft pull request by [otherUsername]
     * - 1 new closed pull request by [otherUsername]
     * - 1 new merged pull request by [otherUsername]
     */
    fun buildActivityChangedPullRequests(
        filteredUsername: String = "filtered-username-login",
        otherUsername: String = "other-username-login",
    ): Pair<List<PullRequestWithRepoAndReviews>, List<PullRequestWithRepoAndReviews>> {
        val otherAuthor = RandomEntities.author().copy(login = otherUsername)
        val filteredAuthor = RandomEntities.author().copy(login = filteredUsername)

        // pull request without changes
        val oldPullRequestWithoutChanges = RandomEntities.pullRequest().copy(
            author = otherAuthor,
        )
        val pullRequestWithoutChanges = oldPullRequestWithoutChanges.copy()
        // new pull request from the filtered username
        val draftPullRequestFromFilteredUsername = RandomEntities.pullRequest().copy(
            author = filteredAuthor,
            isDraft = true,
            state = PullRequestState.OPEN,
        )
        require(draftPullRequestFromFilteredUsername.stateExtended == PullRequestStateExtended.DRAFT)
        // pull request that changed from draft to open
        val oldDraftPullRequest = RandomEntities.pullRequest().copy(
            author = otherAuthor,
            isDraft = true,
            state = PullRequestState.OPEN,
        )
        require(oldDraftPullRequest.stateExtended == PullRequestStateExtended.DRAFT)
        val openPullRequest = oldDraftPullRequest.copy(
            isDraft = false,
            state = PullRequestState.OPEN,
        )
        require(openPullRequest.stateExtended == PullRequestStateExtended.OPEN)
        // new pull requests
        val draftPullRequest = RandomEntities.pullRequest().copy(
            author = otherAuthor,
            isDraft = true,
            state = PullRequestState.OPEN,
        )
        require(draftPullRequest.stateExtended == PullRequestStateExtended.DRAFT)
        val closedPullRequest = RandomEntities.pullRequest().copy(
            author = otherAuthor,
            isDraft = false,
            state = PullRequestState.CLOSED,
        )
        require(closedPullRequest.stateExtended == PullRequestStateExtended.CLOSED)
        val mergedPullRequest = RandomEntities.pullRequest().copy(
            author = otherAuthor,
            isDraft = false,
            state = PullRequestState.MERGED,
        )
        require(mergedPullRequest.stateExtended == PullRequestStateExtended.MERGED)

        val oldPullRequestWithoutChangesWithReviews = RandomEntities.pullRequestWithRepoAndReviews(pullRequest = oldPullRequestWithoutChanges)
        val pullRequestWithoutChangesWithReviews = RandomEntities.pullRequestWithRepoAndReviews(pullRequest = pullRequestWithoutChanges)
        val draftPullRequestWithReviewsFromFilteredUsername = RandomEntities.pullRequestWithRepoAndReviews(pullRequest = draftPullRequestFromFilteredUsername)
        val oldDraftPullRequestWithReviews = RandomEntities.pullRequestWithRepoAndReviews(pullRequest = oldDraftPullRequest)
        val openPullRequestWithReviews = RandomEntities.pullRequestWithRepoAndReviews(pullRequest = openPullRequest)
        val draftPullRequestWithReviews = RandomEntities.pullRequestWithRepoAndReviews(pullRequest = draftPullRequest)
        val closedPullRequestWithReviews = RandomEntities.pullRequestWithRepoAndReviews(pullRequest = closedPullRequest)
        val mergedPullRequestWithReviews = RandomEntities.pullRequestWithRepoAndReviews(pullRequest = mergedPullRequest)

        val oldPullRequestsWithReviews = listOf(
            oldPullRequestWithoutChangesWithReviews,
            oldDraftPullRequestWithReviews,
        )
        val newPullRequestsWithReviews = listOf(
            pullRequestWithoutChangesWithReviews,
            draftPullRequestWithReviewsFromFilteredUsername,
            draftPullRequestWithReviews,
            openPullRequestWithReviews,
            closedPullRequestWithReviews,
            mergedPullRequestWithReviews,
        )

        return oldPullRequestsWithReviews to newPullRequestsWithReviews
    }

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

        val (oldPullRequestsWithReviews, newPullRequestsWithReviews) = buildActivityChangedPullRequests()

        // When
        pullRequestService.sendNotifications(
            appSettings = appSettings,
            oldPullRequestsWithReviews = oldPullRequestsWithReviews,
            newPullRequestsWithReviews = newPullRequestsWithReviews,
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

        val (oldPullRequestsWithReviews, newPullRequestsWithReviews) = buildActivityChangedPullRequests()

        // When
        pullRequestService.sendNotifications(
            appSettings = appSettings,
            oldPullRequestsWithReviews = oldPullRequestsWithReviews,
            newPullRequestsWithReviews = newPullRequestsWithReviews,
        )

        // Then
        testNotificationSender.newPullRequestCount shouldBe 5
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

        val (oldPullRequestsWithReviews, newPullRequestsWithReviews) = buildActivityChangedPullRequests(
            filteredUsername = filteredUsername,
            otherUsername = otherUsername,
        )

        // When
        pullRequestService.sendNotifications(
            appSettings = appSettings,
            oldPullRequestsWithReviews = oldPullRequestsWithReviews,
            newPullRequestsWithReviews = newPullRequestsWithReviews,
        )

        // Then
        testNotificationSender.newPullRequestCount shouldBe 4
    }
})