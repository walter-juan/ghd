package com.woowla.ghd.domain.services

import com.woowla.ghd.domain.entities.AppSettings

interface AppSettingsService {
    suspend fun get(): Result<AppSettings>

    suspend fun save(params: AppSettings): Result<Unit>
}