package com.woowla.ghd.data.local.room.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.woowla.ghd.domain.entities.PullRequest
import io.mcarle.konvert.api.KonvertFrom
import io.mcarle.konvert.api.KonvertTo
import kotlinx.datetime.Instant

@Entity(
    tableName = "pull_request",
    foreignKeys = [
        ForeignKey(
            entity = DbRepoToCheck::class,
            parentColumns = ["id"],
            childColumns = ["repo_to_check_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["repo_to_check_id"])],
)
@KonvertFrom(PullRequest::class)
@KonvertTo(PullRequest::class)
data class DbPullRequest(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "repo_to_check_id") val repoToCheckId: Long,

    @ColumnInfo(name = "number") val number: Long,
    @ColumnInfo(name = "url") val url: String,
    @ColumnInfo(name = "state") val state: String,
    @ColumnInfo(name = "title") val title: String?,
    @ColumnInfo(name = "created_at") val createdAt: Instant,
    @ColumnInfo(name = "updated_at") val updatedAt: Instant,
    @ColumnInfo(name = "merged_at") val mergedAt: Instant?,
    @ColumnInfo(name = "is_draft") val isDraft: Boolean,
    @ColumnInfo(name = "base_ref") val baseRef: String?,
    @ColumnInfo(name = "head_ref") val headRef: String?,
    @ColumnInfo(name = "total_comments_count") val totalCommentsCount: Long?,
    // TODO why the mergeable_state needed a default value?
    @ColumnInfo(name = "mergeable_state", defaultValue = "") val mergeableState: String,
    // TODO why the merge_state_status needed a default value?
    @ColumnInfo(name = "merge_state_status", defaultValue = "") val mergeStateStatus: String,
    @ColumnInfo(name = "last_commit_check_rollup_status") val lastCommitCheckRollupStatus: String,
    @ColumnInfo(name = "last_commit_sha1") val lastCommitSha1: String?,
    @Embedded val author: DbAuthor?,
) {
    companion object
}