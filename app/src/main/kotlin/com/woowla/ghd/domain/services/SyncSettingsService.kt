package com.woowla.ghd.domain.services

import com.woowla.ghd.data.local.LocalDataSource
import com.woowla.ghd.domain.entities.SyncSettings
import com.woowla.ghd.domain.mappers.DbMappers
import com.woowla.ghd.domain.mappers.DomainMappers
import com.woowla.ghd.eventbus.Event
import com.woowla.ghd.eventbus.EventBus

class SyncSettingsService(
    private val localDataSource: LocalDataSource = LocalDataSource(),
) {
    suspend fun get(): Result<SyncSettings> {
        return localDataSource.getSyncSettings()
            .mapCatching {
                DbMappers.INSTANCE.dbSyncSettingsToSyncSettings(it)
            }
    }

    suspend fun save(params: SyncSettings): Result<Unit> {
        return localDataSource.updateSyncSettings(DomainMappers.INSTANCE.syncSettingsToUpsertRequest(params))
            .onSuccess {
                EventBus.publish(Event.SETTINGS_UPDATED)
            }
    }
}