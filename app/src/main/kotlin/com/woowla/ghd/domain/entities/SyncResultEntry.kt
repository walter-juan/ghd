package com.woowla.ghd.domain.entities

import kotlin.time.Duration
import kotlinx.datetime.Instant

data class SyncResultEntry(
    val id: Long = 0,
    val syncResultId: Long,
    val repoToCheckId: Long?,
    val isSuccess: Boolean,
    val startAt: Instant,
    val endAt: Instant,
    val origin: Origin,
    val error: String?,
    val errorMessage: String?,
    val rateLimit: RateLimit?,
) : Comparable<SyncResultEntry> {
    companion object {
        val defaultComparator = compareBy<SyncResultEntry> { it.isSuccess }.thenByDescending { it.origin }.thenByDescending { it.repoToCheckId }
    }

    enum class Origin { OTHER, PULL, RELEASE, UNKNOWN }

    val duration: Duration by lazy { endAt.minus(startAt) }

    override fun compareTo(other: SyncResultEntry): Int {
        return defaultComparator.compare(this, other)
    }
}
