package com.woowla.ghd.domain.services

import com.woowla.ghd.RandomEntities
import com.woowla.ghd.TestNotificationsSender
import com.woowla.ghd.domain.entities.CommitCheckRollupStatus
import com.woowla.ghd.domain.entities.MergeGitHubStateStatus
import com.woowla.ghd.domain.entities.NotificationsSettings.EnabledOption
import com.woowla.ghd.domain.entities.PullRequestState
import com.woowla.ghd.domain.entities.PullRequestWithRepoAndReviews
import com.woowla.ghd.domain.entities.ReviewState
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk

class PullRequestServiceActivityNotificationsUnitTest : ShouldSpec({
    /**
     * Return a list of old pull request and new pull request for the activity changes.
     * This will contain:
     * - 1 Pull request which is newly created
     * - 1 Pull Request with no activity changes
     * - 1 Pull Request with checks activity changes
     * - 1 Pull Request with mergeable activity changes
     * - 1 Pull Request with review added
     * - 1 Pull Request with review changed from commented to approved
     * - 1 Pull Request with review changed from commented to dismissed (re-review required)
     */
    fun buildActivityChangedPullRequests(
        pullRequestsAuthorUsername: String = "author-login",
        reviewsAuthorUsername: String = "author-login",
    ): Pair<List<PullRequestWithRepoAndReviews>, List<PullRequestWithRepoAndReviews>> {
        // pull requests author
        val pullRequestAuthor = RandomEntities.author().copy(
            login = pullRequestsAuthorUsername,
        )
        // reviews author
        val reviewsAuthor = RandomEntities.author().copy(
            login = reviewsAuthorUsername,
        )

        // newly created
        val newCreatedPullRequest = RandomEntities.pullRequest().copy(
            id = "pull-id-9",
            title = "no activity",
            author = pullRequestAuthor,
            state = PullRequestState.OPEN,
        )
        // no activity changes
        val oldNoActivityPullRequest = RandomEntities.pullRequest().copy(
            id = "pull-id-10",
            title = "no activity",
            author = pullRequestAuthor,
            state = PullRequestState.OPEN,
        )
        val noActivityPullRequest = oldNoActivityPullRequest.copy()
        // checks activity changes
        val oldChecksActivityPullRequest = RandomEntities.pullRequest().copy(
            id = "pull-id-11",
            title = "checks activity",
            author = pullRequestAuthor,
            lastCommitCheckRollupStatus = CommitCheckRollupStatus.PENDING,
            mergeStateStatus = MergeGitHubStateStatus.CLEAN,
        )
        val checksActivityPullRequest = oldChecksActivityPullRequest.copy(
            lastCommitCheckRollupStatus = CommitCheckRollupStatus.SUCCESS,
        )
        // mergeable
        val oldMergeableActivityPullRequest = RandomEntities.pullRequest().copy(
            id = "pull-id-12",
            title = "mergeable activity",
            author = pullRequestAuthor,
            mergeStateStatus = MergeGitHubStateStatus.BLOCKED,
            lastCommitCheckRollupStatus = CommitCheckRollupStatus.SUCCESS,
        )
        val mergeableActivityPullRequest = oldMergeableActivityPullRequest.copy(
            mergeStateStatus = MergeGitHubStateStatus.CLEAN,
        )
        // review added
        val oldReviewAddedActivityPullRequest = RandomEntities.pullRequest().copy(
            id = "pull-id-13",
            title = "review added activity",
            author = pullRequestAuthor,
        )
        val reviewAddedActivityPullRequest = oldReviewAddedActivityPullRequest.copy()
        val reviewAdded = RandomEntities.review().copy(
            author = reviewsAuthor,
            state = ReviewState.APPROVED,
        )
        // review changed
        val oldReviewChangedActivityPullRequest = RandomEntities.pullRequest().copy(
            id = "pull-id-14",
            title = "review changed activity",
            author = pullRequestAuthor,
        )
        val reviewChangedActivityPullRequest = oldReviewChangedActivityPullRequest.copy()
        val oldReviewChanged = RandomEntities.review().copy(
            author = reviewsAuthor,
            state = ReviewState.COMMENTED,
        )
        val reviewChanged = oldReviewChanged.copy(
            state = ReviewState.APPROVED,
        )
        // re-review required
        val oldReReviewActivityPullRequest = RandomEntities.pullRequest().copy(
            id = "pull-id-15",
            title = "re-review activity",
            author = pullRequestAuthor,
        )
        val reReviewActivityPullRequest = oldReReviewActivityPullRequest.copy()
        val oldReReview = RandomEntities.review().copy(
            author = reviewsAuthor,
            state = ReviewState.COMMENTED,
        )
        val reReview = oldReReview.copy(
            state = ReviewState.DISMISSED,
        )

        val repoToCheck = RandomEntities.repoToCheck().copy(
            arePullRequestsEnabled = true,
            arePullRequestsNotificationsEnabled = true,
            areReleasesEnabled = true,
            areReleasesNotificationsEnabled = true,
        )
        val newCreatedPullRequestWithReviews = RandomEntities.pullRequestWithRepoAndReviews(
            repoToCheck = repoToCheck,
            pullRequest = newCreatedPullRequest,
            reviews = listOf()
        )
        val oldNoActivityPullRequestWithReviews = RandomEntities.pullRequestWithRepoAndReviews(
            repoToCheck = repoToCheck,
            pullRequest = oldNoActivityPullRequest,
            reviews = listOf()
        )
        val noActivityPullRequestWithReviews = RandomEntities.pullRequestWithRepoAndReviews(
            repoToCheck = repoToCheck,
            pullRequest = noActivityPullRequest,
            reviews = listOf()
        )
        val oldChecksActivityPullRequestWithReviews = RandomEntities.pullRequestWithRepoAndReviews(
            repoToCheck = repoToCheck,
            pullRequest = oldChecksActivityPullRequest,
            reviews = listOf()
        )
        val checksActivityPullRequestWithReviews = RandomEntities.pullRequestWithRepoAndReviews(
            repoToCheck = repoToCheck,
            pullRequest = checksActivityPullRequest,
            reviews = listOf()
        )
        val oldMergeableActivityPullRequestWithReviews = RandomEntities.pullRequestWithRepoAndReviews(
            repoToCheck = repoToCheck,
            pullRequest = oldMergeableActivityPullRequest,
            reviews = listOf()
        )
        val mergeableActivityPullRequestWithReviews = RandomEntities.pullRequestWithRepoAndReviews(
            repoToCheck = repoToCheck,
            pullRequest = mergeableActivityPullRequest,
            reviews = listOf()
        )
        val oldReviewAddedActivityPullRequestWithReviews = RandomEntities.pullRequestWithRepoAndReviews(
            repoToCheck = repoToCheck,
            pullRequest = oldReviewAddedActivityPullRequest,
            reviews = listOf()
        )
        val reviewAddedActivityPullRequestWithReviews = RandomEntities.pullRequestWithRepoAndReviews(
            repoToCheck = repoToCheck,
            pullRequest = reviewAddedActivityPullRequest,
            reviews = listOf(reviewAdded)
        )
        val oldReviewChangedActivityPullRequestWithReviews = RandomEntities.pullRequestWithRepoAndReviews(
            repoToCheck = repoToCheck,
            pullRequest = oldReviewChangedActivityPullRequest,
            reviews = listOf(oldReviewChanged)
        )
        val reviewChangedActivityPullRequestWithReviews = RandomEntities.pullRequestWithRepoAndReviews(
            repoToCheck = repoToCheck,
            pullRequest = reviewChangedActivityPullRequest,
            reviews = listOf(reviewChanged)
        )
        val oldReReviewActivityPullRequestWithReviews = RandomEntities.pullRequestWithRepoAndReviews(
            repoToCheck = repoToCheck,
            pullRequest = oldReReviewActivityPullRequest,
            reviews = listOf(oldReReview)
        )
        val reReviewActivityPullRequestWithReviews = RandomEntities.pullRequestWithRepoAndReviews(
            repoToCheck = repoToCheck,
            pullRequest = reReviewActivityPullRequest,
            reviews = listOf(reReview)
        )

        return listOf(
            oldNoActivityPullRequestWithReviews,
            oldChecksActivityPullRequestWithReviews,
            oldMergeableActivityPullRequestWithReviews,
            oldReviewAddedActivityPullRequestWithReviews,
            oldReviewChangedActivityPullRequestWithReviews,
            oldReReviewActivityPullRequestWithReviews,
        ) to listOf(
            newCreatedPullRequestWithReviews,
            noActivityPullRequestWithReviews,
            checksActivityPullRequestWithReviews,
            mergeableActivityPullRequestWithReviews,
            reviewAddedActivityPullRequestWithReviews,
            reviewChangedActivityPullRequestWithReviews,
            reReviewActivityPullRequestWithReviews,
        )
    }

    context("when activityEnabledOption is NONE then sendNotifications") {
        // Given
        val testNotificationSender = TestNotificationsSender()
        val pullRequestService = PullRequestServiceImpl(
            localDataSource = mockk(),
            remoteDataSource = mockk(),
            notificationsSender = testNotificationSender,
            appSettingsService = mockk(),
            appLogger = mockk(relaxed = true),
        )
        val appSettings = RandomEntities.appSettings().copy(
            notificationsSettings = RandomEntities.notificationsSettings().copy(
                activityEnabledOption = EnabledOption.NONE,
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
        should("should not send notifications for new reviews and changed reviews") {
            testNotificationSender.newPullRequestReviewCount shouldBe 0
        }
        should("should not send notifications for re-request reviews") {
            testNotificationSender.yourPullRequestReviewDismissed shouldBe 0
        }
        should("should not send notifications for checks changes") {
            testNotificationSender.changePullRequestChecksCount shouldBe 0
        }
        should("should not send notifications for mergeable available") {
            testNotificationSender.mergeablePullRequestCount shouldBe 0
        }
    }

    context("when activityEnabledOption is ALL then sendNotifications") {
        // Given
        val testNotificationSender = TestNotificationsSender()
        val pullRequestService = PullRequestServiceImpl(
            localDataSource = mockk(),
            remoteDataSource = mockk(),
            notificationsSender = testNotificationSender,
            appSettingsService = mockk(),
            appLogger = mockk(relaxed = true),
        )
        val appSettings = RandomEntities.appSettings().copy(
            notificationsSettings = RandomEntities.notificationsSettings().copy(
                activityEnabledOption = EnabledOption.ALL,
                activityReviewsFromYourPullRequestsEnabled = true,
                activityReviewsFromYouDismissedEnabled = true,
                activityChecksFromYourPullRequestsEnabled = true,
                activityMergeableFromYourPullRequestsEnabled = true,
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
        should("should send notifications for new reviews and changed reviews") {
            testNotificationSender.newPullRequestReviewCount shouldBe 2
        }
        should("should send notifications for re-request reviews") {
            testNotificationSender.yourPullRequestReviewDismissed shouldBe 0
        }
        should("should send notifications for checks changes") {
            testNotificationSender.changePullRequestChecksCount shouldBe 1
        }
        should("should send notifications for mergeable available") {
            testNotificationSender.mergeablePullRequestCount shouldBe 1
        }
    }

    context("when activityEnabledOption is FILTERED then sendNotifications with all pull requests and reviews from other authors") {
        // Given
        val filterUsername = "filtered-username"
        val otherUsername = "other-username"
        val testNotificationSender = TestNotificationsSender()
        val pullRequestService = PullRequestServiceImpl(
            localDataSource = mockk(),
            remoteDataSource = mockk(),
            notificationsSender = testNotificationSender,
            appSettingsService = mockk(),
            appLogger = mockk(relaxed = true),
        )
        val appSettings = RandomEntities.appSettings().copy(
            notificationsSettings = RandomEntities.notificationsSettings().copy(
                filterUsername = filterUsername,
                activityEnabledOption = EnabledOption.FILTERED,
                activityReviewsFromYourPullRequestsEnabled = true,
                activityReviewsFromYouDismissedEnabled = true,
                activityChecksFromYourPullRequestsEnabled = true,
                activityMergeableFromYourPullRequestsEnabled = true,
            )
        )
        val (oldPullRequestsWithReviews, newPullRequestsWithReviews) = buildActivityChangedPullRequests(
            pullRequestsAuthorUsername = otherUsername,
            reviewsAuthorUsername = otherUsername,
        )

        // When
        pullRequestService.sendNotifications(
            appSettings = appSettings,
            oldPullRequestsWithReviews = oldPullRequestsWithReviews,
            newPullRequestsWithReviews = newPullRequestsWithReviews,
        )

        // Then
        should("should not send notifications for new reviews and changed reviews") {
            testNotificationSender.newPullRequestReviewCount shouldBe 0
        }
        should("should not send notifications for re-request reviews") {
            testNotificationSender.yourPullRequestReviewDismissed shouldBe 0
        }
        should("should not send notifications for checks changes") {
            testNotificationSender.changePullRequestChecksCount shouldBe 0
        }
        should("should not send notifications for mergeable available") {
            testNotificationSender.mergeablePullRequestCount shouldBe 0
        }
    }

    context("when activityEnabledOption is FILTERED then sendNotifications with all pull requests and reviews from filtered username") {
        // Given
        val filterUsername = "filtered-username"
        val testNotificationSender = TestNotificationsSender()
        val pullRequestService = PullRequestServiceImpl(
            localDataSource = mockk(),
            remoteDataSource = mockk(),
            notificationsSender = testNotificationSender,
            appSettingsService = mockk(),
            appLogger = mockk(relaxed = true),
        )
        val appSettings = RandomEntities.appSettings().copy(
            notificationsSettings = RandomEntities.notificationsSettings().copy(
                filterUsername = filterUsername,
                activityEnabledOption = EnabledOption.FILTERED,
                activityReviewsFromYourPullRequestsEnabled = true,
                activityReviewsFromYouDismissedEnabled = true,
                activityChecksFromYourPullRequestsEnabled = true,
                activityMergeableFromYourPullRequestsEnabled = true,
            )
        )
        val (oldPullRequestsWithReviews, newPullRequestsWithReviews) = buildActivityChangedPullRequests(
            pullRequestsAuthorUsername = filterUsername,
            reviewsAuthorUsername = filterUsername,
        )

        // When
        pullRequestService.sendNotifications(
            appSettings = appSettings,
            oldPullRequestsWithReviews = oldPullRequestsWithReviews,
            newPullRequestsWithReviews = newPullRequestsWithReviews,
        )

        // Then
        should("should send notifications for new reviews and changed reviews") {
            testNotificationSender.newPullRequestReviewCount shouldBe 2
        }
        should("should send notifications for re-request reviews") {
            testNotificationSender.yourPullRequestReviewDismissed shouldBe 1
        }
        should("should send notifications for checks changes") {
            testNotificationSender.changePullRequestChecksCount shouldBe 1
        }
        should("should send notifications for mergeable available") {
            testNotificationSender.mergeablePullRequestCount shouldBe 1
        }
    }

    context("when activityEnabledOption is FILTERED but all notifications disabled then sendNotifications with all pull requests and reviews from filtered username should not send notifications") {
        // Given
        val filterUsername = "filtered-username"
        val testNotificationSender = TestNotificationsSender()
        val pullRequestService = PullRequestServiceImpl(
            localDataSource = mockk(),
            remoteDataSource = mockk(),
            notificationsSender = testNotificationSender,
            appSettingsService = mockk(),
            appLogger = mockk(relaxed = true),
        )
        val appSettings = RandomEntities.appSettings().copy(
            notificationsSettings = RandomEntities.notificationsSettings().copy(
                filterUsername = filterUsername,
                activityEnabledOption = EnabledOption.FILTERED,
                activityReviewsFromYourPullRequestsEnabled = false,
                activityReviewsFromYouDismissedEnabled = false,
                activityChecksFromYourPullRequestsEnabled = false,
                activityMergeableFromYourPullRequestsEnabled = false,
            )
        )
        val (oldPullRequestsWithReviews, newPullRequestsWithReviews) = buildActivityChangedPullRequests(
            pullRequestsAuthorUsername = filterUsername,
            reviewsAuthorUsername = filterUsername,
        )

        // When
        pullRequestService.sendNotifications(
            appSettings = appSettings,
            oldPullRequestsWithReviews = oldPullRequestsWithReviews,
            newPullRequestsWithReviews = newPullRequestsWithReviews,
        )

        // Then
        should("should send notifications for new reviews and changed reviews") {
            testNotificationSender.newPullRequestReviewCount shouldBe 0
        }
        should("should send notifications for re-request reviews") {
            testNotificationSender.yourPullRequestReviewDismissed shouldBe 0
        }
        should("should send notifications for checks changes") {
            testNotificationSender.changePullRequestChecksCount shouldBe 0
        }
        should("should send notifications for mergeable available") {
            testNotificationSender.mergeablePullRequestCount shouldBe 0
        }
    }
})