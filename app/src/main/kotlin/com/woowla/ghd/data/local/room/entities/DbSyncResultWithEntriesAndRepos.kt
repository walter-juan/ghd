package com.woowla.ghd.data.local.room.entities

import com.woowla.ghd.domain.entities.SyncResultWithEntriesAndRepos
import io.mcarle.konvert.api.KonvertFrom
import io.mcarle.konvert.api.KonvertTo

// TODO relations
@KonvertFrom(SyncResultWithEntriesAndRepos::class)
@KonvertTo(SyncResultWithEntriesAndRepos::class)
data class DbSyncResultWithEntriesAndRepos(
    val syncResult: DbSyncResult,
    val syncResultEntries: List<DbSyncResultEntryWithRepo>,
) {
    companion object
}
