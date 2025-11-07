package com.woowla.ghd.data.local.room.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.woowla.ghd.domain.entities.Review
import io.mcarle.konvert.api.KonvertFrom
import io.mcarle.konvert.api.KonvertTo
import kotlin.time.Instant

@Entity(
    tableName = "review",
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
@KonvertFrom(Review::class)
@KonvertTo(Review::class)
data class DbReview(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "pull_request_id") val pullRequestId: String,
    @ColumnInfo(name = "submitted_at") val submittedAt: Instant?,
    @ColumnInfo(name = "url") val url: String,
    @ColumnInfo(name = "state") val state: String,
    @Embedded val author: DbAuthor?,
) {
    companion object
}