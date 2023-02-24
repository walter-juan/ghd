package com.woowla.ghd.data.local.db.entities

import com.woowla.ghd.data.local.db.tables.DbReviewTable
import com.woowla.ghd.data.local.db.tables.DbPullRequestTable
import com.woowla.ghd.data.local.db.utils.TextEntity
import com.woowla.ghd.data.local.db.utils.TextEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class DbPullRequest(id: EntityID<String>) : TextEntity(id) {
    companion object : TextEntityClass<DbPullRequest>(DbPullRequestTable)
    var number by DbPullRequestTable.number
    var url by DbPullRequestTable.url
    var state by DbPullRequestTable.state
    var title by DbPullRequestTable.title
    var createdAt by DbPullRequestTable.createdAt
    var updatedAt by DbPullRequestTable.updatedAt
    var mergedAt by DbPullRequestTable.mergedAt
    var draft by DbPullRequestTable.draft
    var baseRef by DbPullRequestTable.baseRef
    var headRef by DbPullRequestTable.headRef
    var authorLogin by DbPullRequestTable.authorLogin
    var authorUrl by DbPullRequestTable.authorUrl
    var authorAvatarUrl by DbPullRequestTable.authorAvatarUrl
    var appSeenAt by DbPullRequestTable.appSeenAt
    var totalCommentsCount by DbPullRequestTable.totalCommentsCount
    var mergeable by DbPullRequestTable.mergeable
    var lastCommitCheckRollupStatus by DbPullRequestTable.lastCommitCheckRollupStatus
    var repoToCheck by DbRepoToCheck referencedOn DbPullRequestTable.repoToCheckId
    val reviews by DbReview referrersOn DbReviewTable.pullRequestId
}
