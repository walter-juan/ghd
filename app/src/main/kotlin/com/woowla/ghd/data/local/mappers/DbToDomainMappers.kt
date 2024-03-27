package com.woowla.ghd.data.local.mappers

import com.woowla.ghd.data.local.db.entities.DbPullRequest
import com.woowla.ghd.data.local.db.entities.DbRelease
import com.woowla.ghd.data.local.db.entities.DbRepoToCheck
import com.woowla.ghd.data.local.db.entities.DbReview
import com.woowla.ghd.data.local.db.entities.DbSyncResult
import com.woowla.ghd.data.local.db.entities.DbSyncResultEntry
import com.woowla.ghd.data.local.db.entities.DbSyncSettings
import com.woowla.ghd.data.local.prop.AppProperties
import com.woowla.ghd.domain.entities.AppSettings
import com.woowla.ghd.domain.entities.CommitCheckRollupStatus
import com.woowla.ghd.domain.entities.MergeableGitHubState
import com.woowla.ghd.domain.entities.PullRequest
import com.woowla.ghd.domain.entities.PullRequestState
import com.woowla.ghd.domain.entities.Release
import com.woowla.ghd.domain.entities.RepoToCheck
import com.woowla.ghd.domain.entities.Review
import com.woowla.ghd.domain.entities.ReviewState
import com.woowla.ghd.domain.entities.SyncResult
import com.woowla.ghd.domain.entities.SyncResultEntry
import com.woowla.ghd.domain.entities.SyncSettings
import com.woowla.ghd.utils.enumValueOfOrDefault

fun AppProperties.toAppSettings(): AppSettings {
    return AppSettings(
        darkTheme = darkTheme,
        encryptedDatabase = encryptedDatabase,
        newPullRequestsNotificationsEnabled = newPullRequestsNotificationsEnabled,
        updatedPullRequestsNotificationsEnabled = updatedPullRequestsNotificationsEnabled,
        newReleaseNotificationsEnabled = newReleaseNotificationsEnabled,
        updatedReleaseNotificationsEnabled = updatedReleaseNotificationsEnabled
    )
}

fun DbSyncSettings.toSyncSettings(): SyncSettings {
    return SyncSettings(
        githubPatToken = githubPatToken,
        checkTimeout = checkTimeoutToValidCheckTimeout(checkTimeout),
        pullRequestCleanUpTimeout = cleanUpTimeoutToValidCleanUpTimeout(pullRequestCleanUpTimeout),
    )
}

fun DbSyncResult.toSyncResult(): SyncResult {
    return SyncResult(
        id = id.value,
        startAt = startAt,
        endAt = endAt,
        entries = entries.map { it.toSyncResultEntry() }
    )
}

fun DbSyncResultEntry.toSyncResultEntry(): SyncResultEntry {
    return if (this.isSuccess) {
        SyncResultEntry.Success(
            id = id.value,
            syncResultId = syncResultId.value,
            repoToCheck = repoToCheck?.toRepoToCheck(),
            startAt = startAt,
            endAt = endAt,
            origin = enumValueOfOrDefault(origin, SyncResultEntry.Origin.UNKNOWN),
        )
    } else {
        SyncResultEntry.Error(
            id = id.value,
            syncResultId = syncResultId.value,
            repoToCheck = repoToCheck?.toRepoToCheck(),
            startAt = startAt,
            endAt = endAt,
            origin = enumValueOfOrDefault(origin, SyncResultEntry.Origin.UNKNOWN),
            error = error,
            errorMessage = errorMessage,
        )
    }
}

fun DbRepoToCheck.toRepoToCheck(): RepoToCheck {
    return RepoToCheck(
        id = id.value,
        owner = owner,
        name = name,
        groupName = groupName,
        pullBranchRegex = pullBranchRegex,
        arePullRequestsEnabled = arePullRequestsEnabled,
        areReleasesEnabled = areReleasesEnabled,
    )
}

fun DbPullRequest.toPullRequest(): PullRequest {
    return PullRequest(
        id = id.value,
        number = number,
        url = url,
        state = enumValueOfOrDefault(state, PullRequestState.UNKNOWN),
        title = title,
        createdAt = createdAt,
        updatedAt = updatedAt,
        mergedAt = mergedAt,
        isDraft = isDraft,
        baseRef = baseRef,
        headRef = headRef,
        authorLogin = authorLogin,
        authorUrl = authorUrl,
        authorAvatarUrl = authorAvatarUrl,
        appSeenAt = appSeenAt,
        totalCommentsCount = totalCommentsCount,
        lastCommitCheckRollupStatus = enumValueOfOrDefault(lastCommitCheckRollupStatus, CommitCheckRollupStatus.UNKNOWN),
        mergeable = enumValueOfOrDefault(mergeable, MergeableGitHubState.UNKNOWN),
        reviews = reviews.map { it.toReview() },
        repoToCheck = repoToCheck.toRepoToCheck()
    )
}

fun DbReview.toReview(): Review {
    return Review(
        id = id.value,
        url = url,
        submittedAt = submittedAt,
        state = enumValueOfOrDefault(state, ReviewState.UNKNOWN),
        authorLogin = authorLogin,
        authorUrl = authorUrl,
        authorAvatarUrl = authorAvatarUrl,
        pullRequestId = "",
    )
}

fun DbRelease.toRelease(): Release {
    return Release(
        id = id.value,
        name = name,
        tagName = tagName,
        url = url,
        publishedAt = publishedAt,
        authorLogin = authorLogin,
        authorUrl = authorUrl,
        authorAvatarUrl = authorAvatarUrl,
        repoToCheck = repoToCheck.toRepoToCheck()
    )
}

private fun checkTimeoutToValidCheckTimeout(checkTimeout: Long?): Long {
    return SyncSettings.getValidCheckTimeout(checkTimeout)
}

private fun cleanUpTimeoutToValidCleanUpTimeout(cleanUpTimeout: Long?): Long {
    return SyncSettings.getValidPullRequestCleanUpTimeout(cleanUpTimeout)
}
