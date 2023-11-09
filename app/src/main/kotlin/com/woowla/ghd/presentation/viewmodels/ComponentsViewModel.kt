package com.woowla.ghd.presentation.viewmodels

import cafe.adriel.voyager.core.model.ScreenModel
import com.woowla.ghd.domain.entities.CommitCheckRollupStatus
import com.woowla.ghd.domain.entities.MergeableGitHubState
import com.woowla.ghd.domain.entities.PullRequest
import com.woowla.ghd.domain.entities.PullRequestGitHubState
import com.woowla.ghd.domain.entities.Release
import com.woowla.ghd.domain.entities.RepoToCheck
import com.woowla.ghd.notifications.NotificationClient
import com.woowla.ghd.notifications.NotificationType
import com.woowla.ghd.notifications.NotificationsSender
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.hours

class ComponentsViewModel(
    private val notificationClient: NotificationClient = NotificationClient(),
    private val notificationsSender: NotificationsSender = NotificationsSender(client = notificationClient)
) : ScreenModel {
    private val pullRequest = PullRequest(
        id = "magna",
        number = 3592,
        url = "http://www.bing.com/search?q=mandamus",
        gitHubState = PullRequestGitHubState.CLOSED,
        title = "awesome pull request",
        createdAt = Clock.System.now(),
        updatedAt = Clock.System.now().plus(1.hours),
        mergedAt = null,
        draft = false,
        baseRef = null,
        headRef = null,
        authorLogin = "janine",
        authorUrl = null,
        authorAvatarUrl = null,
        appSeenAt = null,
        totalCommentsCount = null,
        repoToCheckId = 8608,
        lastCommitCheckRollupStatus = CommitCheckRollupStatus.EXPECTED,
        mergeable = MergeableGitHubState.MERGEABLE,
        reviews = listOf(),
        repoToCheck = RepoToCheck(
            id = 3818,
            owner = "accusata",
            name = "Rusty Saunders",
            pullNotificationsEnabled = false,
            releaseNotificationsEnabled = false,
            groupName = null,
            pullBranchRegex = null
        )
    )

    private val release = Release(
        id = "nec",
        name = "v1.0.0",
        tagName = "Janine Russell",
        url = "https://search.yahoo.com/search?p=nibh",
        publishedAt = Clock.System.now(),
        authorLogin = "janine",
        authorUrl = null,
        authorAvatarUrl = null,
        repoToCheckId = 9678,
        repoToCheck = RepoToCheck(
            id = 9154,
            owner = "hendrerit",
            name = "Serena Levine",
            pullNotificationsEnabled = false,
            releaseNotificationsEnabled = false,
            groupName = null,
            pullBranchRegex = null
        )

    )

    fun sendByType(type: NotificationType) {
        notificationClient.sendNotification(
            title = "title",
            message = "message by type $type",
            type = type
        )
    }

    fun sendNewPullRequest() {
        notificationsSender.newPullRequest(pullRequest)
    }

    fun sendUpdatedPullRequest() {
        notificationsSender.updatePullRequest(pullRequest)
    }

    fun sendNewRelease() {
        notificationsSender.newRelease(release)
    }

    fun sendUpdatedRelease() {
        notificationsSender.updateRelease(release)
    }
}