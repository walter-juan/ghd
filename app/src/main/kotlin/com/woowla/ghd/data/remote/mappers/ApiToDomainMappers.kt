package com.woowla.ghd.data.remote.mappers

import com.woowla.ghd.domain.entities.Author
import com.woowla.ghd.data.remote.GetLastReleaseQuery
import com.woowla.ghd.data.remote.fragment.PullRequestFragment
import com.woowla.ghd.domain.entities.CommitCheckRollupStatus
import com.woowla.ghd.domain.entities.MergeableGitHubState
import com.woowla.ghd.domain.entities.PullRequest
import com.woowla.ghd.domain.entities.PullRequestState
import com.woowla.ghd.domain.entities.PullRequestWithRepoAndReviews
import com.woowla.ghd.domain.entities.Release
import com.woowla.ghd.domain.entities.RepoToCheck
import com.woowla.ghd.domain.entities.Review
import com.woowla.ghd.domain.entities.ReviewState
import com.woowla.ghd.utils.enumValueOfOrDefault
import kotlinx.datetime.Instant

fun PullRequestFragment.Node.toPullRequest(repoToCheck: RepoToCheck, appSeenAt: Instant? = null): PullRequestWithRepoAndReviews {
    val lastCommitCheckRollupStatusString = commits.edges?.firstOrNull()?.node?.commit?.statusCheckRollup?.state?.toString()

    val pullRequest = PullRequest(
        id = id,
        number = number.toLong(),
        url = url.toString(),
        state = enumValueOfOrDefault(state.toString(), PullRequestState.UNKNOWN),
        title = title,
        createdAt = Instant.parse(createdAt.toString()),
        updatedAt = Instant.parse(updatedAt.toString()),
        mergedAt = mergedAt?.toString()?.let { Instant.parse(it) },
        isDraft = isDraft,
        baseRef = baseRefName,
        headRef = headRefName,
        author = author?.toAuthor(),
        appSeenAt = appSeenAt,
        totalCommentsCount = totalCommentsCount?.toLong(),
        mergeable = enumValueOfOrDefault(mergeable.toString(), MergeableGitHubState.UNKNOWN),
        lastCommitCheckRollupStatus = enumValueOfOrDefault(
            lastCommitCheckRollupStatusString,
            CommitCheckRollupStatus.UNKNOWN
        ),
        repoToCheckId = repoToCheck.id,
    )

    return PullRequestWithRepoAndReviews(
        pullRequest = pullRequest,
        reviews = latestReviews?.toReviews(pullRequestId = id) ?: listOf(),
        repoToCheck = repoToCheck,
    )
}

fun GetLastReleaseQuery.LatestRelease.toRelease(repoToCheck: RepoToCheck): Release {
    return Release(
        id = id,
        name = name,
        tagName = tagName,
        url = url.toString(),
        publishedAt = publishedAt?.toString()?.let { Instant.parse(it) },
        author = author?.toAuthor(),
        repoToCheckId = repoToCheck.id,
    )
}

fun PullRequestFragment.LatestReviews.toReviews(pullRequestId: String): List<Review> {
    return edges?.mapNotNull { edge ->
        edge?.node?.let { node ->
            Review(
                id = node.id,
                state = enumValueOfOrDefault(node.state.toString(), ReviewState.UNKNOWN),
                url = node.url.toString(),
                submittedAt = node.submittedAt?.toString()?.let { Instant.parse(it) },
                author = node.author?.toAuthor(),
                pullRequestId = pullRequestId,
            )
        }
    } ?: listOf()
}

fun PullRequestFragment.Author.toAuthor(): Author {
    return Author(
        login = login,
        url = url.toString(),
        avatarUrl = avatarUrl.toString(),
    )
}

fun GetLastReleaseQuery.Author.toAuthor(): Author {
    return Author(
        login = login,
        url = url.toString(),
        avatarUrl = avatarUrl.toString(),
    )
}

fun PullRequestFragment.Author1.toAuthor(): Author {
    return Author(
        login = login,
        url = url.toString(),
        avatarUrl = avatarUrl.toString(),
    )
}