package com.woowla.ghd.data.local.mappers

import com.woowla.ghd.data.local.prop.AppProperties
import com.woowla.ghd.data.local.room.entities.DbPullRequest
import com.woowla.ghd.domain.entities.AppSettings
import com.woowla.ghd.domain.entities.CommitCheckRollupStatus
import com.woowla.ghd.domain.entities.MergeableGitHubState
import com.woowla.ghd.domain.entities.PullRequest
import com.woowla.ghd.domain.entities.PullRequestState
import com.woowla.ghd.domain.entities.Release
import com.woowla.ghd.domain.entities.RepoToCheck
import com.woowla.ghd.domain.entities.Review
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

fun DbPullRequest.toPullRequest(repoToCheckList: List<RepoToCheck>, reviewList: List<Review>): PullRequest {
    val repoToCheck = repoToCheckList.first { it.id == this.repoToCheckId }

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
        reviews = reviewList,
        repoToCheck = repoToCheck
    )
}
