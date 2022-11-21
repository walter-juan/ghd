package com.woowla.ghd.domain.requests

import kotlinx.datetime.Instant

data class UpsertSyncSettings(
    val githubPatToken: String,
    val checkTimeout: Long?,
    val synchronizedAt: Instant?,
    val pullRequestCleanUpTimeout: Long?,
)