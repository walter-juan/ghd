package com.woowla.ghd.domain.requests

import com.woowla.ghd.domain.entities.SyncResultEntry
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class UpsertSyncResultEntryRequest(
    val id: Long? = null,
    val syncResultId: Long,
    val repoToCheckId: Long?,
    val isSuccess: Boolean,
    val startAt: Instant,
    val endAt: Instant,
    val origin: String,
    val error: String?,
    val errorMessage: String?,
)

fun <T> Result<T>.toUpsertSyncResultEntryRequest(
    syncResultId: Long,
    repoToCheckId: Long?,
    origin: SyncResultEntry.Origin,
    startAt: Instant
): UpsertSyncResultEntryRequest {
    return this.fold(
        onSuccess = {
            UpsertSyncResultEntryRequest(
                isSuccess = true,
                syncResultId = syncResultId,
                repoToCheckId = repoToCheckId,
                startAt = startAt,
                endAt = Clock.System.now(),
                origin = origin.toString(),
                error = null,
                errorMessage = null,
            )
        },
        onFailure = { throwable ->
            UpsertSyncResultEntryRequest(
                isSuccess = false,
                syncResultId = syncResultId,
                repoToCheckId = repoToCheckId,
                startAt = startAt,
                endAt = Clock.System.now(),
                origin = origin.toString(),
                error = throwable.javaClass.name,
                errorMessage = throwable.message,
            )
        }
    )
}
