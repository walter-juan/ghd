package com.woowla.ghd.domain.services

import com.woowla.ghd.domain.entities.AppSettings
import com.woowla.ghd.domain.entities.ReleaseWithRepo
import com.woowla.ghd.domain.entities.RepoToCheck
import com.woowla.ghd.domain.entities.SyncResultEntry
import com.woowla.ghd.domain.synchronization.SynchronizableService

interface ReleaseService : SynchronizableService {
    suspend fun getAll(): Result<List<ReleaseWithRepo>>

    suspend fun cleanUp()

    suspend fun sendNotifications(appSettings: AppSettings, oldReleases: List<ReleaseWithRepo>, newReleases: List<ReleaseWithRepo>): Result<Unit>

    suspend fun fetchLastReleases(syncResultId: Long, repoToCheck: RepoToCheck): SyncResultEntry
}