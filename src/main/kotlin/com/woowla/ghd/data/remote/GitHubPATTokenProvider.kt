package com.woowla.ghd.data.remote

import com.woowla.ghd.domain.usecases.GetAppSettingsUseCase

class GitHubPATTokenProvider(
    private val getAppSettingsUseCase: GetAppSettingsUseCase = GetAppSettingsUseCase()
) {
    suspend fun get(): String? {
        return getAppSettingsUseCase.execute().map { it.githubPatToken }.getOrNull()
    }
}