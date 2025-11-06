package com.woowla.ghd.presentation.decorators

import com.woowla.compose.icon.collections.tabler.Tabler
import com.woowla.compose.icon.collections.tabler.tabler.Outline
import com.woowla.compose.icon.collections.tabler.tabler.outline.UserCheck
import com.woowla.compose.icon.collections.tabler.tabler.outline.UserEdit
import com.woowla.compose.icon.collections.tabler.tabler.outline.UserOff
import com.woowla.compose.icon.collections.tabler.tabler.outline.UserQuestion
import com.woowla.compose.icon.collections.tabler.tabler.outline.UserSearch
import com.woowla.compose.icon.collections.tabler.tabler.outline.UserX
import com.woowla.ghd.domain.entities.Author
import com.woowla.ghd.domain.entities.Review
import com.woowla.ghd.domain.entities.ReviewState
import com.woowla.ghd.presentation.i18nUi

class ReviewDecorator(review: Review) {
    val authorLogin = if (review.author?.login == null) {
        i18nUi.generic_unknown
    } else if (review.author.isCopilotReviewer) {
        Author.copilotReviewerLoginShort
    } else {
        review.author.login
    }
    val state = when (review.state) {
        ReviewState.APPROVED -> "approved"
        ReviewState.CHANGES_REQUESTED -> "changes requested"
        ReviewState.COMMENTED -> "commented"
        ReviewState.DISMISSED -> "dismissed"
        ReviewState.PENDING -> "pending"
        ReviewState.UNKNOWN -> "unknown"
    }
    val icon = when (review.state) {
        ReviewState.APPROVED -> Tabler.Outline.UserCheck
        ReviewState.CHANGES_REQUESTED -> Tabler.Outline.UserX
        ReviewState.COMMENTED -> Tabler.Outline.UserEdit
        ReviewState.PENDING -> Tabler.Outline.UserSearch
        ReviewState.DISMISSED -> Tabler.Outline.UserOff
        ReviewState.UNKNOWN -> Tabler.Outline.UserQuestion
    }
}