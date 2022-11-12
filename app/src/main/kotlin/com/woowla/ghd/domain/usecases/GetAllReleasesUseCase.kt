package com.woowla.ghd.domain.usecases

import com.woowla.ghd.data.local.LocalDataSource
import com.woowla.ghd.domain.entities.Release
import com.woowla.ghd.domain.mappers.DbMappers
import com.woowla.ghd.utils.UseCaseWithoutParams

class GetAllReleasesUseCase(
    private val localDataSource: LocalDataSource = LocalDataSource()
)  : UseCaseWithoutParams<List<Release>>() {
    override suspend fun perform(): Result<List<Release>> {
        val dbReleases = localDataSource.getAllReleases().getOrThrow()

        val releases = dbReleases.map { dbRelease ->
            val dbRepoToCheck = localDataSource.getRepoToCheck(dbRelease.repoToCheckId).getOrThrow()
            val repoToCheck = DbMappers.INSTANCE.dbRepoToCheckToRepoToCheck(dbRepoToCheck)
            DbMappers.INSTANCE.dbReleaseToRelease(dbRelease, repoToCheck)
        }.sorted()

        return Result.success(releases)
    }
}