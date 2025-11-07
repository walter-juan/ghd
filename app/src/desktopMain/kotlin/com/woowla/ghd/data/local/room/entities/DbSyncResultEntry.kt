package com.woowla.ghd.data.local.room.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.woowla.ghd.domain.entities.SyncResultEntry
import io.mcarle.konvert.api.KonvertFrom
import io.mcarle.konvert.api.KonvertTo
import kotlin.time.Instant

@Entity(
    tableName = "sync_result_entry",
    foreignKeys = [
        ForeignKey(
            entity = DbSyncResult::class,
            parentColumns = ["id"],
            childColumns = ["sync_result_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DbRepoToCheck::class,
            parentColumns = ["id"],
            childColumns = ["repo_to_check_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index(value = ["sync_result_id"]), Index(value = ["repo_to_check_id"])],
)
@KonvertFrom(SyncResultEntry::class)
@KonvertTo(SyncResultEntry::class)
data class DbSyncResultEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "sync_result_id") val syncResultId: Long,
    @ColumnInfo(name = "repo_to_check_id") val repoToCheckId: Long?,
    @ColumnInfo(name = "is_success") val isSuccess: Boolean,
    @ColumnInfo(name = "start_at") val startAt: Instant,
    @ColumnInfo(name = "end_at") val endAt: Instant,
    @ColumnInfo(name = "origin") val origin: String,
    @ColumnInfo(name = "error") val error: String?,
    @ColumnInfo(name = "error_message") val errorMessage: String?,
    @Embedded val rateLimit: DbSyncResultRateLimit?,
) {
    companion object
}