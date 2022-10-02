package com.woowla.ghd.notifications

import com.woowla.ghd.domain.entities.PullRequest
import com.woowla.ghd.domain.entities.Release

class NotificationsSender(
    private val client: NotificationClient = NotificationClient()
) {
    fun newPullRequest(pull: PullRequest) {
        client.sendNotification(
            title = "New pull request #${pull.number}",
            message = "${pull.authorLogin} - ${pull.title}",
            type = NotificationType.NONE
        )
    }

    fun updatePullRequest(pull: PullRequest) {
        client.sendNotification(
            title = "Updated pull request #${pull.number}",
            message = "${pull.authorLogin} - ${pull.title}",
            type = NotificationType.NONE
        )
    }

    fun newRelease(release: Release) {
        client.sendNotification(
            title = "New release available",
            message = "${release.repoToCheck.owner}/${release.repoToCheck.name} ${release.tagName}",
            type = NotificationType.NONE
        )
    }

    fun updateRelease(release: Release) {
        client.sendNotification(
            title = "Updated release available",
            message = "${release.repoToCheck.owner}/${release.repoToCheck.name} ${release.tagName}",
            type = NotificationType.NONE
        )
    }
}