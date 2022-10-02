package com.woowla.ghd.domain.usecases

import com.woowla.ghd.data.local.LocalDataSource
import com.woowla.ghd.domain.requests.UpsertRepoToCheckRequest
import com.woowla.ghd.eventbus.Event
import com.woowla.ghd.eventbus.EventBus
import com.woowla.ghd.utils.UseCase
import java.io.File

class SaveRepoToCheckBulkUseCase(
    private val localDataSource: LocalDataSource = LocalDataSource(),
) : UseCase<File, Unit>() {
    override suspend fun perform(params: File): Result<Unit> {
        val lines = params.bufferedReader().readLines()
        lines
            .filter { line ->
                line.matches("""^(\w|-)+(/)(\w|-)+${'$'}""".toRegex())
            }
            .forEach { line ->
                val owner = line.split("/").getOrNull(0)
                val name = line.split("/").getOrNull(1)
                require(!owner.isNullOrBlank()) { "owner is null or blank" }
                require(!name.isNullOrBlank()) { "name is null or blank" }
                localDataSource.upsertRepoToCheck(
                    upsertRequest = UpsertRepoToCheckRequest.newInstance().copy(
                        owner = owner,
                        name = name,
                    )
                )
            }

        EventBus.publish(Event.REPO_TO_CHECK_UPDATED)

        return Result.success(Unit)
    }
}