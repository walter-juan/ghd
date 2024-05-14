package com.woowla.ghd.presentation.decorators

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.woowla.ghd.domain.entities.CommitCheckRollupStatus
import com.woowla.ghd.domain.entities.MergeableGitHubState
import com.woowla.ghd.domain.entities.PullRequestWithRepoAndReviews
import com.woowla.ghd.presentation.app.AppColors.info
import com.woowla.ghd.presentation.app.AppColors.success
import com.woowla.ghd.presentation.app.AppColors.warning
import com.woowla.ghd.presentation.app.i18n

class PullRequestDecorator(val pullRequestWithReviews: PullRequestWithRepoAndReviews) {
    val fullRepo = "${pullRequestWithReviews.repoToCheck.owner}/${pullRequestWithReviews.repoToCheck.name}"
    val updatedAt = i18n.pull_request_updated(pullRequestWithReviews.pullRequest.updatedAt)
    val title = pullRequestWithReviews.pullRequest.title ?: i18n.generic_unknown
    val authorLogin = pullRequestWithReviews.pullRequest.author?.login ?: i18n.generic_unknown
    val state = PullRequestStateDecorator(pullRequestWithReviews.pullRequest.stateWithDraft)
    val comments = i18n.pull_request_comments(pullRequestWithReviews.pullRequest.totalCommentsCount ?: 0L)
    val commitChecks = when(pullRequestWithReviews.pullRequest.lastCommitCheckRollupStatus) {
        CommitCheckRollupStatus.ERROR -> "One or more checks failed"
        CommitCheckRollupStatus.EXPECTED -> "All checks are as expected"
        CommitCheckRollupStatus.FAILURE -> "One or more checks failed"
        CommitCheckRollupStatus.PENDING -> "Checks are running"
        CommitCheckRollupStatus.SUCCESS -> "All checks are success!"
        CommitCheckRollupStatus.UNKNOWN -> "Unknown status for the checks"
    }
    val showCommitsCheckBadge = pullRequestWithReviews.pullRequest.lastCommitCheckRollupStatus != CommitCheckRollupStatus.EXPECTED && pullRequestWithReviews.pullRequest.lastCommitCheckRollupStatus != CommitCheckRollupStatus.SUCCESS
    @Composable
    fun commitsCheckBadgeColor(): Color = when(pullRequestWithReviews.pullRequest.lastCommitCheckRollupStatus) {
        CommitCheckRollupStatus.ERROR -> MaterialTheme.colorScheme.error
        CommitCheckRollupStatus.EXPECTED -> MaterialTheme.colorScheme.success
        CommitCheckRollupStatus.FAILURE -> MaterialTheme.colorScheme.error
        CommitCheckRollupStatus.PENDING -> MaterialTheme.colorScheme.warning
        CommitCheckRollupStatus.SUCCESS -> MaterialTheme.colorScheme.success
        CommitCheckRollupStatus.UNKNOWN -> MaterialTheme.colorScheme.info
    }
    val mergeable: String = when(pullRequestWithReviews.pullRequest.mergeable) {
        MergeableGitHubState.CONFLICTING -> "Conflicts found, update before merge"
        MergeableGitHubState.MERGEABLE -> "No conflicts found!"
        MergeableGitHubState.UNKNOWN -> "Unknown status to know if the PR can be merged for conflicts"
    }
    val showMergeableBadge = !pullRequestWithReviews.pullRequest.canBeMergedByMergeable
    @Composable
    fun mergeableBadgeColor(): Color = when(pullRequestWithReviews.pullRequest.mergeable) {
        MergeableGitHubState.CONFLICTING -> MaterialTheme.colorScheme.error
        MergeableGitHubState.MERGEABLE -> MaterialTheme.colorScheme.success
        MergeableGitHubState.UNKNOWN -> MaterialTheme.colorScheme.info
    }

    val showReviewsBadge = !pullRequestWithReviews.canBeMergedByReviews
    @Composable
    fun reviewsBadgeColor(): Color {
        return if (pullRequestWithReviews.reviews.isEmpty()) {
            MaterialTheme.colorScheme.warning
        } else {
            if (pullRequestWithReviews.canBeMergedByReviews) {
                MaterialTheme.colorScheme.success
            } else {
                MaterialTheme.colorScheme.error
            }
        }
    }
    fun reviews(): String {
        return if (pullRequestWithReviews.reviews.isEmpty()) {
            "Pending to be reviewed"
        } else {
            val reviews = pullRequestWithReviews.reviews.joinToString {
                val reviewDecorator = ReviewDecorator(it)
                "${reviewDecorator.authorLogin} (${reviewDecorator.state})"
            }
            "Reviewed by $reviews"
        }
    }
}