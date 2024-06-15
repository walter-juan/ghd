package com.woowla.ghd.domain.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import com.woowla.ghd.extensions.after
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration

@Entity(
    tableName = "pull_request",
    foreignKeys = [
        ForeignKey(
            entity = RepoToCheck::class,
            parentColumns = ["id"],
            childColumns = ["repo_to_check_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["repo_to_check_id"])],
)
data class PullRequest(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "repo_to_check_id") val repoToCheckId: Long,

    @ColumnInfo(name = "number") val number: Long,
    @ColumnInfo(name = "url") val url: String,
    @ColumnInfo(name = "state") val state: PullRequestState,
    @ColumnInfo(name = "title") val title: String?,
    @ColumnInfo(name = "created_at") val createdAt: Instant,
    @ColumnInfo(name = "updated_at") val updatedAt: Instant,
    @ColumnInfo(name = "merged_at") val mergedAt: Instant?,
    @ColumnInfo(name = "is_draft") val isDraft: Boolean,
    @ColumnInfo(name = "base_ref") val baseRef: String?,
    @ColumnInfo(name = "head_ref") val headRef: String?,
    @ColumnInfo(name = "app_seen_at") val appSeenAt: Instant?,
    @ColumnInfo(name = "total_comments_count") val totalCommentsCount: Long?,
    @ColumnInfo(name = "mergeable") val mergeable: MergeableGitHubState,
    @ColumnInfo(name = "last_commit_check_rollup_status") val lastCommitCheckRollupStatus: CommitCheckRollupStatus,
    @Embedded val author: Author?,
): Comparable<PullRequest> {
    companion object {
        val defaultComparator = compareBy<PullRequest> { it.stateWithDraft }.thenBy { it.appSeen }.thenByDescending { it.createdAt }
    }

    @Ignore
    val appSeen: Boolean = appSeenAt?.after(updatedAt) ?: false

    @Ignore
    val canBeMergedByMergeable = mergeable == MergeableGitHubState.MERGEABLE

    @Ignore
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

fun PullRequest.isOld(cleanUpTimeout: Long): Boolean {
    return if (stateWithDraft == PullRequestStateWithDraft.CLOSED || stateWithDraft == PullRequestStateWithDraft.MERGED) {
        val duration: Duration = Clock.System.now() - updatedAt
        duration.inWholeHours > cleanUpTimeout
    } else {
        false
    }
}