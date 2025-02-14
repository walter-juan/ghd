package com.woowla.ghd.notifications

import com.woowla.ghd.domain.entities.PullRequest
import com.woowla.ghd.domain.entities.ReleaseWithRepo
import com.woowla.ghd.domain.entities.Review

interface NotificationsSender {
    fun newPullRequest(pull: PullRequest)
    fun newPullRequestReview(pull: PullRequest, review: Review)
    fun newPullRequestReReview(pull: PullRequest)
    fun changePullRequestChecks(pull: PullRequest)
    fun mergeablePullRequest(pull: PullRequest)
    fun newRelease(releaseWithRepo: ReleaseWithRepo)
}

