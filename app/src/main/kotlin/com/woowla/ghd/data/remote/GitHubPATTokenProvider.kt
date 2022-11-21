package com.woowla.ghd.data.remote

import com.woowla.ghd.domain.usecases.GetSyncSettingsUseCase

class GitHubPATTokenProvider(
    private val getSyncSettingsUseCase: GetSyncSettingsUseCase = GetSyncSettingsUseCase()
) {
    suspend fun get(): String? {
        return getSyncSettingsUseCase.execute().map { it.githubPatToken }.getOrNull()
    }
}