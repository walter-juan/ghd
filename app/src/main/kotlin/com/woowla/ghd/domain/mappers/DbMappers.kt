package com.woowla.ghd.domain.mappers

import com.woowla.ghd.data.local.db.entities.DbPullRequest
import com.woowla.ghd.data.local.db.entities.DbRelease
import com.woowla.ghd.data.local.db.entities.DbRepoToCheck
import com.woowla.ghd.data.local.db.entities.DbReview
import com.woowla.ghd.data.local.db.entities.DbSyncSettings
import com.woowla.ghd.domain.entities.CommitCheckRollupStatus
import com.woowla.ghd.domain.entities.MergeableGitHubState
import com.woowla.ghd.domain.entities.PullRequest
import com.woowla.ghd.domain.entities.PullRequestGitHubState
import com.woowla.ghd.domain.entities.Release
import com.woowla.ghd.domain.entities.RepoToCheck
import com.woowla.ghd.domain.entities.Review
import com.woowla.ghd.domain.entities.ReviewState
import com.woowla.ghd.domain.entities.SyncSettings
import com.woowla.ghd.utils.enumValueOfOrDefault

fun DbSyncSettings.toSyncSettings(): SyncSettings {
    return SyncSettings(
        githubPatToken = githubPatToken,
        checkTimeout = checkTimeoutToValidCheckTimeout(checkTimeout),
        pullRequestCleanUpTimeout = cleanUpTimeoutToValidCleanUpTimeout(pullRequestCleanUpTimeout),
        synchronizedAt = synchronizedAt
    )
}

fun DbRepoToCheck.toRepoToCheck(): RepoToCheck {
    return RepoToCheck(
        id = id.value,
        owner = owner,
        name = name,
        pullNotificationsEnabled = pullNotificationsEnabled,
        releaseNotificationsEnabled = releaseNotificationsEnabled,
        groupName = groupName,
        pullBranchRegex = pullBranchRegex
    )
}

fun DbPullRequest.toPullRequest(): PullRequest {
    return PullRequest(
        id = id.value,
        number = number,
        url = url,
        gitHubState = enumValueOfOrDefault(state, PullRequestGitHubState.UNKNOWN),
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
        repoToCheckId = repoToCheck.id.value,
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
        authorAvatarUrl = authorAvatarUrl
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
        repoToCheckId = repoToCheck.id.value,
        repoToCheck = repoToCheck.toRepoToCheck()
    )
}

private fun checkTimeoutToValidCheckTimeout(checkTimeout: Long?): Long {
    return SyncSettings.getValidCheckTimeout(checkTimeout)
}

private fun cleanUpTimeoutToValidCleanUpTimeout(cleanUpTimeout: Long?): Long {
    return SyncSettings.getValidPullRequestCleanUpTimeout(cleanUpTimeout)
}
