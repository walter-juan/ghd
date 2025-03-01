package com.woowla.ghd.domain.synchronization

import com.woowla.ghd.domain.entities.SyncResultEntryWithRepo
import com.woowla.ghd.domain.entities.SyncResultWithEntriesAndRepos

interface Synchronizer {
    fun initialize()

    suspend fun getAllSyncResults(): Result<List<SyncResultWithEntriesAndRepos>>

    suspend fun getLastSyncResult(): Result<SyncResultWithEntriesAndRepos?>

    suspend fun getSyncResult(id: Long): Result<SyncResultWithEntriesAndRepos>

    suspend fun getSyncResultEntries(syncResultId: Long): Result<List<SyncResultEntryWithRepo>>

    suspend fun cleanUpSyncResult()
    fun sync()
}