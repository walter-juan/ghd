package com.woowla.ghd.presentation.decorators

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.woowla.ghd.domain.entities.CommitCheckRollupStatus
import com.woowla.ghd.domain.entities.MergeableGitHubState
import com.woowla.ghd.domain.entities.PullRequest
import com.woowla.ghd.presentation.app.AppColors.info
import com.woowla.ghd.presentation.app.AppColors.success
import com.woowla.ghd.presentation.app.AppColors.warning
import com.woowla.ghd.presentation.app.i18n

class PullRequestDecorator(val pullRequest: PullRequest) {
    val fullRepo = "${pullRequest.repoToCheck.owner}/${pullRequest.repoToCheck.name}"
    val updatedAt = i18n.pull_request_updated(pullRequest.updatedAt)
    val title = pullRequest.title ?: i18n.generic_unknown
    val authorLogin = pullRequest.authorLogin ?: i18n.generic_unknown
    val state = PullRequestStateDecorator(pullRequest.stateWithDraft)
    val comments = i18n.pull_request_comments(pullRequest.totalCommentsCount ?: 0L)
    val commitChecks = when(pullRequest.lastCommitCheckRollupStatus) {
        CommitCheckRollupStatus.ERROR -> "One or more checks failed"
        CommitCheckRollupStatus.EXPECTED -> "All checks are as expected"
        CommitCheckRollupStatus.FAILURE -> "One or more checks failed"
        CommitCheckRollupStatus.PENDING -> "Checks are running"
        CommitCheckRollupStatus.SUCCESS -> "All checks are success!"
        CommitCheckRollupStatus.UNKNOWN -> "Unknown status for the checks"
    }
    val showCommitsCheckBadge = pullRequest.lastCommitCheckRollupStatus != CommitCheckRollupStatus.EXPECTED && pullRequest.lastCommitCheckRollupStatus != CommitCheckRollupStatus.SUCCESS
    @Composable
    fun commitsCheckBadgeColor(): Color = when(pullRequest.lastCommitCheckRollupStatus) {
        CommitCheckRollupStatus.ERROR -> MaterialTheme.colorScheme.error
        CommitCheckRollupStatus.EXPECTED -> MaterialTheme.colorScheme.success
        CommitCheckRollupStatus.FAILURE -> MaterialTheme.colorScheme.error
        CommitCheckRollupStatus.PENDING -> MaterialTheme.colorScheme.warning
        CommitCheckRollupStatus.SUCCESS -> MaterialTheme.colorScheme.success
        CommitCheckRollupStatus.UNKNOWN -> MaterialTheme.colorScheme.info
    }
    val mergeable: String = when(pullRequest.mergeable) {
        MergeableGitHubState.CONFLICTING -> "Conflicts found, update before merge"
        MergeableGitHubState.MERGEABLE -> "No conflicts found!"
        MergeableGitHubState.UNKNOWN -> "Unknown status to know if the PR can be merged for conflicts"
    }
    val showMergeableBadge = !pullRequest.canBeMergedByMergeable
    @Composable
    fun mergeableBadgeColor(): Color = when(pullRequest.mergeable) {
        MergeableGitHubState.CONFLICTING -> MaterialTheme.colorScheme.error
        MergeableGitHubState.MERGEABLE -> MaterialTheme.colorScheme.success
        MergeableGitHubState.UNKNOWN -> MaterialTheme.colorScheme.info
    }

    val showReviewsBadge = !pullRequest.canBeMergedByReviews
    @Composable
    fun reviewsBadgeColor(): Color {
        return if (pullRequest.reviews.isEmpty()) {
            MaterialTheme.colorScheme.warning
        } else {
            if (pullRequest.canBeMergedByReviews) {
                MaterialTheme.colorScheme.success
            } else {
                MaterialTheme.colorScheme.error
            }
        }
    }
    fun reviews(): String {
        return if (pullRequest.reviews.isEmpty()) {
            "Pending to be reviewed"
        } else {
            val reviews = pullRequest.reviews.joinToString {
                val reviewDecorator = ReviewDecorator(it)
                "${reviewDecorator.authorLogin} (${reviewDecorator.state})"
            }
            "Reviewed by $reviews"
        }
    }
}