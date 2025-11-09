package com.woowla.ghd.data.local.room.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.woowla.ghd.domain.entities.Deployment
import io.mcarle.konvert.api.KonvertFrom
import io.mcarle.konvert.api.KonvertTo
import kotlin.time.Instant

@Entity(
    tableName = "deployment",
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
@KonvertFrom(Deployment::class)
@KonvertTo(Deployment::class)
data class DbDeployment(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "repo_to_check_id") val repoToCheckId: Long,

    @ColumnInfo(name = "state") val state: String,
    @ColumnInfo(name = "environment") val environment: String?,
    @ColumnInfo(name = "refName") val refName: String?,
    @ColumnInfo(name = "refPrefix") val refPrefix: String?,
    @ColumnInfo(name = "created_at") val createdAt: Instant,
    @ColumnInfo(name = "updated_at") val updatedAt: Instant,
    @Embedded val author: DbAuthor?,
) {
    companion object
}
