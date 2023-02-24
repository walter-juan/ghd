package com.woowla.ghd.domain.mappers

import com.woowla.ghd.data.remote.GetLastReleaseQuery
import com.woowla.ghd.data.remote.GetPullRequestsQuery
import com.woowla.ghd.domain.requests.UpsertPullRequestRequest
import com.woowla.ghd.domain.requests.UpsertReleaseRequest
import com.woowla.ghd.domain.requests.UpsertReviewRequest
import kotlinx.datetime.Instant
import kotlinx.datetime.toInstant

fun GetPullRequestsQuery.Node.toUpsertPullRequestRequest(repoToCheckId: Long, appSeenAt: Instant? = null): UpsertPullRequestRequest {
    return UpsertPullRequestRequest(
        id = id,
        number = number.toLong(),
        url = url.toString(),
        state = state.toString(),
        title = title,
        createdAt = createdAt.toString().toInstant(),
        updatedAt = updatedAt.toString().toInstant(),
        mergedAt = mergedAt?.toString()?.toInstant(),
        draft = isDraft,
        baseRef = baseRefName,
        headRef = headRefName,
        authorLogin = author?.login,
        authorUrl = author?.url?.toString(),
        authorAvatarUrl = author?.avatarUrl?.toString(),
        appSeenAt = appSeenAt,
        totalCommentsCount = totalCommentsCount?.toLong(),
        mergeable = mergeable.toString(),
        lastCommitCheckRollupStatus = commits.edges?.first()?.node?.commit?.statusCheckRollup?.state?.toString(),
        repoToCheckId = repoToCheckId
    )
}

fun GetLastReleaseQuery.LatestRelease.toUpsertReleaseRequest(repoToCheckId: Long): UpsertReleaseRequest {
    return UpsertReleaseRequest(
        id = id,
        name = name,
        tagName = tagName,
        url = url.toString(),
        publishedAt = publishedAt?.toString()?.toInstant(),
        authorLogin = author?.login,
        authorUrl = author?.url?.toString(),
        authorAvatarUrl = author?.avatarUrl?.toString(),
        repoToCheckId = repoToCheckId
    )
}

fun GetPullRequestsQuery.LatestReviews.toUpsertReviewRequests(pullRequestId: String): List<UpsertReviewRequest> {
    return edges?.mapNotNull { edge ->
        edge?.node?.let { node ->
            UpsertReviewRequest(
                id = node.id,
                state = node.state.toString(),
                url = node.url.toString(),
                submittedAt = node.submittedAt?.toString()?.toInstant(),
                authorLogin = node.author?.login,
                authorUrl = node.author?.url?.toString(),
                authorAvatarUrl = node.author?.avatarUrl?.toString(),
                pullRequestId = pullRequestId
            )
        }
    } ?: listOf()
}
