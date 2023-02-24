package com.woowla.ghd.domain.entities

import com.woowla.ghd.domain.mappers.toPullRequestState
import com.woowla.ghd.extensions.after
import kotlinx.datetime.Instant

data class PullRequest(
    val id: String,
    val number: Long,
    val url: String,
    val gitHubState: PullRequestGitHubState,
    val title: String?,
    val createdAt: Instant,
    val updatedAt: Instant,
    val mergedAt: Instant?,
    val draft: Boolean,
    val baseRef: String?,
    val headRef: String?,
    val authorLogin: String?,
    val authorUrl: String?,
    val authorAvatarUrl: String?,
    val appSeenAt: Instant?,
    val totalCommentsCount: Long?,
    val repoToCheckId: Long,
    val lastCommitCheckRollupStatus: CommitCheckRollupStatus,
    val mergeable: MergeableGitHubState,
    val reviews: List<Review>,
    val repoToCheck: RepoToCheck
): Comparable<PullRequest> {
    companion object {
        val defaultComparator = compareBy<PullRequest> { it.state }.thenByDescending { it.updatedAt }
    }

    val appSeen: Boolean = appSeenAt?.after(updatedAt) ?: false

    val canBeMergedByMergeable = mergeable == MergeableGitHubState.MERGEABLE

    val canBeMergedByReviews = reviews.isNotEmpty() &&
            reviews.any { it.state == ReviewState.APPROVED } &&
            !reviews.any { it.state == ReviewState.CHANGES_REQUESTED }

    val canBeMerged = canBeMergedByMergeable && canBeMergedByReviews

    val state: PullRequestState = gitHubState.toPullRequestState(isDraft = draft)

    override fun compareTo(other: PullRequest): Int {
        return defaultComparator.compare(this, other)
    }
}