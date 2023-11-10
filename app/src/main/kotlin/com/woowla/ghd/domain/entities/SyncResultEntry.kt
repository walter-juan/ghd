package com.woowla.ghd.domain.entities

import kotlinx.datetime.Instant
import kotlin.time.Duration

sealed class SyncResultEntry(
    open val id: Long,
    open val syncResultId: Long,
    open val repoToCheck: RepoToCheck?,
    open val startAt: Instant,
    open val endAt: Instant,
    open val origin: Origin,
): Comparable<SyncResultEntry> {
    companion object {
        val defaultComparator = compareBy<SyncResultEntry> { it.isSuccess() }.thenByDescending { it.origin }.thenByDescending { it.repoToCheck?.id }
    }

    fun isSuccess () = this is Success
    fun isError () = this is Error

    val duration: Duration by lazy { endAt.minus(startAt) }

    enum class Origin { OTHER, PULL, RELEASE, UNKNOWN }

    data class Success(
        override val id: Long,
        override val syncResultId: Long,
        override val repoToCheck: RepoToCheck?,
        override val startAt: Instant,
        override val endAt: Instant,
        override val origin: Origin,
    ): SyncResultEntry(id, syncResultId, repoToCheck, startAt, endAt, origin)

    data class Error(
        override val id: Long,
        override val syncResultId: Long,
        override val repoToCheck: RepoToCheck?,
        override val startAt: Instant,
        override val endAt: Instant,
        override val origin: Origin,
        val error: String?,
        val errorMessage: String?,
    ): SyncResultEntry(id, syncResultId, repoToCheck, startAt, endAt, origin)

    override fun compareTo(other: SyncResultEntry): Int {
        return defaultComparator.compare(this, other)
    }
}
