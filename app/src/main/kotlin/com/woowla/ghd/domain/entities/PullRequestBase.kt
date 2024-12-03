package com.woowla.ghd.domain.entities

import androidx.room.Ignore
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration

abstract class PullRequestBase {
    abstract val id: String
    abstract val repoToCheckId: Long
    abstract val number: Long
    abstract val url: String
    abstract val state: PullRequestState
    abstract val title: String?
    abstract val createdAt: Instant
    abstract val updatedAt: Instant
    abstract val mergedAt: Instant?
    abstract val isDraft: Boolean
    abstract val baseRef: String?
    abstract val headRef: String?
    abstract val totalCommentsCount: Long?
    abstract val mergeStateStatus: MergeGitHubStateStatus
    abstract val lastCommitCheckRollupStatus: CommitCheckRollupStatus
    abstract val lastCommitSha1: String?
    abstract val author: Author?

    val canBeMerged
        @Ignore
        get() = mergeStateStatus == MergeGitHubStateStatus.CLEAN

    val stateExtended: PullRequestStateExtended
        @Ignore
        get() = state.toPullRequestStateExtended(isDraft = isDraft)
}

fun PullRequestBase.isOld(cleanUpTimeout: Long): Boolean {
    return if (stateExtended == PullRequestStateExtended.CLOSED || stateExtended == PullRequestStateExtended.MERGED) {
        val duration: Duration = Clock.System.now() - updatedAt
        duration.inWholeHours > cleanUpTimeout
    } else {
        false
    }
}