package com.woowla.ghd

import com.woowla.ghd.domain.entities.PullRequest
import com.woowla.ghd.domain.entities.ReleaseWithRepo
import com.woowla.ghd.domain.entities.Review
import com.woowla.ghd.notifications.NotificationsSender

class TestNotificationsSender : NotificationsSender {
    var newPullRequestCount = 0
    var newPullRequestReviewCount = 0
    var yourPullRequestReviewDismissed = 0
    var changePullRequestChecksCount = 0
    var mergeablePullRequestCount = 0
    var newReleaseCount = 0

    override fun newPullRequest(pull: PullRequest) {
        newPullRequestCount++
    }

    override fun newPullRequestReview(pull: PullRequest, review: Review) {
        newPullRequestReviewCount++
    }

    override fun yourPullRequestReviewDismissed(pull: PullRequest) {
        yourPullRequestReviewDismissed++
    }

    override fun pullRequestChecksChanged(pull: PullRequest) {
        changePullRequestChecksCount++
    }

    override fun mergeablePullRequest(pull: PullRequest) {
        mergeablePullRequestCount++
    }

    override fun newRelease(releaseWithRepo: ReleaseWithRepo) {
        newReleaseCount++
    }
}