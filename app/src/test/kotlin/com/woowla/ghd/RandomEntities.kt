package com.woowla.ghd

import com.woowla.ghd.domain.entities.Author
import com.woowla.ghd.domain.entities.CommitCheckRollupStatus
import com.woowla.ghd.domain.entities.MergeGitHubStateStatus
import com.woowla.ghd.domain.entities.PullRequest
import com.woowla.ghd.domain.entities.PullRequestState
import com.woowla.ghd.domain.entities.RepoToCheck
import com.woowla.ghd.domain.entities.Review
import com.woowla.ghd.domain.entities.ReviewState

object RandomEntities {
    fun pullRequest(
        repoToCheckId: Long = RandomValues.randomLong(),
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
        isDraft = RandomValues.randomBoolean(),
        baseRef = RandomValues.randomString(),
        headRef = RandomValues.randomString(),
        totalCommentsCount = RandomValues.randomLong(),
        mergeStateStatus = MergeGitHubStateStatus.entries.random(),
        lastCommitCheckRollupStatus = CommitCheckRollupStatus.entries.random(),
        lastCommitSha1 = RandomValues.randomString(),
        author = author(),
    )

    fun repoToCheck(): RepoToCheck = RepoToCheck(
        id = RandomValues.randomLong(),
        owner = RandomValues.randomString(),
        name = RandomValues.randomString(),
        groupName = RandomValues.randomString(),
        pullBranchRegex = RandomValues.randomString(),
        arePullRequestsEnabled = false,
        areReleasesEnabled = false
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

    fun author() = Author(
        login = RandomValues.randomString(),
        url = RandomValues.randomUrl(),
        avatarUrl = RandomValues.randomUrl(),
    )
}