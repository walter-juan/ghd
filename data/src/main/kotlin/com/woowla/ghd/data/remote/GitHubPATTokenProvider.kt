package com.woowla.ghd.data.remote

import com.woowla.ghd.domain.services.SyncSettingsService

class GitHubPATTokenProvider(
    private val syncSettingsService: SyncSettingsService
) {
    suspend fun get(): String? {
        return syncSettingsService.get().map { it.githubPatToken }.getOrNull()
    }
}