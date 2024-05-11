package com.woowla.ghd.data.local.room.entities

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
            entity = DbRepoToCheck::class,
            parentColumns = ["id"],
            childColumns = ["repo_to_check_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["repo_to_check_id"])],
)
data class DbRelease(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "repo_to_check_id") val repoToCheckId: Long,

    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "tag_name") val tagName: String,
    @ColumnInfo(name = "url") val url: String,
    @ColumnInfo(name = "published_at") val publishedAt: Instant?,
    @Embedded val author: DbAuthor?,
)