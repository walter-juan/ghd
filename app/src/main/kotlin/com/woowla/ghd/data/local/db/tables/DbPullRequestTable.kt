package com.woowla.ghd.data.local.db.tables

import com.woowla.ghd.data.local.db.utils.TextIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object DbPullRequestTable : TextIdTable() {
    val number = long("number").nullable()
    val url = text("url").nullable()
    val state = text("state").nullable()
    val title = text("title").nullable()
    val createdAt = timestamp("createdAt").nullable()
    val updatedAt = timestamp("updatedAt").nullable()
    val mergedAt = timestamp("mergedAt").nullable()
    val draft = bool("draft").nullable()
    val baseRef = text("baseRef").nullable()
    val headRef = text("headRef").nullable()
    val authorLogin = text("authorLogin").nullable()
    val authorUrl = text("authorUrl").nullable()
    val authorAvatarUrl = text("authorAvatarUrl").nullable()
    val appSeenAt = timestamp("appSeenAt").nullable()
    val totalCommentsCount = long("totalCommentsCount").nullable()
    val repoToCheck = reference("repoToCheckId", DbRepoToCheckTable, onDelete = ReferenceOption.CASCADE)
}