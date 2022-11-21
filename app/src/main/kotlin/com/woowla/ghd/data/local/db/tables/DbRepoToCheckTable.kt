package com.woowla.ghd.data.local.db.tables

import org.jetbrains.exposed.dao.id.LongIdTable

object DbRepoToCheckTable : LongIdTable() {
    val owner = text("owner")
    val name = text("name")
    val pullNotificationsEnabled = bool("pullNotificationsEnabled")
    val releaseNotificationsEnabled = bool("releaseNotificationsEnabled")
    val groupName = text("groupName").nullable()
    val pullBranchRegex = text("pullBranchRegex").nullable()
}