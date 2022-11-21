package com.woowla.ghd.data.local.db.entities

import com.woowla.ghd.data.local.db.tables.DbReleaseTable
import com.woowla.ghd.data.local.db.utils.TextEntity
import com.woowla.ghd.data.local.db.utils.TextEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class DbRelease(id: EntityID<String>) : TextEntity(id) {
    companion object : TextEntityClass<DbRelease>(DbReleaseTable)
    var name by DbReleaseTable.name
    var tagName by DbReleaseTable.tagName
    var url by DbReleaseTable.url
    var publishedAt by DbReleaseTable.publishedAt
    var authorLogin by DbReleaseTable.authorLogin
    var authorUrl by DbReleaseTable.authorUrl
    var authorAvatarUrl by DbReleaseTable.authorAvatarUrl
    var repoToCheck by DbRepoToCheck referencedOn DbReleaseTable.repoToCheckId
}