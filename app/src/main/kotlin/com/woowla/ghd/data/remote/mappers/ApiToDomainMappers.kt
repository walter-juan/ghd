package com.woowla.ghd.data.remote.mappers

import com.woowla.ghd.domain.entities.Author
import com.woowla.ghd.data.remote.GetLastReleaseQuery
import com.woowla.ghd.data.remote.GetPullRequestsQuery
import com.woowla.ghd.domain.entities.CommitCheckRollupStatus
import com.woowla.ghd.domain.entities.MergeableGitHubState
import com.woowla.ghd.domain.entities.PullRequest
import com.woowla.ghd.domain.entities.PullRequestState
import com.woowla.ghd.domain.entities.Release
import com.woowla.ghd.domain.entities.RepoToCheck
import com.woowla.ghd.domain.entities.Review
import com.woowla.ghd.domain.entities.ReviewState
import com.woowla.ghd.utils.enumValueOfOrDefault
import kotlinx.datetime.Instant
import kotlinx.datetime.toInstant

fun GetPullRequestsQuery.Node.toPullRequest(repoToCheck: RepoToCheck, appSeenAt: Instant? = null): PullRequest {
    val lastCommitCheckRollupStatusString = commits.edges?.first()?.node?.commit?.statusCheckRollup?.state?.toString()

    return PullRequest(
        id = id,
        number = number.toLong(),
        url = url.toString(),
        state = enumValueOfOrDefault(state.toString(), PullRequestState.UNKNOWN),
        title = title,
        createdAt = createdAt.toString().toInstant(),
        updatedAt = updatedAt.toString().toInstant(),
        mergedAt = mergedAt?.toString()?.toInstant(),
        isDraft = isDraft,
        baseRef = baseRefName,
        headRef = headRefName,
        authorLogin = author?.login,
        authorUrl = author?.url?.toString(),
        authorAvatarUrl = author?.avatarUrl?.toString(),
        appSeenAt = appSeenAt,
        totalCommentsCount = totalCommentsCount?.toLong(),
        mergeable = enumValueOfOrDefault(mergeable.toString(), MergeableGitHubState.UNKNOWN),
        lastCommitCheckRollupStatus = enumValueOfOrDefault(
            lastCommitCheckRollupStatusString,
            CommitCheckRollupStatus.UNKNOWN
        ),
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
        publishedAt = publishedAt?.toString()?.toInstant(),
        authorLogin = author?.login,
        authorUrl = author?.url?.toString(),
        authorAvatarUrl = author?.avatarUrl?.toString(),
        repoToCheck = repoToCheck,
    )
}

fun GetPullRequestsQuery.LatestReviews.toReviews(pullRequestId: String): List<Review> {
    return edges?.mapNotNull { edge ->
        edge?.node?.let { node ->
            Review(
                id = node.id,
                state = enumValueOfOrDefault(node.state.toString(), ReviewState.UNKNOWN),
                url = node.url.toString(),
                submittedAt = node.submittedAt?.toString()?.toInstant(),
                author = node.author?.toAuthor(),
                pullRequestId = pullRequestId,
            )
        }
    } ?: listOf()
}

fun GetPullRequestsQuery.Author1.toAuthor(): Author {
    return Author(
        login = login,
        url = url.toString(),
        avatarUrl = avatarUrl.toString(),
    )
}