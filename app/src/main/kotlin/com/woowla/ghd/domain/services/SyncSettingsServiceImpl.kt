package com.woowla.ghd.domain.services

import com.woowla.ghd.domain.data.LocalDataSource
import com.woowla.ghd.domain.entities.SyncSettings
import com.woowla.ghd.domain.entities.Event
import com.woowla.ghd.core.eventbus.EventBus

class SyncSettingsServiceImpl(
    private val localDataSource: LocalDataSource,
    private val eventBus: EventBus,
) : SyncSettingsService {
    override suspend fun get(): Result<SyncSettings> {
        return localDataSource.getSyncSettings()
    }

    override suspend fun save(params: SyncSettings): Result<Unit> {
        return localDataSource.updateSyncSettings(params)
            .onSuccess {
                eventBus.publish(Event.SETTINGS_UPDATED)
            }
    }
}