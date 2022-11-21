package com.woowla.ghd.domain.usecases

import com.woowla.ghd.data.local.LocalDataSource
import com.woowla.ghd.domain.entities.PullRequest
import com.woowla.ghd.domain.mappers.DbMappers
import com.woowla.ghd.utils.UseCaseWithoutParams

class GetAllPullRequestsUseCase(
    private val localDataSource: LocalDataSource = LocalDataSource()
) : UseCaseWithoutParams<List<PullRequest>>() {

    override suspend fun perform(): Result<List<PullRequest>> {
        return localDataSource.getAllPullRequests()
            .map { dbPullRequests ->
                DbMappers.INSTANCE.dbPullRequestToPullRequest(dbPullRequests)
            }.map { pullRequests ->
                pullRequests.sorted()
            }
    }
}