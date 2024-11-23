package com.woowla.ghd

import com.woowla.ghd.domain.entities.PullRequest
import com.woowla.ghd.domain.entities.ReleaseWithRepo
import com.woowla.ghd.notifications.NotificationsSender

class TestNotificationsSender : NotificationsSender {
    var newPullRequestCount = 0
    var updatePullRequestCount = 0
    var newReleaseCount = 0
    var updateReleaseCount = 0
    override fun newPullRequest(pull: PullRequest) {
        newPullRequestCount++
    }
    override fun updatePullRequest(pull: PullRequest) {
        updatePullRequestCount++
    }
    override fun newRelease(releaseWithRepo: ReleaseWithRepo) {
        newReleaseCount++
    }
    override fun updateRelease(releaseWithRepo: ReleaseWithRepo) {
        updateReleaseCount++
    }
}