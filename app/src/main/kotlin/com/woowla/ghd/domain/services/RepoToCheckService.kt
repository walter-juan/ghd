package com.woowla.ghd.domain.services

import com.woowla.ghd.domain.entities.RepoToCheck

interface RepoToCheckService {
    suspend fun get(id: Long): Result<RepoToCheck>

    suspend fun getAll(): Result<List<RepoToCheck>>

    suspend fun delete(id: Long): Result<Unit>

    suspend fun save(repoToCheck: RepoToCheck): Result<Unit>

    suspend fun export(): Result<String>

    suspend fun import(content: String): Result<Unit>
}