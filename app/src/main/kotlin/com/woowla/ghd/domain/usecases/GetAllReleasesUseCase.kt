package com.woowla.ghd.domain.usecases

import com.woowla.ghd.data.local.LocalDataSource
import com.woowla.ghd.domain.entities.Release
import com.woowla.ghd.domain.mappers.DbMappers
import com.woowla.ghd.utils.UseCaseWithoutParams

class GetAllReleasesUseCase(
    private val localDataSource: LocalDataSource = LocalDataSource()
)  : UseCaseWithoutParams<List<Release>>() {
    override suspend fun perform(): Result<List<Release>> {
        return localDataSource.getAllReleases()
            .map { dbReleases ->
                DbMappers.INSTANCE.dbReleaseToRelease(dbReleases)
            }.map { releases ->
                releases.sorted()
            }
    }
}