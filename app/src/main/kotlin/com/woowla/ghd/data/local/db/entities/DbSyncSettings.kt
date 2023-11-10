package com.woowla.ghd.data.local.db.entities

import com.woowla.ghd.data.local.db.tables.DbSyncSettingsTable
import java.util.UUID
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class DbSyncSettings(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<DbSyncSettings>(DbSyncSettingsTable)
    var githubPatToken by DbSyncSettingsTable.githubPatToken
    var checkTimeout by DbSyncSettingsTable.checkTimeout
    var pullRequestCleanUpTimeout by DbSyncSettingsTable.pullRequestCleanUpTimeout
}