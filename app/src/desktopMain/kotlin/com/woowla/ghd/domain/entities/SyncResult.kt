package com.woowla.ghd.domain.entities

import kotlin.time.Duration
import kotlin.time.Instant

data class SyncResult(
    val id: Long = 0,
    val startAt: Instant,
    val endAt: Instant?,
) {
    enum class Status { SUCCESS, WARNING, ERROR, CRITICAL }

    val duration: Duration? = endAt?.minus(startAt)
}