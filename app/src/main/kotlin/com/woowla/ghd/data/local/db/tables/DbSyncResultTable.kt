package com.woowla.ghd.data.local.db.tables

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object DbSyncResultTable : LongIdTable(name = "sync_result") {
    val startAt = timestamp("start_at")
    val endAt = timestamp("end_at").nullable()
}