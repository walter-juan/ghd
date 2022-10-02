package com.woowla.ghd.domain.usecases

import com.woowla.ghd.data.local.LocalDataSource
import com.woowla.ghd.domain.mappers.InstantMapper
import com.woowla.ghd.utils.UseCase
import kotlinx.datetime.Instant

class SetPullRequestSeenAt(
    private val localDataSource: LocalDataSource = LocalDataSource(),
) : UseCase<SetPullRequestSeenAt.Params, Unit>() {
    data class Params(val id: String, val appSeenAt: Instant?)

    override suspend fun perform(params: Params): Result<Unit> {
        val instantMapper = InstantMapper()
        val appSeenAt = instantMapper.instantToString(params.appSeenAt)
        return localDataSource.updateAppSeenAt(id = params.id, appSeenAt = appSeenAt)
    }
}