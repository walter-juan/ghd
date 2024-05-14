package com.woowla.ghd.notifications

import com.woowla.ghd.domain.entities.PullRequest
import com.woowla.ghd.domain.entities.ReleaseWithRepo

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

    fun newRelease(releaseWithRepo: ReleaseWithRepo) {
        client.sendNotification(
            title = "New release available",
            message = "${releaseWithRepo.repoToCheck.owner}/${releaseWithRepo.repoToCheck.name} ${releaseWithRepo.release.tagName}",
            type = NotificationType.NONE
        )
    }

    fun updateRelease(releaseWithRepo: ReleaseWithRepo) {
        client.sendNotification(
            title = "Updated release available",
            message = "${releaseWithRepo.repoToCheck.owner}/${releaseWithRepo.repoToCheck.name} ${releaseWithRepo.release.tagName}",
            type = NotificationType.NONE
        )
    }
}