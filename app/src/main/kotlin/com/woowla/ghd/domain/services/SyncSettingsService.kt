package com.woowla.ghd.domain.services

import com.woowla.ghd.domain.entities.SyncSettings

interface SyncSettingsService {
    suspend fun get(): Result<SyncSettings>

    suspend fun save(params: SyncSettings): Result<Unit>
}