package com.woowla.ghd.domain.services

import com.woowla.ghd.domain.entities.DeploymentWithRepo
import com.woowla.ghd.domain.entities.SyncSettings
import com.woowla.ghd.domain.synchronization.SynchronizableService

interface DeploymentService : SynchronizableService {
    suspend fun getAll(): Result<List<DeploymentWithRepo>>

    suspend fun cleanUp(syncSettings: SyncSettings)
}