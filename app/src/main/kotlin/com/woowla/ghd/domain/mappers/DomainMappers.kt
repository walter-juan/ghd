package com.woowla.ghd.domain.mappers

import com.woowla.ghd.domain.entities.AppSettings
import com.woowla.ghd.domain.entities.PullRequest
import com.woowla.ghd.domain.entities.PullRequestState
import com.woowla.ghd.domain.entities.PullRequestStateWithDraft
import com.woowla.ghd.domain.entities.Release
import com.woowla.ghd.domain.entities.RepoToCheck
import com.woowla.ghd.domain.entities.SyncSettings
import com.woowla.ghd.domain.requests.UpsertAppSettingsRequest
import com.woowla.ghd.domain.requests.UpsertPullRequestRequest
import com.woowla.ghd.domain.requests.UpsertReleaseRequest
import com.woowla.ghd.domain.requests.UpsertRepoToCheckRequest
import com.woowla.ghd.domain.requests.UpsertSyncSettingsRequest

fun AppSettings.toUpsertAppSettingsRequest(): UpsertAppSettingsRequest {
    return UpsertAppSettingsRequest(
        darkTheme = darkTheme,
        encryptedDatabase = encryptedDatabase,
        newPullRequestsNotificationsEnabled = newPullRequestsNotificationsEnabled,
        updatedPullRequestsNotificationsEnabled = updatedPullRequestsNotificationsEnabled,
        newReleaseNotificationsEnabled = newReleaseNotificationsEnabled,
        updatedReleaseNotificationsEnabled = updatedReleaseNotificationsEnabled
    )
}

fun SyncSettings.toUpsertSyncSettingsRequest(): UpsertSyncSettingsRequest {
    return UpsertSyncSettingsRequest(
        githubPatToken = githubPatToken ?: "",
        checkTimeout = checkTimeout,
        pullRequestCleanUpTimeout = pullRequestCleanUpTimeout
    )
}

fun PullRequest.toUpsertPullRequestRequest(): UpsertPullRequestRequest {
    return UpsertPullRequestRequest(
        id = id,
        number = number,
        url = url,
        state = state.toString(),
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
        mergeable = mergeable.toString(),
        lastCommitCheckRollupStatus = lastCommitCheckRollupStatus.toString(),
        repoToCheckId = repoToCheckId
    )
}

fun Release.toUpsertReleaseRequest(): UpsertReleaseRequest {
    return UpsertReleaseRequest(
        id = id,
        name = name,
        tagName = tagName,
        url = url,
        publishedAt = publishedAt,
        authorLogin = authorLogin,
        authorUrl = authorUrl,
        authorAvatarUrl = authorAvatarUrl,
        repoToCheckId = repoToCheckId
    )
}

fun RepoToCheck.toUpsertRepoToCheckRequest(): UpsertRepoToCheckRequest {
    return UpsertRepoToCheckRequest(
        id = id,
        owner = owner,
        name = name,
        groupName = groupName,
        pullBranchRegex = pullBranchRegex
    )
}

fun PullRequestState.toPullRequestState(isDraft: Boolean): PullRequestStateWithDraft {
    return when (this) {
        PullRequestState.OPEN -> if (isDraft) {
            PullRequestStateWithDraft.DRAFT
        } else {
            PullRequestStateWithDraft.OPEN
        }
        PullRequestState.MERGED -> PullRequestStateWithDraft.MERGED
        PullRequestState.CLOSED -> PullRequestStateWithDraft.CLOSED
        PullRequestState.UNKNOWN, null -> PullRequestStateWithDraft.UNKNOWN
    }
}