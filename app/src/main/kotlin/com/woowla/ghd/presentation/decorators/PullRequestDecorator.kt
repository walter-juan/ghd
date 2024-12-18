package com.woowla.ghd.presentation.decorators

import androidx.compose.ui.graphics.vector.ImageVector
import com.woowla.compose.icon.collections.tabler.Tabler
import com.woowla.compose.icon.collections.tabler.tabler.Outline
import com.woowla.compose.icon.collections.tabler.tabler.outline.List
import com.woowla.compose.icon.collections.tabler.tabler.outline.ListCheck
import com.woowla.compose.icon.collections.tabler.tabler.outline.PlaylistX
import com.woowla.compose.icon.collections.tabler.tabler.outline.UserCheck
import com.woowla.compose.icon.collections.tabler.tabler.outline.UserEdit
import com.woowla.compose.icon.collections.tabler.tabler.outline.UserQuestion
import com.woowla.compose.icon.collections.tabler.tabler.outline.UserX
import com.woowla.compose.icon.collections.tabler.tabler.outline.Users
import com.woowla.ghd.domain.entities.CommitCheckRollupStatus
import com.woowla.ghd.domain.entities.PullRequestWithRepoAndReviews
import com.woowla.ghd.domain.entities.ReviewState
import com.woowla.ghd.presentation.app.i18n

class PullRequestDecorator(val pullRequestWithReviews: PullRequestWithRepoAndReviews) {
    val fullRepo = "${pullRequestWithReviews.repoToCheck.owner}/${pullRequestWithReviews.repoToCheck.name}"
    val updatedAt = i18n.pull_request_updated(pullRequestWithReviews.pullRequest.updatedAt)
    val title = pullRequestWithReviews.pullRequest.title ?: i18n.generic_unknown
    val authorLogin = pullRequestWithReviews.pullRequest.author?.login ?: i18n.generic_unknown
    val state = PullRequestStateDecorator(pullRequestWithReviews.pullRequest.stateExtended)
    val comments = i18n.pull_request_comments(pullRequestWithReviews.pullRequest.totalCommentsCount ?: 0L)
    val commitChecks = when(pullRequestWithReviews.pullRequest.lastCommitCheckRollupStatus) {
        CommitCheckRollupStatus.ERROR -> "One or more checks failed"
        CommitCheckRollupStatus.EXPECTED -> "All checks are as expected"
        CommitCheckRollupStatus.FAILURE -> "One or more checks failed"
        CommitCheckRollupStatus.PENDING -> "Checks are running"
        CommitCheckRollupStatus.SUCCESS -> "All checks are success!"
        CommitCheckRollupStatus.UNKNOWN -> "Unknown status for the checks"
    }
    val commitChecksIcon: ImageVector = when(pullRequestWithReviews.pullRequest.lastCommitCheckRollupStatus) {
        CommitCheckRollupStatus.SUCCESS -> Tabler.Outline.ListCheck
        CommitCheckRollupStatus.ERROR -> Tabler.Outline.PlaylistX
        CommitCheckRollupStatus.FAILURE -> Tabler.Outline.PlaylistX
        CommitCheckRollupStatus.EXPECTED -> Tabler.Outline.List
        CommitCheckRollupStatus.PENDING -> Tabler.Outline.List
        CommitCheckRollupStatus.UNKNOWN -> Tabler.Outline.List
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
    fun reviewsIcon(): ImageVector {
        return when {
            pullRequestWithReviews.reviews.isEmpty() -> return Tabler.Outline.Users
            pullRequestWithReviews.reviews.size == 1 -> {
                when(pullRequestWithReviews.reviews.first().state) {
                    ReviewState.APPROVED -> return Tabler.Outline.UserCheck
                    ReviewState.CHANGES_REQUESTED -> return Tabler.Outline.UserX
                    ReviewState.COMMENTED -> return Tabler.Outline.UserQuestion
                    ReviewState.PENDING -> return Tabler.Outline.UserEdit
                    ReviewState.DISMISSED -> return Tabler.Outline.Users
                    ReviewState.UNKNOWN -> return Tabler.Outline.Users
                }
            }
            else -> {
                if (pullRequestWithReviews.reviews.any { review -> review.state == ReviewState.APPROVED }) {
                    Tabler.Outline.UserCheck
                } else {
                    return Tabler.Outline.Users
                }
            }
        }
    }
}