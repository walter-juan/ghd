package com.woowla.ghd.domain.synchronization

import com.woowla.ghd.domain.entities.RepoToCheck
import com.woowla.ghd.domain.entities.SyncSettings

interface SynchronizableService {
    suspend fun synchronize(syncSettings: SyncSettings, repoToCheckList: List<RepoToCheck>)
}