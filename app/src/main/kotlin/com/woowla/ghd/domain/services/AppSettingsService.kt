package com.woowla.ghd.domain.services

import com.woowla.ghd.data.local.LocalDataSource
import com.woowla.ghd.domain.entities.AppSettings
import com.woowla.ghd.eventbus.Event
import com.woowla.ghd.eventbus.EventBus

class AppSettingsService(
    private val localDataSource: LocalDataSource,
    private val eventBus: EventBus,
) {
    suspend fun get(): Result<AppSettings> {
        return localDataSource.getAppSettings()
    }

    suspend fun save(params: AppSettings): Result<Unit> {
        return localDataSource.updateAppSettings(params)
            .onSuccess {
                eventBus.publish(Event.SETTINGS_UPDATED)
            }
    }
}