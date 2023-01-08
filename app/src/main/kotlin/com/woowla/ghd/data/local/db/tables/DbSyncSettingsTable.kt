package com.woowla.ghd.data.local.db.tables

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object DbSyncSettingsTable : UUIDTable(name = "sync_setting") {
    val githubPatToken = text("github_pat_token")
    val checkTimeout = long("check_timeout").nullable()
    val synchronizedAt = timestamp("synchronized_at").nullable()
    val pullRequestCleanUpTimeout = long("pull_request_clean_up_timeout").nullable()
}