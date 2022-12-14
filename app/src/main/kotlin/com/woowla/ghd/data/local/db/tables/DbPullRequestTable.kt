package com.woowla.ghd.data.local.db.tables

import com.woowla.ghd.data.local.db.utils.TextIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object DbPullRequestTable : TextIdTable(name = "pull_request") {
    val number = long("number").nullable()
    val url = text("url").nullable()
    val state = text("state").nullable()
    val title = text("title").nullable()
    val createdAt = timestamp("created_at").nullable()
    val updatedAt = timestamp("updated_at").nullable()
    val mergedAt = timestamp("merged_at").nullable()
    val draft = bool("draft").nullable()
    val baseRef = text("base_ref").nullable()
    val headRef = text("head_ref").nullable()
    val authorLogin = text("author_login").nullable()
    val authorUrl = text("author_url").nullable()
    val authorAvatarUrl = text("author_avatar_url").nullable()
    val appSeenAt = timestamp("app_seen_at").nullable()
    val totalCommentsCount = long("total_comments_count").nullable()
    val repoToCheck = reference("repo_to_check_id", DbRepoToCheckTable, onDelete = ReferenceOption.CASCADE)
}