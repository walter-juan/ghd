package com.woowla.ghd.domain.usecases

import com.woowla.ghd.data.local.LocalDataSource
import com.woowla.ghd.domain.entities.AppSettings
import com.woowla.ghd.domain.mappers.DomainMappers
import com.woowla.ghd.eventbus.Event
import com.woowla.ghd.eventbus.EventBus
import com.woowla.ghd.utils.UseCase

class SaveAppSettingsUseCase(
    private val localDataSource: LocalDataSource = LocalDataSource(),
) : UseCase<AppSettings, Unit>() {
    override suspend fun perform(params: AppSettings): Result<Unit> {
        return localDataSource.updateAppSettings(DomainMappers.INSTANCE.appSettingsToDbAppSettings(params))
            .onSuccess {
                EventBus.publish(Event.APP_SETTINGS_UPDATED)
            }
    }
}