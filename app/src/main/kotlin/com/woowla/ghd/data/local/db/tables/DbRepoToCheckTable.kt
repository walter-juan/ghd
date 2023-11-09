package com.woowla.ghd.data.local.db.tables

import org.jetbrains.exposed.dao.id.LongIdTable

object DbRepoToCheckTable : LongIdTable(name = "repo_to_check") {
    val owner = text("owner")
    val name = text("name")
    val groupName = text("group_name").nullable()
    val pullBranchRegex = text("pull_branch_regex").nullable()
}