package com.woowla.ghd.domain.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant

@Entity(
    tableName = "review_seen",
    foreignKeys = [
        ForeignKey(
            entity = PullRequestSeen::class,
            parentColumns = ["id"],
            childColumns = ["pull_request_id"],
            onDelete = ForeignKey.CASCADE
        ),
    ],
    indices = [Index(value = ["pull_request_id"])],
)
data class ReviewSeen(
    @PrimaryKey override val id: String,
    @ColumnInfo(name = "pull_request_id") override val pullRequestId: String,
    @ColumnInfo(name = "submitted_at") override val submittedAt: Instant?,
    @ColumnInfo(name = "url") override val url: String,
    @ColumnInfo(name = "state") override val state: ReviewState,
    @Embedded override val author: Author?,
): ReviewBase()