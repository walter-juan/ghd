package com.woowla.ghd.domain.services

import com.woowla.ghd.data.local.LocalDataSource
import com.woowla.ghd.domain.entities.SyncSettings
import com.woowla.ghd.eventbus.Event
import com.woowla.ghd.eventbus.EventBus

class SyncSettingsService(
    private val localDataSource: LocalDataSource,
) {
    suspend fun get(): Result<SyncSettings> {
        return localDataSource.getSyncSettings()
    }

    suspend fun save(params: SyncSettings): Result<Unit> {
        return localDataSource.updateSyncSettings(params)
            .onSuccess {
                EventBus.publish(Event.SETTINGS_UPDATED)
            }
    }
}