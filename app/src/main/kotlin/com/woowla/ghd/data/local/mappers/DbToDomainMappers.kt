package com.woowla.ghd.data.local.mappers

import com.woowla.ghd.data.local.prop.AppProperties
import com.woowla.ghd.data.local.room.entities.DbPullRequest
import com.woowla.ghd.data.local.room.entities.DbRelease
import com.woowla.ghd.data.local.room.entities.DbRepoToCheck
import com.woowla.ghd.data.local.room.entities.DbReview
import com.woowla.ghd.domain.entities.AppSettings
import com.woowla.ghd.domain.entities.CommitCheckRollupStatus
import com.woowla.ghd.domain.entities.MergeableGitHubState
import com.woowla.ghd.domain.entities.PullRequest
import com.woowla.ghd.domain.entities.PullRequestState
import com.woowla.ghd.domain.entities.Release
import com.woowla.ghd.domain.entities.RepoToCheck
import com.woowla.ghd.domain.entities.Review
import com.woowla.ghd.domain.entities.ReviewState
import com.woowla.ghd.utils.enumValueOfOrDefault

fun AppProperties.toAppSettings(): AppSettings {
    return AppSettings(
        darkTheme = darkTheme,
        newPullRequestsNotificationsEnabled = newPullRequestsNotificationsEnabled,
        updatedPullRequestsNotificationsEnabled = updatedPullRequestsNotificationsEnabled,
        newReleaseNotificationsEnabled = newReleaseNotificationsEnabled,
        updatedReleaseNotificationsEnabled = updatedReleaseNotificationsEnabled
    )
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
