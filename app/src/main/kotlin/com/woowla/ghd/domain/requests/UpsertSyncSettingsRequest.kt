package com.woowla.ghd.domain.requests

data class UpsertSyncSettingsRequest(
    val githubPatToken: String,
    val checkTimeout: Long?,
    val pullRequestCleanUpTimeout: Long?,
)