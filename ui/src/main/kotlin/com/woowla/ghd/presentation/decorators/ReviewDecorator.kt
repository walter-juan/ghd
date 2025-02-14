package com.woowla.ghd.presentation.decorators

import com.woowla.ghd.domain.entities.Review
import com.woowla.ghd.domain.entities.ReviewState
import com.woowla.ghd.i18n

class ReviewDecorator(review: Review) {
    val submittedAt: String = review.submittedAt?.let { i18n.review_submitted(it) } ?: i18n.generic_unknown
    val authorLogin = review.author?.login ?: i18n.generic_unknown
    val state = when (review.state) {
        ReviewState.APPROVED -> "approved"
        ReviewState.CHANGES_REQUESTED -> "changes requested"
        ReviewState.COMMENTED -> "commented"
        ReviewState.DISMISSED -> "dismissed"
        ReviewState.PENDING -> "pending"
        ReviewState.UNKNOWN -> "unknown"
    }
}