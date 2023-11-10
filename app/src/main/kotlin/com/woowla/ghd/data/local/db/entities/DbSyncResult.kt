package com.woowla.ghd.data.local.db.entities

import com.woowla.ghd.data.local.db.tables.DbSyncResultEntryTable
import com.woowla.ghd.data.local.db.tables.DbSyncResultTable
import com.woowla.ghd.data.local.db.tables.DbSyncSettingsTable.nullable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class DbSyncResult(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<DbSyncResult>(DbSyncResultTable)
    var startAt by DbSyncResultTable.startAt
    var endAt by DbSyncResultTable.endAt
    val entries by DbSyncResultEntry referrersOn DbSyncResultEntryTable.syncResultId
}