package com.woowla.ghd.data.local.room.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.woowla.ghd.domain.entities.ReviewRequest
import io.mcarle.konvert.api.KonvertFrom
import io.mcarle.konvert.api.KonvertTo

@Entity(
    tableName = "review_request",
    foreignKeys = [
        ForeignKey(
            entity = DbPullRequest::class,
            parentColumns = ["id"],
            childColumns = ["pull_request_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["pull_request_id"])],
)
@KonvertFrom(ReviewRequest::class)
@KonvertTo(ReviewRequest::class)
data class DbReviewRequest(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "pull_request_id") val pullRequestId: String,
    @Embedded val author: DbAuthor?,
) {
    companion object
}