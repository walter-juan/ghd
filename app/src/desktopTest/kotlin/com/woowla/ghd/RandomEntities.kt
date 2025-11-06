package com.woowla.ghd

import com.woowla.ghd.domain.entities.AppSettings
import com.woowla.ghd.domain.entities.Author
import com.woowla.ghd.domain.entities.CommitCheckRollupStatus
import com.woowla.ghd.domain.entities.GitHubMergeableState
import com.woowla.ghd.domain.entities.Repository
import com.woowla.ghd.domain.entities.MergeGitHubStateStatus
import com.woowla.ghd.domain.entities.NotificationsSettings
import com.woowla.ghd.domain.entities.PullRequest
import com.woowla.ghd.domain.entities.PullRequestState
import com.woowla.ghd.domain.entities.PullRequestWithRepoAndReviews
import com.woowla.ghd.domain.entities.Release
import com.woowla.ghd.domain.entities.ReleaseWithRepo
import com.woowla.ghd.domain.entities.RepoToCheck
import com.woowla.ghd.domain.entities.Review
import com.woowla.ghd.domain.entities.ReviewRequest
import com.woowla.ghd.domain.entities.ReviewState

object RandomEntities {
    fun appSettings() = AppSettings(
        darkTheme = RandomValues.randomBoolean(),
        notificationsSettings = notificationsSettings(),
        filtersPullRequestState = emptySet(),
        filtersReleaseGroupName = emptySet(),
        filtersRepoToCheckGroupName = emptySet()
    )

    fun notificationsSettings() = NotificationsSettings(
        filterUsername = RandomValues.randomString(),
        stateEnabledOption = NotificationsSettings.EnabledOption.entries.random(),
        stateOpenFromOthersPullRequestsEnabled = RandomValues.randomBoolean(),
        stateClosedFromOthersPullRequestsEnabled = RandomValues.randomBoolean(),
        stateMergedFromOthersPullRequestsEnabled = RandomValues.randomBoolean(),
        stateDraftFromOthersPullRequestsEnabled = RandomValues.randomBoolean(),
        activityEnabledOption = NotificationsSettings.EnabledOption.entries.random(),
        activityReviewsFromYourPullRequestsEnabled = RandomValues.randomBoolean(),
        activityReviewsFromYouDismissedEnabled = RandomValues.randomBoolean(),
        activityChecksFromYourPullRequestsEnabled = RandomValues.randomBoolean(),
        activityMergeableFromYourPullRequestsEnabled = RandomValues.randomBoolean(),
        newReleaseEnabled = RandomValues.randomBoolean()
    )

    fun pullRequest(
        repoToCheckId: Long = RandomValues.randomLong(),
        author: Author = author(),
    ) = PullRequest(
        id = RandomValues.randomId(),
        repoToCheckId = repoToCheckId,
        number = RandomValues.randomLong(),
        url = RandomValues.randomUrl(),
        state = PullRequestState.entries.random(),
        title = RandomValues.randomString(),
        createdAt = RandomValues.randomInstant(),
        updatedAt = RandomValues.randomInstant(),
        mergedAt = RandomValues.randomInstant(),
        closedAt = RandomValues.randomInstant(),
        isDraft = RandomValues.randomBoolean(),
        baseRef = RandomValues.randomString(),
        headRef = RandomValues.randomString(),
        totalCommentsCount = RandomValues.randomLong(),
        mergeStateStatus = MergeGitHubStateStatus.entries.random(),
        lastCommitCheckRollupStatus = CommitCheckRollupStatus.entries.random(),
        lastCommitSha1 = RandomValues.randomString(),
        author = author,
        mergeableState = GitHubMergeableState.entries.random(),
    )

    fun pullRequestWithRepoAndReviews(
        repoToCheck: RepoToCheck = repoToCheck(),
        pullRequest: PullRequest = pullRequest(),
        reviews: List<Review> = listOf(review()),
        reviewRequests: List<ReviewRequest> = listOf(reviewRequest()),
    ) = PullRequestWithRepoAndReviews(
        pullRequest = pullRequest,
        repoToCheck = repoToCheck,
        reviews = reviews,
        reviewRequests = reviewRequests,
    )

    fun release(
        repoToCheckId: Long = RandomValues.randomLong(),
    ) = Release(
        id = RandomValues.randomId(),
        repoToCheckId = repoToCheckId,
        name = RandomValues.randomString(),
        tagName = RandomValues.randomString(),
        url = RandomValues.randomUrl(),
        publishedAt = RandomValues.randomInstant(),
        author = author(),
    )

    fun releaseWithRepo(repoToCheck: RepoToCheck = repoToCheck()) = ReleaseWithRepo(
        release = release(),
        repoToCheck = repoToCheck,
    )

    fun repoToCheck(): RepoToCheck = RepoToCheck(
        id = RandomValues.randomLong(),
        repository = randomGitHubRepository(),
        groupName = RandomValues.randomString(),
        pullBranchRegex = RandomValues.randomString(),
        arePullRequestsEnabled = RandomValues.randomBoolean(),
        arePullRequestsNotificationsEnabled = RandomValues.randomBoolean(),
        areReleasesEnabled = RandomValues.randomBoolean(),
        areReleasesNotificationsEnabled = RandomValues.randomBoolean(),
    )

    fun randomGitHubRepository() = Repository(
        owner = RandomValues.randomString(),
        name = RandomValues.randomString(),
    )

    fun review(
        pullRequestId: String = RandomValues.randomString(),
    ): Review = Review(
        id = RandomValues.randomId(),
        pullRequestId = pullRequestId,
        submittedAt = RandomValues.randomInstant(),
        url = RandomValues.randomUrl(),
        state = ReviewState.entries.random(),
        author = author()
    )

    fun reviewRequest(
        pullRequestId: String = RandomValues.randomString(),
    ): ReviewRequest = ReviewRequest(
        id = RandomValues.randomId(),
        pullRequestId = pullRequestId,
        author = author()
    )

    fun author() = Author(
        login = RandomValues.randomString(),
        url = RandomValues.randomUrl(),
        avatarUrl = RandomValues.randomUrl(),
    )
}