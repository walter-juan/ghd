package com.woowla.ghd.domain.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant

@Entity(
    tableName = "release",
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
data class Release(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "repo_to_check_id") val repoToCheckId: Long,

    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "tag_name") val tagName: String,
    @ColumnInfo(name = "url") val url: String,
    @ColumnInfo(name = "published_at") val publishedAt: Instant?,
    @Embedded val author: Author?,
): Comparable<Release> {
    companion object {
        val defaultComparator = compareByDescending<Release> { it.publishedAt }
    }

    override fun compareTo(other: Release): Int {
        return defaultComparator.compare(this, other)
    }
}
