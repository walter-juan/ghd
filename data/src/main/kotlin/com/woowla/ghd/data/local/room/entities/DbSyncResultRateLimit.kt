package com.woowla.ghd.data.local.room.entities

import androidx.room.ColumnInfo
import com.woowla.ghd.domain.entities.RateLimit
import io.mcarle.konvert.api.KonvertFrom
import io.mcarle.konvert.api.KonvertTo
import kotlinx.datetime.Instant

@KonvertFrom(RateLimit::class)
@KonvertTo(RateLimit::class)
data class DbSyncResultRateLimit(
    @ColumnInfo(name = "ratelimit_limit") val limit: Long?,
    @ColumnInfo(name = "ratelimit_remaining") val remaining: Long?,
    @ColumnInfo(name = "ratelimit_used") val used: Long?,
    @ColumnInfo(name = "ratelimit_reset") val reset: Instant?,
    @ColumnInfo(name = "ratelimit_resource") val resource: String?,
) {
    companion object
}
