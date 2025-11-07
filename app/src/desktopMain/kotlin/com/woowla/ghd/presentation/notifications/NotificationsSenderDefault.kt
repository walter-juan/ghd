package com.woowla.ghd.presentation.notifications

import com.woowla.ghd.domain.entities.PullRequest
import com.woowla.ghd.domain.entities.ReleaseWithRepo
import com.woowla.ghd.domain.entities.Review
import com.woowla.ghd.core.notifications.NotificationClient
import com.woowla.ghd.core.notifications.NotificationType
import com.woowla.ghd.domain.notifications.NotificationsSender
import com.woowla.ghd.presentation.decorators.PullRequestStateDecorator
import com.woowla.ghd.presentation.decorators.ReviewDecorator

class NotificationsSenderDefault(private val client: NotificationClient) : NotificationsSender {
    override fun newPullRequest(pull: PullRequest) {
        val pullRequestDecorator = PullRequestStateDecorator(pull.stateExtended)
        client.sendNotification(
            title = "${pullRequestDecorator.text} pull request",
            message = "#${pull.number} ${pull.author?.login} - ${pull.title}",
            type = NotificationType.NONE
        )
    }

    override fun newPullRequestReview(pull: PullRequest, review: Review) {
        val reviewDecorator = ReviewDecorator(review)
        client.sendNotification(
            title = "New review: ${reviewDecorator.state}",
            message = "${pull.title}",
            type = NotificationType.NONE
        )
    }

    override fun yourPullRequestReviewDismissed(pull: PullRequest) {
        client.sendNotification(
            title = "Your review dismissed",
            message = "${pull.title}",
            type = NotificationType.NONE
        )
    }

    override fun pullRequestChecksChanged(pull: PullRequest) {
        client.sendNotification(
            title = "Checks ${pull.lastCommitCheckRollupStatus}",
            message = "${pull.title}",
            type = NotificationType.NONE
        )
    }

    override fun mergeablePullRequest(pull: PullRequest) {
        client.sendNotification(
            title = "Ready to be merged",
            message = "${pull.title}",
            type = NotificationType.NONE
        )
    }

    override fun newRelease(releaseWithRepo: ReleaseWithRepo) {
        client.sendNotification(
            title = "New release ${releaseWithRepo.repoToCheck.repository?.name}",
            message = "${releaseWithRepo.release.tagName} ${releaseWithRepo.repoToCheck.repository?.owner}/${releaseWithRepo.repoToCheck.repository?.name}",
            type = NotificationType.NONE
        )
    }
}