package com.woowla.ghd.data.local.db.tables

import com.woowla.ghd.data.local.db.tables.DbReleaseTable.nullable
import com.woowla.ghd.data.local.db.utils.TextIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object DbReviewTable : TextIdTable(name = "review") {
    val submittedAt = timestamp("submitted_at").nullable()
    val url = text("url")
    val state = text("state").nullable()
    val authorLogin = text("author_login").nullable()
    val authorUrl = text("author_url").nullable()
    val authorAvatarUrl = text("author_avatar_url").nullable()
    val pullRequestId = reference("pull_request_id", DbPullRequestTable, onDelete = ReferenceOption.CASCADE)
}