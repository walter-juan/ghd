package com.woowla.ghd.domain.services

import com.woowla.ghd.data.local.LocalDataSource
import com.woowla.ghd.domain.entities.AppSettings
import com.woowla.ghd.domain.mappers.toAppSettings
import com.woowla.ghd.domain.mappers.toUpsertAppSettingsRequest
import com.woowla.ghd.eventbus.Event
import com.woowla.ghd.eventbus.EventBus

class AppSettingsService(
    private val localDataSource: LocalDataSource = LocalDataSource(),
) {
    suspend fun get(): Result<AppSettings> {
        return localDataSource.getAppSettings()
            .mapCatching {
                it.toAppSettings()
            }
    }

    suspend fun save(params: AppSettings): Result<Unit> {
        return localDataSource.updateAppSettings(params.toUpsertAppSettingsRequest())
            .onSuccess {
                EventBus.publish(Event.SETTINGS_UPDATED)
            }
    }
}