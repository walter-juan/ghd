package com.woowla.ghd.domain.mappers

import com.woowla.ghd.domain.entities.AppSettings
import com.woowla.ghd.domain.entities.PullRequest
import com.woowla.ghd.domain.entities.PullRequestGitHubState
import com.woowla.ghd.domain.entities.PullRequestState
import com.woowla.ghd.domain.entities.Release
import com.woowla.ghd.domain.entities.RepoToCheck
import com.woowla.ghd.domain.entities.SyncSettings
import com.woowla.ghd.domain.requests.UpsertAppSettingsRequest
import com.woowla.ghd.domain.requests.UpsertPullRequestRequest
import com.woowla.ghd.domain.requests.UpsertReleaseRequest
import com.woowla.ghd.domain.requests.UpsertRepoToCheckRequest
import com.woowla.ghd.domain.requests.UpsertSyncSettings

fun AppSettings.toUpsertAppSettingsRequest(): UpsertAppSettingsRequest {
    return UpsertAppSettingsRequest(
        darkTheme = darkTheme,
        featurePreviewNewCards = featurePreviewNewCards,
        featurePreviewNewCardsBoldStyle = featurePreviewNewCardsBoldStyle
    )
}

fun SyncSettings.toUpsertSyncSettings(): UpsertSyncSettings {
    return UpsertSyncSettings(
        githubPatToken = githubPatToken ?: "",
        checkTimeout = checkTimeout,
        synchronizedAt = synchronizedAt,
        pullRequestCleanUpTimeout = pullRequestCleanUpTimeout
    )
}

fun PullRequest.toUpsertPullRequestRequest(): UpsertPullRequestRequest {
    return UpsertPullRequestRequest(
        id = id,
        number = number,
        url = url,
        state = gitHubState.toString(),
        title = title,
        createdAt = createdAt,
        updatedAt = updatedAt,
        mergedAt = mergedAt,
        draft = draft,
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
        pullNotificationsEnabled = pullNotificationsEnabled,
        releaseNotificationsEnabled = releaseNotificationsEnabled,
        groupName = groupName,
        pullBranchRegex = pullBranchRegex
    )
}

fun PullRequestGitHubState.toPullRequestState(isDraft: Boolean): PullRequestState {
    return when (this) {
        PullRequestGitHubState.OPEN -> if (isDraft) {
            PullRequestState.DRAFT
        } else {
            PullRequestState.OPEN
        }
        PullRequestGitHubState.MERGED -> PullRequestState.MERGED
        PullRequestGitHubState.CLOSED -> PullRequestState.CLOSED
        PullRequestGitHubState.UNKNOWN, null -> PullRequestState.UNKNOWN
    }
}