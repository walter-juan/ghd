package com.woowla.ghd.data.local.db.tables

import com.woowla.ghd.data.local.db.utils.TextIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object DbReleaseTable : TextIdTable(name = "release") {
    val name = text("name").nullable()
    val tagName = text("tag_name").nullable()
    val url = text("url").nullable()
    val publishedAt = timestamp("published_at").nullable()
    val authorLogin = text("author_login").nullable()
    val authorUrl = text("author_url").nullable()
    val authorAvatarUrl = text("author_avatar_url").nullable()
    val repoToCheckId = reference("repo_to_check_id", DbRepoToCheckTable, onDelete = ReferenceOption.CASCADE)
}