package com.woowla.ghd.domain.requests

import kotlinx.datetime.Instant

data class UpsertSyncResultEntryRequest(
    val id: Long? = null,
    val syncResultId: Long,
    val repoToCheckId: Long?,
    val isSuccess: Boolean,
    val startAt: Instant,
    val endAt: Instant,
    val origin: String,
    val error: String?,
    val errorMessage: String?,
)
