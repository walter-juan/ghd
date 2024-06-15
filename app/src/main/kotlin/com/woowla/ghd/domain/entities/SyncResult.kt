package com.woowla.ghd.domain.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant
import kotlin.time.Duration

@Entity(tableName = "sync_result")
data class SyncResult(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "start_at")
    val startAt: Instant,
    @ColumnInfo(name = "end_at")
    val endAt: Instant?,
) {
    enum class Status { SUCCESS, WARNING, ERROR, CRITICAL }

    @Ignore
    val duration: Duration? = endAt?.minus(startAt)
}