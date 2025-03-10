package com.woowla.ghd.domain.services

import com.woowla.ghd.domain.data.LocalDataSource
import com.woowla.ghd.domain.entities.AppSettings
import com.woowla.ghd.domain.entities.Event
import com.woowla.ghd.core.eventbus.EventBus

class AppSettingsServiceImpl(
    private val localDataSource: LocalDataSource,
    private val eventBus: EventBus,
) : AppSettingsService {
    override suspend fun get(): Result<AppSettings> {
        return localDataSource.getAppSettings()
    }

    override suspend fun save(params: AppSettings): Result<Unit> {
        return localDataSource.updateAppSettings(params)
            .onSuccess {
                eventBus.publish(Event.SETTINGS_UPDATED)
            }
    }
}