package com.woowla.ghd.domain.requests

import kotlinx.datetime.Instant

data class UpsertSyncResultRequest(
    val id: Long? = null,
    val startAt: Instant,
    val endAt: Instant?,
)