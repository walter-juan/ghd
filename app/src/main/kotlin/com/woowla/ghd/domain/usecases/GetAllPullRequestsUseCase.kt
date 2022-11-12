package com.woowla.ghd.domain.usecases

import com.woowla.ghd.data.local.LocalDataSource
import com.woowla.ghd.domain.entities.PullRequest
import com.woowla.ghd.domain.mappers.DbMappers
import com.woowla.ghd.utils.UseCaseWithoutParams

class GetAllPullRequestsUseCase(
    private val localDataSource: LocalDataSource = LocalDataSource()
) : UseCaseWithoutParams<List<PullRequest>>() {

    override suspend fun perform(): Result<List<PullRequest>> {
        val dbPullRequests = localDataSource.getAllPullRequests().getOrThrow()

        val pullRequests = dbPullRequests.map { dbPullRequest ->
            val dbRepoToCheck = localDataSource.getRepoToCheck(dbPullRequest.repoToCheckId).getOrThrow()
            val repoToCheck = DbMappers.INSTANCE.dbRepoToCheckToRepoToCheck(dbRepoToCheck)
            DbMappers.INSTANCE.dbPullRequestToPullRequest(dbPullRequest, repoToCheck)
        }.sorted()

        return Result.success(pullRequests)
    }
}