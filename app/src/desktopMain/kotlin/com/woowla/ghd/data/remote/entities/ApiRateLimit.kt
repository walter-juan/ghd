package com.woowla.ghd.data.remote.entities

import kotlin.time.Instant

data class ApiRateLimit(
    val limit: Long?,
    val remaining: Long?,
    val used: Long?,
    val reset: Instant?,
    val resource: String?,
)
