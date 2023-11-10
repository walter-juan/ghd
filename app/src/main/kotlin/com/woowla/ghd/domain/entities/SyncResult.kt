package com.woowla.ghd.domain.entities

import kotlinx.datetime.Instant
import kotlin.time.Duration

class SyncResult(
    val id: Long,
    val startAt: Instant,
    val endAt: Instant?,
    entries: List<SyncResultEntry>,
) {
    enum class Status { SUCCESS, WARNING, ERROR, CRITICAL }

    val entriesSize: Int
    val errorPercentage: Int
    val duration: Duration?
    val status: Status

    init {
        entriesSize = entries.size
        duration = endAt?.minus(startAt)

        errorPercentage = if (entriesSize == 0) {
            0
        } else {
            entries.count { it.isError() } * 100 / entries.size
        }

        status = when(errorPercentage) {
            0 -> Status.SUCCESS
            in 0..5 -> Status.WARNING
            in 5..25 -> Status.ERROR
            else -> Status.CRITICAL
        }
    }
}