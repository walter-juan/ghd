package com.woowla.ghd.domain.services

import com.woowla.ghd.data.local.LocalDataSource
import com.woowla.ghd.domain.entities.SyncSettings
import com.woowla.ghd.domain.mappers.toSyncSettings
import com.woowla.ghd.domain.mappers.toUpsertSyncSettingsRequest
import com.woowla.ghd.eventbus.Event
import com.woowla.ghd.eventbus.EventBus

class SyncSettingsService(
    private val localDataSource: LocalDataSource = LocalDataSource(),
) {
    suspend fun get(): Result<SyncSettings> {
        return localDataSource.getSyncSettings()
            .mapCatching {
                it.toSyncSettings()
            }
    }

    suspend fun save(params: SyncSettings): Result<Unit> {
        return localDataSource.updateSyncSettings(params.toUpsertSyncSettingsRequest())
            .onSuccess {
                EventBus.publish(Event.SETTINGS_UPDATED)
            }
    }
}