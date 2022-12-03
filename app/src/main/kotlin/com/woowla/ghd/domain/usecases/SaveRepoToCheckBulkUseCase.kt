package com.woowla.ghd.domain.usecases

import com.woowla.ghd.data.local.LocalDataSource
import com.woowla.ghd.domain.requests.UpsertRepoToCheckRequest
import com.woowla.ghd.eventbus.Event
import com.woowla.ghd.eventbus.EventBus
import com.woowla.ghd.utils.UseCase
import java.io.File

open class SaveRepoToCheckBulkUseCase(
    private val localDataSource: LocalDataSource = LocalDataSource(),
    val fileParser: FileParser = PlainTextFileParser(),
) : UseCase<File, Unit>() {
    override suspend fun perform(params: File): Result<Unit> {

        fileParser.parse(params).forEach { repo ->
            storeToDataSource(repo)
        }

        EventBus.publish(Event.REPO_TO_CHECK_UPDATED)

        return Result.success(Unit)
    }

    open suspend fun storeToDataSource(repo: Repository) {
        localDataSource.upsertRepoToCheck(
            upsertRequest = UpsertRepoToCheckRequest.newInstance().copy(
                owner = repo.owner,
                name = repo.name,
                pullNotificationsEnabled = repo.pullNotificationsEnabled
            )
        )
    }

    data class Repository(val owner: String, val name: String, val pullNotificationsEnabled: Boolean = false)
}

interface FileParser {
    fun parse(params: File): List<SaveRepoToCheckBulkUseCase.Repository>
}

open class PlainTextFileParser : FileParser {
    override fun parse(params: File): List<SaveRepoToCheckBulkUseCase.Repository> {
        val lines = getFileLines(params)

        val repositoryList = lines
            .filter { line ->
                validate(line)
            }
            .map { line ->
                parse(line)
            }
        return repositoryList
    }

    open fun getFileLines(params: File) = params.bufferedReader().readLines()

    private fun parse(line: String): SaveRepoToCheckBulkUseCase.Repository {
        val owner = line.split("/").getOrNull(0)
        val name = line.split("/").getOrNull(1)?.split(" ")?.getOrNull(0)
        val notificationsEnabled = line.split(" ").getOrElse(1) { "false" }
        require(!owner.isNullOrBlank()) { "owner is null or blank" }
        require(!name.isNullOrBlank()) { "name is null or blank" }
        return SaveRepoToCheckBulkUseCase.Repository(
            owner = owner,
            name = name,
            pullNotificationsEnabled = notificationsEnabled.toBoolean()
        )
    }

    private fun validate(line: String): Boolean {
        return line.matches("""^(\w|-)+(/)(\w|-)+( (true|false))*${'$'}""".toRegex())
    }
}