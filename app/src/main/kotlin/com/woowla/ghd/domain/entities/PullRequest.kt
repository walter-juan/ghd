package com.woowla.ghd.domain.entities

import com.woowla.ghd.extensions.after
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration

data class PullRequest(
    val id: String,
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
    val authorLogin: String?,
    val authorUrl: String?,
    val authorAvatarUrl: String?,
    val appSeenAt: Instant?,
    val totalCommentsCount: Long?,
    val lastCommitCheckRollupStatus: CommitCheckRollupStatus,
    val mergeable: MergeableGitHubState,
    val reviews: List<Review>,
    val repoToCheck: RepoToCheck
): Comparable<PullRequest> {
    companion object {
        val defaultComparator = compareBy<PullRequest> { it.stateWithDraft }.thenBy { it.appSeen }.thenByDescending { it.createdAt }
    }

    val appSeen: Boolean = appSeenAt?.after(updatedAt) ?: false

    val canBeMergedByMergeable = mergeable == MergeableGitHubState.MERGEABLE

    val canBeMergedByReviews = reviews.isNotEmpty() &&
            reviews.any { it.state == ReviewState.APPROVED } &&
            !reviews.any { it.state == ReviewState.CHANGES_REQUESTED }

    val canBeMerged = canBeMergedByMergeable && canBeMergedByReviews

    val stateWithDraft: PullRequestStateWithDraft = state.toPullRequestState(isDraft = isDraft)

    override fun compareTo(other: PullRequest): Int {
        return defaultComparator.compare(this, other)
    }

    private fun PullRequestState.toPullRequestState(isDraft: Boolean): PullRequestStateWithDraft {
        return when (this) {
            PullRequestState.OPEN -> if (isDraft) {
                PullRequestStateWithDraft.DRAFT
            } else {
                PullRequestStateWithDraft.OPEN
            }
            PullRequestState.MERGED -> PullRequestStateWithDraft.MERGED
            PullRequestState.CLOSED -> PullRequestStateWithDraft.CLOSED
            PullRequestState.UNKNOWN -> PullRequestStateWithDraft.UNKNOWN
        }
    }
}

/**
 * Return a list containing only the elements valid to store/show
 */
fun List<PullRequest>.filterSyncValid(syncSettings: SyncSettings): List<PullRequest> {
    return this.filter { pullRequest -> pullRequest.isSyncValid(syncSettings) }
}

/**
 * Return a list containing only the elements which are not valid to store/show.
 */
fun List<PullRequest>.filterNotSyncValid(syncSettings: SyncSettings): List<PullRequest> {
    return this.filterNot { pullRequest -> pullRequest.isSyncValid(syncSettings) }
}

fun PullRequest.isSyncValid(syncSettings: SyncSettings): Boolean {
    val cleanUpTimeout = syncSettings.getValidPullRequestCleanUpTimeout()
    val isOld = this.isOld(cleanUpTimeout)
    val hasBranchToExclude = this.hasBranchToExclude
    val pullsEnabled = this.repoToCheck.arePullRequestsEnabled

    return !isOld && !hasBranchToExclude && pullsEnabled
}

val PullRequest.hasBranchToExclude: Boolean
    get() {
        val regexStr = repoToCheck.pullBranchRegex
        return if (!headRef.isNullOrBlank() && !regexStr.isNullOrBlank()) {
            !headRef.matches(regexStr.toRegex())
        } else {
            false
        }
    }

fun PullRequest.isOld(cleanUpTimeout: Long): Boolean {
    return if (stateWithDraft == PullRequestStateWithDraft.CLOSED || stateWithDraft == PullRequestStateWithDraft.MERGED) {
        val duration: Duration = Clock.System.now() - updatedAt
        duration.inWholeHours > cleanUpTimeout
    } else {
        false
    }
}