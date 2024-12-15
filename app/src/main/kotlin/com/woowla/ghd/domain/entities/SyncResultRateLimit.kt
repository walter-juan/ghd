package com.woowla.ghd.domain.entities

import androidx.room.ColumnInfo
import kotlinx.datetime.Instant

data class SyncResultRateLimit(
    @ColumnInfo(name = "ratelimit_limit") val limit: Long?,
    @ColumnInfo(name = "ratelimit_remaining") val remaining: Long?,
    @ColumnInfo(name = "ratelimit_used") val used: Long?,
    @ColumnInfo(name = "ratelimit_reset") val reset: Instant?,
    @ColumnInfo(name = "ratelimit_resource") val resource: String?,
) {
    val percentageUsed: Int?
        get() = if (limit == null || used == null) {
            null
        } else {
            ((used * 100) / limit).toInt()
        }
    val percentageRemaining: Int?
        get() = if (limit == null || remaining == null) {
            null
        } else {
            ((remaining * 100) / limit).toInt()
        }
}
