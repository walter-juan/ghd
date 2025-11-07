package com.woowla.ghd.data.local.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.woowla.ghd.domain.entities.SyncResult
import io.mcarle.konvert.api.KonvertFrom
import io.mcarle.konvert.api.KonvertTo
import kotlin.time.Instant

@Entity(tableName = "sync_result")
@KonvertFrom(SyncResult::class)
@KonvertTo(SyncResult::class)
data class DbSyncResult(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "start_at")
    val startAt: Instant,
    @ColumnInfo(name = "end_at")
    val endAt: Instant?,
) {
    companion object
}