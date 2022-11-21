package com.woowla.ghd.domain.usecases

import com.woowla.ghd.data.local.LocalDataSource
import com.woowla.ghd.domain.requests.UpsertRepoToCheckRequest
import com.woowla.ghd.eventbus.Event
import com.woowla.ghd.eventbus.EventBus
import com.woowla.ghd.utils.UseCase

class SaveRepoToCheckUseCase(
    private val localDataSource: LocalDataSource = LocalDataSource(),
): UseCase<UpsertRepoToCheckRequest, Unit>() {
    override suspend fun perform(params: UpsertRepoToCheckRequest): Result<Unit> {
        return localDataSource.upsertRepoToCheck(params)
            .onSuccess {
                EventBus.publish(Event.REPO_TO_CHECK_UPDATED)
            }
    }
}