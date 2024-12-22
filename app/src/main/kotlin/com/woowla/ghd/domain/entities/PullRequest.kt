package com.woowla.ghd.domain.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant

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
    @PrimaryKey override val id: String,
    @ColumnInfo(name = "repo_to_check_id") override val repoToCheckId: Long,

    @ColumnInfo(name = "number") override val number: Long,
    @ColumnInfo(name = "url") override val url: String,
    @ColumnInfo(name = "state") override val state: PullRequestState,
    @ColumnInfo(name = "title") override val title: String?,
    @ColumnInfo(name = "created_at") override val createdAt: Instant,
    @ColumnInfo(name = "updated_at") override val updatedAt: Instant,
    @ColumnInfo(name = "merged_at") override val mergedAt: Instant?,
    @ColumnInfo(name = "is_draft") override val isDraft: Boolean,
    @ColumnInfo(name = "base_ref") override val baseRef: String?,
    @ColumnInfo(name = "head_ref") override val headRef: String?,
    @ColumnInfo(name = "total_comments_count") override val totalCommentsCount: Long?,
    // TODO why the merge_state_status needed a default value?
    @ColumnInfo(name = "merge_state_status", defaultValue = "") override val mergeStateStatus: MergeGitHubStateStatus,
    @ColumnInfo(name = "last_commit_check_rollup_status") override val lastCommitCheckRollupStatus: CommitCheckRollupStatus,
    @ColumnInfo(name = "last_commit_sha1") override val lastCommitSha1: String?,
    @Embedded override val author: Author?,
): PullRequestBase()