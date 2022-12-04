package com.woowla.ghd.domain.usecases

import com.woowla.ghd.domain.entities.RepoToCheck
import com.woowla.ghd.domain.parsers.RepoToCheckFileParser
import com.woowla.ghd.domain.parsers.YamlRepoToCheckFileParser
import com.woowla.ghd.domain.requests.UpsertRepoToCheckRequest
import com.woowla.ghd.eventbus.Event
import com.woowla.ghd.eventbus.EventBus
import com.woowla.ghd.utils.UseCase

open class ImportRepoToCheckUseCase(
    private val getAllReposToCheckUseCase: GetAllReposToCheckUseCase = GetAllReposToCheckUseCase(),
    private val saveRepoToCheckUseCase: SaveRepoToCheckUseCase = SaveRepoToCheckUseCase(),
    private val fileParser: RepoToCheckFileParser = YamlRepoToCheckFileParser(),
) : UseCase<String, Unit>() {
    override suspend fun perform(content: String): Result<Unit> {
        val allReposToCheck = getAllReposToCheckUseCase.execute().getOrNull() ?: listOf()

        fileParser.decode(content)
            .filter { repoToCheck ->
                allReposToCheck.none { it.owner == repoToCheck.owner && it.name == repoToCheck.name }
            }
            .forEach { repo ->
                storeToDataSource(repo)
            }

        EventBus.publish(Event.REPO_TO_CHECK_UPDATED)

        return Result.success(Unit)
    }

    open suspend fun storeToDataSource(repo: RepoToCheck) {
        saveRepoToCheckUseCase.execute(UpsertRepoToCheckRequest.newInstance().copy(
            owner = repo.owner,
            name = repo.name,
            pullNotificationsEnabled = repo.pullNotificationsEnabled,
            releaseNotificationsEnabled = repo.releaseNotificationsEnabled,
            groupName = repo.groupName,
            pullBranchRegex = repo.pullBranchRegex,
        ))
    }
}

