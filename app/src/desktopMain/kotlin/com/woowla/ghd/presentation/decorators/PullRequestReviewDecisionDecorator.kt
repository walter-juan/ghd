package com.woowla.ghd.presentation.decorators

import com.woowla.compose.icon.collections.tabler.Tabler
import com.woowla.compose.icon.collections.tabler.tabler.Outline
import com.woowla.compose.icon.collections.tabler.tabler.outline.AlertCircle
import com.woowla.compose.icon.collections.tabler.tabler.outline.CircleCheck
import com.woowla.compose.icon.collections.tabler.tabler.outline.CircleDashed
import com.woowla.compose.icon.collections.tabler.tabler.outline.HelpCircle
import com.woowla.ghd.domain.entities.PullRequestReviewDecision

class PullRequestReviewDecisionDecorator(reviewDecision: PullRequestReviewDecision) {
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
}