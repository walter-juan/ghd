package com.woowla.ghd.data.local.db.tables

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object DbSyncResultEntryTable : LongIdTable(name = "sync_result_entry") {
    val isSuccess = bool("is_success")
    val startAt = timestamp("start_at")
    val endAt = timestamp("end_at")
    val origin = text("origin")
    val error = text("error").nullable()
    val errorMessage = text("error_message").nullable()
    val syncResultId = reference("sync_result_id", DbSyncResultTable, onDelete = ReferenceOption.CASCADE)
    val repoToCheckId = reference("repo_to_check_id", DbRepoToCheckTable, onDelete = ReferenceOption.CASCADE).nullable()
}