package com.woowla.ghd.domain.synchronization

import com.woowla.ghd.domain.entities.RepoToCheck
import com.woowla.ghd.domain.entities.SyncSettings
import com.woowla.ghd.domain.requests.UpsertSyncResultEntryRequest

interface SynchronizableService {
    suspend fun synchronize(syncResultId: Long, syncSettings: SyncSettings, repoToCheckList: List<RepoToCheck>): List<UpsertSyncResultEntryRequest>
}