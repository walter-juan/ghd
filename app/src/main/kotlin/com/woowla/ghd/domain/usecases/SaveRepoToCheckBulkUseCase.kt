package com.woowla.ghd.domain.usecases

import com.woowla.ghd.data.local.LocalDataSource
import com.woowla.ghd.domain.requests.UpsertRepoToCheckRequest
import com.woowla.ghd.eventbus.Event
import com.woowla.ghd.eventbus.EventBus
import com.woowla.ghd.utils.UseCase
import java.io.File

open class SaveRepoToCheckBulkUseCase(
    private val localDataSource: LocalDataSource = LocalDataSource(),
) : UseCase<File, Unit>() {
    override suspend fun perform(params: File): Result<Unit> {
        val lines = getFileLines(params)
       
        lines
            .filter { line ->
                validate(line)
            }
            .map { line ->
                parse(line)
            }
            .forEach { repo ->
                storeToDataSource(repo)
            }

        EventBus.publish(Event.REPO_TO_CHECK_UPDATED)

        return Result.success(Unit)
    }

    private fun parse(line: String): Repository {
        val owner = line.split("/").getOrNull(0)
        val name = line.split("/").getOrNull(1)?.split(" ")?.getOrNull(0)
        val notificationsEnabled = line.split(" ").getOrElse(1) { "false" }
        require(!owner.isNullOrBlank()) { "owner is null or blank" }
        require(!name.isNullOrBlank()) { "name is null or blank" }
        return Repository(owner = owner, name = name, notificationsEnabled = notificationsEnabled.toBoolean())
    }

    private fun validate(line: String): Boolean {
        return line.matches("""^(\w|-)+(/)(\w|-)+( (true|false))*${'$'}""".toRegex())
    }

    open fun getFileLines(params: File) = params.bufferedReader().readLines()

    open suspend fun storeToDataSource(repo: Repository) {
        localDataSource.upsertRepoToCheck(
            upsertRequest = UpsertRepoToCheckRequest.newInstance().copy(
                owner = repo.owner,
                name = repo.name,
                pullNotificationsEnabled = repo.notificationsEnabled
            )
        )
    }

    data class Repository(val owner: String, val name: String, val notificationsEnabled: Boolean = false)
}