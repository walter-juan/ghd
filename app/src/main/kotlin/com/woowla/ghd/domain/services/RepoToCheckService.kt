package com.woowla.ghd.domain.services

import com.woowla.ghd.data.local.LocalDataSource
import com.woowla.ghd.domain.entities.RepoToCheck
import com.woowla.ghd.domain.parsers.RepoToCheckFileParser
import com.woowla.ghd.eventbus.Event
import com.woowla.ghd.eventbus.EventBus

class RepoToCheckService(
    private val localDataSource: LocalDataSource,
    private val fileParser: RepoToCheckFileParser,
) {
    suspend fun get(id: Long): Result<RepoToCheck> {
        return localDataSource.getRepoToCheck(id = id)
    }

    suspend fun getAll(): Result<List<RepoToCheck>> {
        return localDataSource.getAllReposToCheck()
            .mapCatching { reposToCheck ->
                reposToCheck.sorted()
            }
    }

    suspend fun delete(id: Long): Result<Unit> {
        return localDataSource.removeRepoToCheck(id)
    }

    suspend fun save(repoToCheck: RepoToCheck): Result<Unit> {
        return localDataSource.upsertRepoToCheck(repoToCheck)
            .onSuccess {
                EventBus.publish(Event.REPO_TO_CHECK_UPDATED)
            }
    }

    suspend fun export(): Result<String> {
        return getAll().map(fileParser::encode)
    }

    suspend fun import(content: String): Result<Unit> {
        val allReposToCheck = getAll().getOrNull() ?: listOf()

        fileParser.decode(content)
            .filter { repoToCheck ->
                allReposToCheck.none { it.owner == repoToCheck.owner && it.name == repoToCheck.name }
            }
            .forEach { repoToCheck ->
                save(repoToCheck)
            }

        EventBus.publish(Event.REPO_TO_CHECK_UPDATED)

        return Result.success(Unit)
    }
}