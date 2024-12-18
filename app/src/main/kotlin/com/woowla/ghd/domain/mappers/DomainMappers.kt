package com.woowla.ghd.domain.mappers

import com.woowla.ghd.data.remote.entities.ApiResponse
import com.woowla.ghd.data.remote.mappers.toSyncResultRateLimit
import com.woowla.ghd.domain.entities.PullRequest
import com.woowla.ghd.domain.entities.PullRequestSeen
import com.woowla.ghd.domain.entities.Review
import com.woowla.ghd.domain.entities.ReviewSeen
import com.woowla.ghd.domain.entities.SyncResultEntry
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

fun PullRequest.toPullRequestSeen(seenAt: Instant): PullRequestSeen {
    return PullRequestSeen(
        id = this.id,
        repoToCheckId = this.repoToCheckId,
        number = this.number,
        state = this.state,
        url = this.url,
        title = this.title,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        mergedAt = this.mergedAt,
        isDraft = this.isDraft,
        baseRef = this.baseRef,
        headRef = this.headRef,
        totalCommentsCount = this.totalCommentsCount,
        mergeStateStatus = this.mergeStateStatus,
        lastCommitCheckRollupStatus = this.lastCommitCheckRollupStatus,
        lastCommitSha1 = this.lastCommitSha1,
        author = this.author,
        appSeenAt = seenAt,
    )
}

fun Review.toReviewSeen(): ReviewSeen {
    return ReviewSeen(
        id = this.id,
        pullRequestId = this.pullRequestId,
        author = this.author,
        state = this.state,
        submittedAt = this.submittedAt,
        url = this.url,
    )
}

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