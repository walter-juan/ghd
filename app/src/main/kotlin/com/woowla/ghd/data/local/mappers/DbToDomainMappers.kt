package com.woowla.ghd.data.local.mappers

import com.woowla.ghd.data.local.prop.AppProperties
import com.woowla.ghd.data.local.room.entities.DbPullRequest
import com.woowla.ghd.data.local.room.entities.DbRelease
import com.woowla.ghd.data.local.room.entities.DbRepoToCheck
import com.woowla.ghd.data.local.room.entities.DbReview
import com.woowla.ghd.data.local.room.entities.DbSyncResult
import com.woowla.ghd.data.local.room.entities.DbSyncResultEntry
import com.woowla.ghd.data.local.room.entities.DbSyncSettings
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

// TODO relations
//fun DbSyncResultWithEntriesAndRepos.toSyncResult(): SyncResult {
//    return SyncResult(
//        id = dbSyncResult.id,
//        startAt = dbSyncResult.startAt,
//        endAt = dbSyncResult.endAt,
//        entries = dbSyncResultEntries.map { it.toSyncResultEntry() }
//    )
//}
fun DbSyncResult.toSyncResult(syncResultEntryList: List<SyncResultEntry>): SyncResult {
    return SyncResult(
        id = this.id,
        startAt = this.startAt,
        endAt = this.endAt,
        entries = syncResultEntryList
    )
}

// TODO relations
//fun DbSyncResultEntryWithRepoToCheck.toSyncResultEntry(): SyncResultEntry {
//    return if (dbSyncResultEntry.isSuccess) {
//        SyncResultEntry.Success(
//            id = dbSyncResultEntry.id,
//            syncResultId = dbSyncResultEntry.syncResultId,
//            repoToCheck = dbRepoToCheck?.toRepoToCheck(),
//            startAt = dbSyncResultEntry.startAt,
//            endAt = dbSyncResultEntry.endAt,
//            origin = enumValueOfOrDefault(dbSyncResultEntry.origin, SyncResultEntry.Origin.UNKNOWN),
//        )
//    } else {
//        SyncResultEntry.Error(
//            id = dbSyncResultEntry.id,
//            syncResultId = dbSyncResultEntry.syncResultId,
//            repoToCheck = dbRepoToCheck?.toRepoToCheck(),
//            startAt = dbSyncResultEntry.startAt,
//            endAt = dbSyncResultEntry.endAt,
//            origin = enumValueOfOrDefault(dbSyncResultEntry.origin, SyncResultEntry.Origin.UNKNOWN),
//            error = dbSyncResultEntry.error,
//            errorMessage = dbSyncResultEntry.errorMessage,
//        )
//    }
//}
fun DbSyncResultEntry.toSyncResultEntry(dbRepoToCheckList: List<DbRepoToCheck>): SyncResultEntry {
    val dbRepoToCheck = this.repoToCheckId?.let { id -> dbRepoToCheckList.firstOrNull { it.id == id } }
    return if (this.isSuccess) {
        SyncResultEntry.Success(
            id = this.id,
            syncResultId = this.syncResultId,
            repoToCheck = dbRepoToCheck?.toRepoToCheck(),
            startAt = this.startAt,
            endAt = this.endAt,
            origin = enumValueOfOrDefault(this.origin, SyncResultEntry.Origin.UNKNOWN),
        )
    } else {
        SyncResultEntry.Error(
            id = this.id,
            syncResultId = this.syncResultId,
            repoToCheck = dbRepoToCheck?.toRepoToCheck(),
            startAt = this.startAt,
            endAt = this.endAt,
            origin = enumValueOfOrDefault(this.origin, SyncResultEntry.Origin.UNKNOWN),
            error = this.error,
            errorMessage = this.errorMessage,
        )
    }
}

fun DbRepoToCheck.toRepoToCheck(): RepoToCheck {
    return RepoToCheck(
        id = id,
        owner = owner,
        name = name,
        groupName = groupName,
        pullBranchRegex = pullBranchRegex,
        arePullRequestsEnabled = arePullRequestsEnabled,
        areReleasesEnabled = areReleasesEnabled,
    )
}

fun DbPullRequest.toPullRequest(dbRepoToCheckList: List<DbRepoToCheck>, dbReviewList: List<DbReview>): PullRequest {
    val dbRepoToCheck = dbRepoToCheckList.first { it.id == this.repoToCheckId }

    return PullRequest(
        id = id,
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
        authorLogin = author?.login,
        authorUrl = author?.url,
        authorAvatarUrl = author?.avatarUrl,
        appSeenAt = appSeenAt,
        totalCommentsCount = totalCommentsCount,
        lastCommitCheckRollupStatus = enumValueOfOrDefault(lastCommitCheckRollupStatus, CommitCheckRollupStatus.UNKNOWN),
        mergeable = enumValueOfOrDefault(mergeable, MergeableGitHubState.UNKNOWN),
        reviews = dbReviewList.map { it.toReview() },
        repoToCheck = dbRepoToCheck.toRepoToCheck()
    )
}

fun DbReview.toReview(): Review {
    return Review(
        id = id,
        url = url,
        submittedAt = submittedAt,
        state = enumValueOfOrDefault(state, ReviewState.UNKNOWN),
        authorLogin = author?.login,
        authorUrl = author?.url,
        authorAvatarUrl = author?.avatarUrl,
        pullRequestId = "",
    )
}

fun DbRelease.toRelease(dbRepoToCheckList: List<DbRepoToCheck>): Release {
    val dbRepoToCheck = dbRepoToCheckList.first { it.id == this.repoToCheckId }
    return Release(
        id = id,
        name = name,
        tagName = tagName,
        url = url,
        publishedAt = publishedAt,
        authorLogin = author?.login,
        authorUrl = author?.url,
        authorAvatarUrl = author?.avatarUrl,
        repoToCheck = dbRepoToCheck.toRepoToCheck()
    )
}

private fun checkTimeoutToValidCheckTimeout(checkTimeout: Long?): Long {
    return SyncSettings.getValidCheckTimeout(checkTimeout)
}

private fun cleanUpTimeoutToValidCleanUpTimeout(cleanUpTimeout: Long?): Long {
    return SyncSettings.getValidPullRequestCleanUpTimeout(cleanUpTimeout)
}
