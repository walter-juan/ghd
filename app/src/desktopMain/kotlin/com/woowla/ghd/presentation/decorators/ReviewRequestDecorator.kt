package com.woowla.ghd.presentation.decorators

import com.woowla.compose.icon.collections.tabler.Tabler
import com.woowla.compose.icon.collections.tabler.tabler.Outline
import com.woowla.compose.icon.collections.tabler.tabler.outline.UserQuestion
import com.woowla.ghd.domain.entities.Author
import com.woowla.ghd.domain.entities.ReviewRequest
import com.woowla.ghd.presentation.i18nUi

class ReviewRequestDecorator(reviewRequest: ReviewRequest) {
    val authorLogin = if (reviewRequest.author?.login == null) {
        i18nUi.generic_unknown
    } else if (reviewRequest.author.isCopilotReviewer) {
        Author.copilotReviewerLoginShort
    } else {
        reviewRequest.author.login
    }
    val icon = Tabler.Outline.UserQuestion
}