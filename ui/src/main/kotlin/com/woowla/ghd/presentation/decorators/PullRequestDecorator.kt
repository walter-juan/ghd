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
import com.woowla.ghd.extensions.toRelativeString
import com.woowla.ghd.i18n
import kotlin.time.Duration.Companion.days

class PullRequestDecorator(val pullRequestWithReviews: PullRequestWithRepoAndReviews) {
    val fullRepo = "${pullRequestWithReviews.repoToCheck.owner}/${pullRequestWithReviews.repoToCheck.name}"
    val createdAt = pullRequestWithReviews.pullRequest.createdAt.toRelativeString(maximumDays = 999.days)
    val mergedAt = pullRequestWithReviews.pullRequest.mergedAt?.toRelativeString(maximumDays = 999.days) ?: ""
    val closedAt = pullRequestWithReviews.pullRequest.closedAt?.toRelativeString(maximumDays = 999.days) ?: ""
    val title = pullRequestWithReviews.pullRequest.title ?: i18n.generic_unknown
    val state = PullRequestStateDecorator(pullRequestWithReviews.pullRequest.stateExtended)
    val comments = i18n.pull_request_comments(pullRequestWithReviews.pullRequest.totalCommentsCount ?: 0L)
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
    fun reviewsNonApproved(): String {
        return if (pullRequestWithReviews.reviews.isEmpty()) {
            "Review pending"
        } else {
            val reviews = pullRequestWithReviews.reviews
                .groupBy { it.state }
                .toList()
                .filter { (_, reviews) ->
                    reviews.isNotEmpty()
                }
                .joinToString(separator = ", ") { (state, reviews) ->
                    val reviewDecorator = ReviewDecorator(reviews.first())
                    "${reviews.size} ${reviewDecorator.state}"
                }
            "${pullRequestWithReviews.reviews.size} Reviews ($reviews)"
        }
    }
    fun reviewsIcon(): ImageVector {
        return when {
            pullRequestWithReviews.reviews.isEmpty() -> return Tabler.Outline.Users
            pullRequestWithReviews.reviews.size == 1 -> {
                when (pullRequestWithReviews.reviews.first().state) {
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
                    return Tabler.Outline.UserQuestion
                }
            }
        }
    }
}