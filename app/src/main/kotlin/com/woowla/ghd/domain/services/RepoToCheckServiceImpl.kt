package com.woowla.ghd.domain.services

import com.woowla.ghd.domain.data.LocalDataSource
import com.woowla.ghd.domain.entities.RepoToCheck
import com.woowla.ghd.domain.parsers.RepoToCheckFileParser
import com.woowla.ghd.domain.entities.Event
import com.woowla.ghd.core.eventbus.EventBus

class RepoToCheckServiceImpl(
    private val localDataSource: LocalDataSource,
    private val fileParser: RepoToCheckFileParser,
    private val eventBus: EventBus
) : RepoToCheckService {
    override suspend fun get(id: Long): Result<RepoToCheck> {
        return localDataSource.getRepoToCheck(id = id)
    }

    override suspend fun getAll(): Result<List<RepoToCheck>> {
        return localDataSource.getAllReposToCheck()
            .mapCatching { reposToCheck ->
                reposToCheck.sorted()
            }
    }

    override suspend fun delete(id: Long): Result<Unit> {
        return localDataSource.removeRepoToCheck(id)
    }

    override suspend fun save(repoToCheck: RepoToCheck): Result<Unit> {
        return localDataSource.upsertRepoToCheck(repoToCheck)
            .onSuccess {
                eventBus.publish(Event.REPO_TO_CHECK_UPDATED)
            }
    }

    override suspend fun export(): Result<String> {
        return getAll().map(fileParser::encode)
    }

    override suspend fun import(content: String): Result<Unit> {
        val allReposToCheck = getAll().getOrNull() ?: listOf()

        fileParser.decode(content)
            .filter { repoToCheck ->
                allReposToCheck.none { it.owner == repoToCheck.owner && it.name == repoToCheck.name }
            }
            .forEach { repoToCheck ->
                save(repoToCheck)
            }

        eventBus.publish(Event.REPO_TO_CHECK_UPDATED)

        return Result.success(Unit)
    }
}