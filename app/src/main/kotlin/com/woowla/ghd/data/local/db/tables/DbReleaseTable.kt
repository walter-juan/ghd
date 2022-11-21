package com.woowla.ghd.data.local.db.tables

import com.woowla.ghd.data.local.db.utils.TextIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object DbReleaseTable : TextIdTable() {
    val name = text("name").nullable()
    val tagName = text("tagName").nullable()
    val url = text("url").nullable()
    val publishedAt = timestamp("publishedAt").nullable()
    val authorLogin = text("authorLogin").nullable()
    val authorUrl = text("authorUrl").nullable()
    val authorAvatarUrl = text("authorAvatarUrl").nullable()
    val repoToCheckId = reference("repoToCheckId", DbRepoToCheckTable, onDelete = ReferenceOption.CASCADE)
}