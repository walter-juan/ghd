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

class PullRequestServiceStateNotificationsUnitTest : StringSpec({
    /**
     * Return a list of old pull request and new pull request for the activity changes.
     * This will contain:
     * - 1 pull request by [otherUsername] WITHOUT CHANGED
     *
     * - 1 new draft pull request by [filteredUsername]
     * - 1 pull request by [otherUsername] that changed from draft to open
     * - 1 pull request by [otherUsername] that changed from open to merged
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
        // pull request that changed from open to merged
        val oldOpenPullRequest = RandomEntities.pullRequest().copy(
            author = otherAuthor,
            isDraft = false,
            state = PullRequestState.OPEN,
        )
        require(oldOpenPullRequest.stateExtended == PullRequestStateExtended.OPEN)
        val mergedPullRequest = oldDraftPullRequest.copy(
            isDraft = false,
            state = PullRequestState.MERGED,
        )
        require(mergedPullRequest.stateExtended == PullRequestStateExtended.MERGED)
        // new pull requests
        val newDraftPullRequest = RandomEntities.pullRequest().copy(
            author = otherAuthor,
            isDraft = true,
            state = PullRequestState.OPEN,
        )
        require(newDraftPullRequest.stateExtended == PullRequestStateExtended.DRAFT)
        val newClosedPullRequest = RandomEntities.pullRequest().copy(
            author = otherAuthor,
            isDraft = false,
            state = PullRequestState.CLOSED,
        )
        require(newClosedPullRequest.stateExtended == PullRequestStateExtended.CLOSED)
        val newMergedPullRequest = RandomEntities.pullRequest().copy(
            author = otherAuthor,
            isDraft = false,
            state = PullRequestState.MERGED,
        )
        require(newMergedPullRequest.stateExtended == PullRequestStateExtended.MERGED)

        val repoToCheck = RandomEntities.repoToCheck().copy(
            arePullRequestsEnabled = true,
            arePullRequestsNotificationsEnabled = true,
            areReleasesEnabled = true,
            areReleasesNotificationsEnabled = true,
        )
        val oldPullRequestWithoutChangesWithReviews = RandomEntities.pullRequestWithRepoAndReviews(repoToCheck = repoToCheck, pullRequest = oldPullRequestWithoutChanges)
        val pullRequestWithoutChangesWithReviews = RandomEntities.pullRequestWithRepoAndReviews(repoToCheck = repoToCheck, pullRequest = pullRequestWithoutChanges)
        val draftPullRequestWithReviewsFromFilteredUsername = RandomEntities.pullRequestWithRepoAndReviews(repoToCheck = repoToCheck, pullRequest = draftPullRequestFromFilteredUsername)
        val oldDraftPullRequestWithReviews = RandomEntities.pullRequestWithRepoAndReviews(repoToCheck = repoToCheck, pullRequest = oldDraftPullRequest)
        val openPullRequestWithReviews = RandomEntities.pullRequestWithRepoAndReviews(repoToCheck = repoToCheck, pullRequest = openPullRequest)
        val oldOpenPullRequestWithReviews = RandomEntities.pullRequestWithRepoAndReviews(repoToCheck = repoToCheck, pullRequest = oldOpenPullRequest)
        val mergedPullRequestWithReviews = RandomEntities.pullRequestWithRepoAndReviews(repoToCheck = repoToCheck, pullRequest = mergedPullRequest)
        val newDraftPullRequestWithReviews = RandomEntities.pullRequestWithRepoAndReviews(repoToCheck = repoToCheck, pullRequest = newDraftPullRequest)
        val newClosedPullRequestWithReviews = RandomEntities.pullRequestWithRepoAndReviews(repoToCheck = repoToCheck, pullRequest = newClosedPullRequest)
        val newMergedPullRequestWithReviews = RandomEntities.pullRequestWithRepoAndReviews(repoToCheck = repoToCheck, pullRequest = newMergedPullRequest)

        val oldPullRequestsWithReviews = listOf(
            oldPullRequestWithoutChangesWithReviews,
            oldDraftPullRequestWithReviews,
            oldOpenPullRequestWithReviews,
        )
        val newPullRequestsWithReviews = listOf(
            pullRequestWithoutChangesWithReviews,
            draftPullRequestWithReviewsFromFilteredUsername,
            mergedPullRequestWithReviews,
            newDraftPullRequestWithReviews,
            openPullRequestWithReviews,
            newClosedPullRequestWithReviews,
            newMergedPullRequestWithReviews,
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
                stateEnabledOption = EnabledOption.ALL,
                stateOpenFromOthersPullRequestsEnabled = true,
                stateClosedFromOthersPullRequestsEnabled = true,
                stateMergedFromOthersPullRequestsEnabled = true,
                stateDraftFromOthersPullRequestsEnabled = true,
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
        testNotificationSender.newPullRequestCount shouldBe 6
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
                stateEnabledOption = EnabledOption.FILTERED,
                stateOpenFromOthersPullRequestsEnabled = true,
                stateClosedFromOthersPullRequestsEnabled = true,
                stateMergedFromOthersPullRequestsEnabled = true,
                stateDraftFromOthersPullRequestsEnabled = true,
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
        testNotificationSender.newPullRequestCount shouldBe 5
    }

    "when stateEnabledOption is FILTERED but all notifications disabled then sendNotifications should not send notifications" {
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
                stateEnabledOption = EnabledOption.FILTERED,
                stateOpenFromOthersPullRequestsEnabled = false,
                stateClosedFromOthersPullRequestsEnabled = false,
                stateMergedFromOthersPullRequestsEnabled = false,
                stateDraftFromOthersPullRequestsEnabled = false,
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
        testNotificationSender.newPullRequestCount shouldBe 0
    }
})