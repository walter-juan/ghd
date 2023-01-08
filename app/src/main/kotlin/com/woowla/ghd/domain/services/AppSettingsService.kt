package com.woowla.ghd.domain.services

import com.woowla.ghd.data.local.LocalDataSource
import com.woowla.ghd.domain.entities.AppSettings
import com.woowla.ghd.domain.mappers.DomainMappers
import com.woowla.ghd.domain.mappers.PropMappers
import com.woowla.ghd.eventbus.Event
import com.woowla.ghd.eventbus.EventBus

class AppSettingsService(
    private val localDataSource: LocalDataSource = LocalDataSource(),
) {
    suspend fun get(): Result<AppSettings> {
        return localDataSource.getAppSettings()
            .mapCatching {
                PropMappers.INSTANCE.propAppSettingsToAppSettings(it)
            }
    }

    suspend fun save(params: AppSettings): Result<Unit> {
        return localDataSource.updateAppSettings(DomainMappers.INSTANCE.appSettingsToUpsertRequest(params))
            .onSuccess {
                EventBus.publish(Event.SETTINGS_UPDATED)
            }
    }
}