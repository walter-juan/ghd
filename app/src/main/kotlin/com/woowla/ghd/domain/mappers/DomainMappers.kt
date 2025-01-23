package com.woowla.ghd.domain.mappers

import com.woowla.ghd.data.remote.entities.ApiResponse
import com.woowla.ghd.data.remote.mappers.toSyncResultRateLimit
import com.woowla.ghd.domain.entities.SyncResultEntry
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

fun <T: Any> Result<ApiResponse<T>>.toSyncResultEntry(
    syncResultId: Long,
    repoToCheckId: Long?,
    origin: SyncResultEntry.Origin,
    startAt: Instant
): SyncResultEntry {
    return this.fold(
        onSuccess = {
            SyncResultEntry(
                isSuccess = true,
                syncResultId = syncResultId,
                repoToCheckId = repoToCheckId,
                startAt = startAt,
                endAt = Clock.System.now(),
                origin = origin,
                error = null,
                errorMessage = null,
                rateLimit = it.rateLimit.toSyncResultRateLimit(),
            )
        },
        onFailure = { throwable ->
            SyncResultEntry(
                isSuccess = false,
                syncResultId = syncResultId,
                repoToCheckId = repoToCheckId,
                startAt = startAt,
                endAt = Clock.System.now(),
                origin = origin,
                error = throwable.javaClass.name,
                errorMessage = throwable.message,
                rateLimit = null,
            )
        }
    )
}