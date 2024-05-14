package com.woowla.ghd.data.local.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant

@Entity(tableName = "sync_result")
data class DbSyncResult(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "start_at") val startAt: Instant,
    @ColumnInfo(name = "end_at") val endAt: Instant?
)