package com.woowla.ghd.domain.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant

@Entity(
    tableName = "review",
    foreignKeys = [
        ForeignKey(
            entity = PullRequest::class,
            parentColumns = ["id"],
            childColumns = ["pull_request_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["pull_request_id"])],
)
data class Review(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "pull_request_id") val pullRequestId: String,
    @ColumnInfo(name = "submitted_at") val submittedAt: Instant?,
    @ColumnInfo(name = "url") val url: String,
    @ColumnInfo(name = "state") val state: ReviewState,
    @Embedded val author: Author?,
): Comparable<Review> {
    companion object {
        val defaultComparator = compareByDescending<Review> { it.submittedAt }
    }

    override fun compareTo(other: Review): Int {
        return defaultComparator.compare(this, other)
    }

    fun reRequestedReview(): Boolean {
        return state == ReviewState.DISMISSED
    }
}

fun List<Review>.anyNonApproved(): Boolean {
    return any { it.state != ReviewState.APPROVED }
}

fun List<Review>.anyCommentedOrChangesRequested(): Boolean {
    return any { it.state == ReviewState.COMMENTED || it.state == ReviewState.CHANGES_REQUESTED || it.state == ReviewState.UNKNOWN }
}