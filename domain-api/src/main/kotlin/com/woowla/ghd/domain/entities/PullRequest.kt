package com.woowla.ghd.domain.entities

import kotlin.time.Duration
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class PullRequest(
    val id: String,
    val repoToCheckId: Long,
    val number: Long,
    val url: String,
    val state: PullRequestState,
    val title: String?,
    val createdAt: Instant,
    val updatedAt: Instant,
    val mergedAt: Instant?,
    val isDraft: Boolean,
    val baseRef: String?,
    val headRef: String?,
    val totalCommentsCount: Long?,
    val mergeableState: GitHubMergeableState,
    val mergeStateStatus: MergeGitHubStateStatus,
    val lastCommitCheckRollupStatus: CommitCheckRollupStatus,
    val lastCommitSha1: String?,
    val author: Author?,
) {
    val canBeMerged = mergeStateStatus == MergeGitHubStateStatus.CLEAN

    val stateExtended: PullRequestStateExtended = state.toPullRequestStateExtended(isDraft = isDraft)

    val hasConflicts = mergeableState == GitHubMergeableState.CONFLICTING

    val checkHaveErrors = lastCommitCheckRollupStatus == CommitCheckRollupStatus.ERROR || lastCommitCheckRollupStatus == CommitCheckRollupStatus.FAILURE || lastCommitCheckRollupStatus == CommitCheckRollupStatus.UNKNOWN
}

fun PullRequest.isOld(cleanUpTimeout: Long): Boolean {
    return if (stateExtended == PullRequestStateExtended.CLOSED || stateExtended == PullRequestStateExtended.MERGED) {
        val duration: Duration = Clock.System.now() - updatedAt
        duration.inWholeHours > cleanUpTimeout
    } else {
        false
    }
}