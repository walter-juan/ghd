package com.woowla.ghd.presentation.decorators

import androidx.compose.ui.graphics.vector.ImageVector
import com.woowla.compose.icon.collections.tabler.Tabler
import com.woowla.compose.icon.collections.tabler.tabler.Outline
import com.woowla.compose.icon.collections.tabler.tabler.outline.List
import com.woowla.compose.icon.collections.tabler.tabler.outline.ListCheck
import com.woowla.compose.icon.collections.tabler.tabler.outline.PlaylistX
import com.woowla.ghd.domain.entities.CommitCheckRollupStatus
import com.woowla.ghd.domain.entities.PullRequestWithRepoAndReviews
import com.woowla.ghd.core.extensions.toRelativeString
import com.woowla.ghd.presentation.i18nUi
import kotlin.time.Duration.Companion.days

class PullRequestDecorator(val pullRequestWithReviews: PullRequestWithRepoAndReviews) {
    val fullRepo = "${pullRequestWithReviews.repoToCheck.repository?.owner}/${pullRequestWithReviews.repoToCheck.repository?.name}"
    val createdAt = pullRequestWithReviews.pullRequest.createdAt.toRelativeString(maximumDays = 999.days)
    val mergedAt = pullRequestWithReviews.pullRequest.mergedAt?.toRelativeString(maximumDays = 999.days) ?: ""
    val closedAt = pullRequestWithReviews.pullRequest.closedAt?.toRelativeString(maximumDays = 999.days) ?: ""
    val title = pullRequestWithReviews.pullRequest.title ?: i18nUi.generic_unknown
    val state = PullRequestStateDecorator(pullRequestWithReviews.pullRequest.stateExtended)
    val comments = i18nUi.pull_request_comments(pullRequestWithReviews.pullRequest.totalCommentsCount ?: 0L)
    val commitChecks = when (pullRequestWithReviews.pullRequest.lastCommitCheckRollupStatus) {
        CommitCheckRollupStatus.ERROR -> "Checks failed"
        CommitCheckRollupStatus.EXPECTED -> "Checks passed"
        CommitCheckRollupStatus.FAILURE -> "Checks failed"
        CommitCheckRollupStatus.PENDING -> "Running checks"
        CommitCheckRollupStatus.SUCCESS -> "Checks successful"
        CommitCheckRollupStatus.UNKNOWN -> "Checks unknown"
    }
    val commitChecksIcon: ImageVector = when (pullRequestWithReviews.pullRequest.lastCommitCheckRollupStatus) {
        CommitCheckRollupStatus.SUCCESS -> Tabler.Outline.ListCheck
        CommitCheckRollupStatus.ERROR -> Tabler.Outline.PlaylistX
        CommitCheckRollupStatus.FAILURE -> Tabler.Outline.PlaylistX
        CommitCheckRollupStatus.EXPECTED -> Tabler.Outline.List
        CommitCheckRollupStatus.PENDING -> Tabler.Outline.List
        CommitCheckRollupStatus.UNKNOWN -> Tabler.Outline.List
    }
}