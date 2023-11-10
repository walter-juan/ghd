package com.woowla.ghd.data.local.db.entities

import com.woowla.ghd.data.local.db.tables.DbSyncResultEntryTable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class DbSyncResultEntry(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<DbSyncResultEntry>(DbSyncResultEntryTable)
    var isSuccess by DbSyncResultEntryTable.isSuccess
    var startAt by DbSyncResultEntryTable.startAt
    var endAt by DbSyncResultEntryTable.endAt
    var origin by DbSyncResultEntryTable.origin
    var error by DbSyncResultEntryTable.error
    var errorMessage by DbSyncResultEntryTable.errorMessage
    var syncResultId by DbSyncResultEntryTable.syncResultId
    var repoToCheck by DbRepoToCheck optionalReferencedOn DbSyncResultEntryTable.repoToCheckId
}