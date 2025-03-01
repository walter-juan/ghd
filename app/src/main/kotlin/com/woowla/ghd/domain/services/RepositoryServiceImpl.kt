package com.woowla.ghd.domain.services

import com.woowla.ghd.domain.data.RemoteDataSource
import com.woowla.ghd.domain.entities.Repository

class RepositoryServiceImpl(
    private val remoteDataSource: RemoteDataSource
) : RepositoryService {
    override suspend fun search(text: String?, owner: String?): Result<List<Repository>> {
        return remoteDataSource.search(
            text = text,
            owner = owner,
        ).map {
            it.data
        }
    }
}