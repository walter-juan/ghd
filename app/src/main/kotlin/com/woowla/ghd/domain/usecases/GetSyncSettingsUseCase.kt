package com.woowla.ghd.domain.usecases

import com.woowla.ghd.data.local.LocalDataSource
import com.woowla.ghd.domain.entities.SyncSettings
import com.woowla.ghd.domain.mappers.DbMappers
import com.woowla.ghd.utils.UseCaseWithoutParams

class GetSyncSettingsUseCase(
    private val localDataSource: LocalDataSource = LocalDataSource(),
): UseCaseWithoutParams<SyncSettings>() {
    override suspend fun perform(): Result<SyncSettings> {
        return localDataSource.getSyncSettings()
            .map {
                DbMappers.INSTANCE.dbSyncSettingsToSyncSettings(it)
            }
    }
}
