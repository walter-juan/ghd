package com.woowla.ghd.domain.services

import com.woowla.ghd.domain.entities.Repository

interface RepositoryService {
    suspend fun search(text: String? = null, user: String? = null): Result<List<Repository>>
}