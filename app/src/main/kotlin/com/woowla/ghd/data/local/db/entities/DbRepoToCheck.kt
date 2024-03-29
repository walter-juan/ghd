package com.woowla.ghd.data.local.db.entities

import com.woowla.ghd.data.local.db.tables.DbPullRequestTable
import com.woowla.ghd.data.local.db.tables.DbReleaseTable
import com.woowla.ghd.data.local.db.tables.DbRepoToCheckTable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class DbRepoToCheck(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<DbRepoToCheck>(DbRepoToCheckTable)
    var owner by DbRepoToCheckTable.owner
    var name by DbRepoToCheckTable.name
    var groupName by DbRepoToCheckTable.groupName
    var pullBranchRegex by DbRepoToCheckTable.pullBranchRegex
    var arePullRequestsEnabled by DbRepoToCheckTable.arePullRequestsEnabled
    var areReleasesEnabled by DbRepoToCheckTable.areReleasesEnabled

    val releases by DbRelease referrersOn DbReleaseTable.repoToCheckId
    val pullRequests by DbPullRequest referrersOn DbPullRequestTable.repoToCheckId
}