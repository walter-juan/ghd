package com.woowla.ghd.presentation.decorators

import com.woowla.ghd.domain.entities.Review
import com.woowla.ghd.domain.entities.ReviewState
import com.woowla.ghd.presentation.i18nUi

class ReviewDecorator(review: Review) {
    val submittedAt: String = review.submittedAt?.let { i18nUi.review_submitted(it) } ?: i18nUi.generic_unknown
    val authorLogin = review.author?.login ?: i18nUi.generic_unknown
    val state = when (review.state) {
        ReviewState.APPROVED -> "approved"
        ReviewState.CHANGES_REQUESTED -> "changes requested"
        ReviewState.COMMENTED -> "commented"
        ReviewState.DISMISSED -> "dismissed"
        ReviewState.PENDING -> "pending"
        ReviewState.UNKNOWN -> "unknown"
    }
}