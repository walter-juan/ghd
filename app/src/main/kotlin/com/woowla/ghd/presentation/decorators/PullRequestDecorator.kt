package com.woowla.ghd.presentation.decorators

import com.woowla.ghd.domain.entities.PullRequest
import com.woowla.ghd.presentation.app.i18n

class PullRequestDecorator(val pullRequest: PullRequest) {
    val fullRepo = "${pullRequest.repoToCheck.owner}/${pullRequest.repoToCheck.name}"
    val updatedAt = i18n.pull_request_updated_at(pullRequest.updatedAt)
    val authorWithTitle = "${pullRequest.authorLogin} - ${pullRequest.title}"
    val state = PullRequestStateDecorator(pullRequest.state)
    val totalCommentCount = if (pullRequest.totalCommentsCount != null && pullRequest.totalCommentsCount > 0) {
        "${pullRequest.totalCommentsCount} - "
    } else {
        ""
    }
}