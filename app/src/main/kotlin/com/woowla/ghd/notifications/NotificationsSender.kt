package com.woowla.ghd.notifications

import com.woowla.ghd.domain.entities.PullRequest
import com.woowla.ghd.domain.entities.ReleaseWithRepo

interface NotificationsSender {
    companion object {
        fun getInstance(client: NotificationClient = NotificationClient()): NotificationsSender = NotificationsSenderDefault(client)
    }
    fun newPullRequest(pull: PullRequest)
    fun updatePullRequest(pull: PullRequest)
    fun newRelease(releaseWithRepo: ReleaseWithRepo)
    fun updateRelease(releaseWithRepo: ReleaseWithRepo)
}

private class NotificationsSenderDefault(
    private val client: NotificationClient = NotificationClient()
): NotificationsSender {
    override fun newPullRequest(pull: PullRequest) {
        client.sendNotification(
            title = "New pull request #${pull.number}",
            message = "${pull.author?.login} - ${pull.title}",
            type = NotificationType.NONE
        )
    }

    override fun updatePullRequest(pull: PullRequest) {
        client.sendNotification(
            title = "Updated pull request #${pull.number}",
            message = "${pull.author?.login} - ${pull.title}",
            type = NotificationType.NONE
        )
    }

    override fun newRelease(releaseWithRepo: ReleaseWithRepo) {
        client.sendNotification(
            title = "New release available",
            message = "${releaseWithRepo.repoToCheck.owner}/${releaseWithRepo.repoToCheck.name} ${releaseWithRepo.release.tagName}",
            type = NotificationType.NONE
        )
    }

    override fun updateRelease(releaseWithRepo: ReleaseWithRepo) {
        client.sendNotification(
            title = "Updated release available",
            message = "${releaseWithRepo.repoToCheck.owner}/${releaseWithRepo.repoToCheck.name} ${releaseWithRepo.release.tagName}",
            type = NotificationType.NONE
        )
    }
}