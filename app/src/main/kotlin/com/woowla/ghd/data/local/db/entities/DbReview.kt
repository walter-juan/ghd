package com.woowla.ghd.data.local.db.entities

import com.woowla.ghd.data.local.db.tables.DbReviewTable
import com.woowla.ghd.data.local.db.utils.TextEntity
import com.woowla.ghd.data.local.db.utils.TextEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class DbReview(id: EntityID<String>) : TextEntity(id) {
    companion object : TextEntityClass<DbReview>(DbReviewTable)
    var submittedAt by DbReviewTable.submittedAt
    var url by DbReviewTable.url
    var state by DbReviewTable.state
    var authorLogin by DbReviewTable.authorLogin
    var authorUrl by DbReviewTable.authorUrl
    var authorAvatarUrl by DbReviewTable.authorAvatarUrl
    var pullRequest by DbPullRequest referencedOn DbReviewTable.pullRequestId
}