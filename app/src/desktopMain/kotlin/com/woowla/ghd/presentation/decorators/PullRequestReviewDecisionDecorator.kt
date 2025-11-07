package com.woowla.ghd.presentation.decorators

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.woowla.compose.icon.collections.tabler.Tabler
import com.woowla.compose.icon.collections.tabler.tabler.Outline
import com.woowla.compose.icon.collections.tabler.tabler.outline.AlertCircle
import com.woowla.compose.icon.collections.tabler.tabler.outline.CircleCheck
import com.woowla.compose.icon.collections.tabler.tabler.outline.CircleDashed
import com.woowla.compose.icon.collections.tabler.tabler.outline.HelpCircle
import com.woowla.ghd.domain.entities.PullRequestReviewDecision
import com.woowla.ghd.domain.entities.ReviewState
import com.woowla.ghd.presentation.app.AppColors.warning

class PullRequestReviewDecisionDecorator(private val reviewDecision: PullRequestReviewDecision) {
    val text = when (reviewDecision) {
        PullRequestReviewDecision.APPROVED -> "Review approved"
        PullRequestReviewDecision.CHANGES_REQUESTED -> "Changes requested"
        PullRequestReviewDecision.REVIEW_REQUIRED -> "Review pending"
        PullRequestReviewDecision.UNKNOWN -> "Review unknown"
    }
    val icon = when (reviewDecision) {
        PullRequestReviewDecision.APPROVED -> Tabler.Outline.CircleCheck
        PullRequestReviewDecision.CHANGES_REQUESTED -> Tabler.Outline.AlertCircle
        PullRequestReviewDecision.REVIEW_REQUIRED -> Tabler.Outline.CircleDashed
        PullRequestReviewDecision.UNKNOWN -> Tabler.Outline.HelpCircle
    }
    @Composable
    fun color(): Color = when (reviewDecision) {
        PullRequestReviewDecision.APPROVED -> MaterialTheme.colorScheme.secondary
        PullRequestReviewDecision.CHANGES_REQUESTED -> MaterialTheme.colorScheme.error
        PullRequestReviewDecision.REVIEW_REQUIRED -> MaterialTheme.colorScheme.secondary
        PullRequestReviewDecision.UNKNOWN -> MaterialTheme.colorScheme.warning
    }
}