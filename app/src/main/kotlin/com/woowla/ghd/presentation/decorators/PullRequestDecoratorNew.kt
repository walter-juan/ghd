package com.woowla.ghd.presentation.decorators

import com.woowla.ghd.domain.entities.PullRequest
import com.woowla.ghd.extensions.format
import com.woowla.ghd.presentation.app.i18n
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter

class PullRequestDecoratorNew(val pullRequest: PullRequest) {
    val fullRepo = "${pullRequest.repoToCheck.owner}/${pullRequest.repoToCheck.name}"
    val updatedAt = i18n.pull_request_updated(pullRequest.updatedAt)
    val title = pullRequest.title ?: i18n.generic_unknown
    val authorLogin = pullRequest.authorLogin ?: i18n.generic_unknown
    val state = PullRequestStateDecorator(pullRequest.state)
    val comments = i18n.pull_request_comments(pullRequest.totalCommentsCount ?: 0L)
}