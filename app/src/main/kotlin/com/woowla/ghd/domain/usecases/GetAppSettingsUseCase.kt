package com.woowla.ghd.domain.usecases

import com.woowla.ghd.data.local.LocalDataSource
import com.woowla.ghd.domain.entities.AppSettings
import com.woowla.ghd.domain.mappers.PropMappers
import com.woowla.ghd.utils.UseCaseWithoutParams

class GetAppSettingsUseCase(
    private val localDataSource: LocalDataSource = LocalDataSource(),
): UseCaseWithoutParams<AppSettings>() {
    override suspend fun perform(): Result<AppSettings> {
        return localDataSource.getAppSettings()
            .map {
                PropMappers.INSTANCE.propAppSettingsToAppSettings(it)
            }
    }
}
