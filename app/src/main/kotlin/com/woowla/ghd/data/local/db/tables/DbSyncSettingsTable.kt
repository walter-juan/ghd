package com.woowla.ghd.data.local.db.tables

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object DbSyncSettingsTable : UUIDTable() {
    val githubPatToken = text("githubPatToken")
    val checkTimeout = long("checkTimeout").nullable()
    val synchronizedAt = timestamp("synchronizedAt").nullable()
    val pullRequestCleanUpTimeout = long("pullRequestCleanUpTimeout").nullable()
}