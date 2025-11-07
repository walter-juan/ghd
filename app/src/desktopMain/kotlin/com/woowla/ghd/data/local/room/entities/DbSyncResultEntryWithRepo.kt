package com.woowla.ghd.data.local.room.entities

import com.woowla.ghd.domain.entities.SyncResultEntryWithRepo
import io.mcarle.konvert.api.KonvertFrom
import io.mcarle.konvert.api.KonvertTo

// TODO relations
@KonvertFrom(SyncResultEntryWithRepo::class)
@KonvertTo(SyncResultEntryWithRepo::class)
data class DbSyncResultEntryWithRepo(
    val syncResultEntry: DbSyncResultEntry,
    val repoToCheck: DbRepoToCheck?,
) {
    companion object
}