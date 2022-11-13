package com.woowla.ghd.domain.usecases

import com.woowla.ghd.data.local.LocalDataSource
import com.woowla.ghd.domain.entities.RepoToCheck
import com.woowla.ghd.domain.mappers.DbMappers
import com.woowla.ghd.utils.UseCaseWithoutParams

class GetAllReposToCheckUseCaseUseCase(
    private val localDataSource: LocalDataSource = LocalDataSource(),
): UseCaseWithoutParams<List<RepoToCheck>>() {
    override suspend fun perform(): Result<List<RepoToCheck>> {
        return localDataSource.getAllReposToCheck().map { DbMappers.INSTANCE.dbRepoToCheckToRepoToCheck(it) }
    }
}