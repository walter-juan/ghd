package com.woowla.ghd.domain.usecases

import com.woowla.ghd.data.local.LocalDataSource
import com.woowla.ghd.domain.entities.SyncSettings
import com.woowla.ghd.domain.mappers.DomainMappers
import com.woowla.ghd.eventbus.Event
import com.woowla.ghd.eventbus.EventBus
import com.woowla.ghd.utils.UseCase

class SaveSyncSettingsUseCase(
    private val localDataSource: LocalDataSource = LocalDataSource(),
) : UseCase<SyncSettings, Unit>() {
    override suspend fun perform(params: SyncSettings): Result<Unit> {
        return localDataSource.updateSyncSettings(DomainMappers.INSTANCE.syncSettingsToUpsertRequest(params))
            .onSuccess {
                EventBus.publish(Event.SETTINGS_UPDATED)
            }
    }
}