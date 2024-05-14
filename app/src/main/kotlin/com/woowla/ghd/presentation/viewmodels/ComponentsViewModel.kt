package com.woowla.ghd.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.woowla.ghd.domain.entities.Author
import com.woowla.ghd.domain.entities.CommitCheckRollupStatus
import com.woowla.ghd.domain.entities.MergeableGitHubState
import com.woowla.ghd.domain.entities.PullRequest
import com.woowla.ghd.domain.entities.PullRequestState
import com.woowla.ghd.domain.entities.Release
import com.woowla.ghd.domain.entities.ReleaseWithRepo
import com.woowla.ghd.domain.entities.RepoToCheck
import com.woowla.ghd.notifications.NotificationClient
import com.woowla.ghd.notifications.NotificationType
import com.woowla.ghd.notifications.NotificationsSender
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.hours

class ComponentsViewModel(
    private val notificationClient: NotificationClient = NotificationClient(),
    private val notificationsSender: NotificationsSender = NotificationsSender(client = notificationClient)
) : ViewModel() {
    private val repoToCheck = RepoToCheck(
        id = 9154,
        owner = "hendrerit",
        name = "Serena Levine",
        groupName = null,
        pullBranchRegex = null,
        arePullRequestsEnabled = true,
        areReleasesEnabled = true
    )
    private val pullRequest = PullRequest(
        id = "magna",
        number = 3592,
        url = "http://www.bing.com/search?q=mandamus",
        state = PullRequestState.CLOSED,
        title = "awesome pull request",
        createdAt = Clock.System.now(),
        updatedAt = Clock.System.now().plus(1.hours),
        mergedAt = null,
        isDraft = false,
        baseRef = null,
        headRef = null,
        author = Author(
            login = "janine",
            url = null,
            avatarUrl = null
        ),
        appSeenAt = null,
        totalCommentsCount = null,
        lastCommitCheckRollupStatus = CommitCheckRollupStatus.EXPECTED,
        mergeable = MergeableGitHubState.MERGEABLE,
        repoToCheckId = repoToCheck.id
    )
    private val release = Release(
        id = "nec",
        name = "v1.0.0",
        tagName = "Janine Russell",
        url = "https://search.yahoo.com/search?p=nibh",
        publishedAt = Clock.System.now(),
        author = Author(
            login = "janine",
            url = null,
            avatarUrl = null
        ),
        repoToCheckId = repoToCheck.id
    )
    private val releaseWithRepo = ReleaseWithRepo(release, repoToCheck)

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
        notificationsSender.newRelease(releaseWithRepo)
    }

    fun sendUpdatedRelease() {
        notificationsSender.updateRelease(releaseWithRepo)
    }
}