package com.woowla.ghd.domain.entities

import kotlin.time.Instant

data class RateLimit(
    val limit: Long?,
    val remaining: Long?,
    val used: Long?,
    val reset: Instant?,
    val resource: String?,
) {
    val percentageUsed: Int? = if (limit == null || used == null) { null } else { ((used * 100) / limit).toInt() }
    val percentageRemaining: Int? = if (limit == null || remaining == null) { null } else { ((remaining * 100) / limit).toInt() }
}
