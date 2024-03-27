package com.woowla.ghd.domain.mappers

import com.woowla.ghd.domain.entities.RepoToCheck
import com.woowla.ghd.domain.entities.SyncResultEntry
import com.woowla.ghd.domain.requests.UpsertRepoToCheckRequest
import com.woowla.ghd.domain.requests.UpsertSyncResultEntryRequest
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

fun RepoToCheck.toUpsertRepoToCheckRequest(): UpsertRepoToCheckRequest {
    return UpsertRepoToCheckRequest(
        id = id,
        owner = owner,
        name = name,
        groupName = groupName,
        pullBranchRegex = pullBranchRegex,
        arePullRequestsEnabled = arePullRequestsEnabled,
        areReleasesEnabled = areReleasesEnabled
    )
}

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