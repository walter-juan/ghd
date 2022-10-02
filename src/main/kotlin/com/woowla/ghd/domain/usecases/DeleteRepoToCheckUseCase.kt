package com.woowla.ghd.domain.usecases

import com.woowla.ghd.data.local.LocalDataSource
import com.woowla.ghd.utils.UseCase

class DeleteRepoToCheckUseCase(
    private val localDataSource: LocalDataSource = LocalDataSource(),
): UseCase<Long, Unit>() {
    override suspend fun perform(params: Long): Result<Unit> {
        return localDataSource.removeRepoToCheck(params)
    }
}
