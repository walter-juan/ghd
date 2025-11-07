package com.woowla.ghd.presentation.decorators

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.woowla.compose.icon.collections.tabler.Tabler
import com.woowla.compose.icon.collections.tabler.tabler.Outline
import com.woowla.compose.icon.collections.tabler.tabler.outline.BrandGithubCopilot
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

class ReviewDecorator(private val review: Review) {
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
    val icon = if (review.author?.isCopilotReviewer == true) {
        Tabler.Outline.BrandGithubCopilot
    } else {
        when (review.state) {
            ReviewState.APPROVED -> Tabler.Outline.UserCheck
            ReviewState.CHANGES_REQUESTED -> Tabler.Outline.UserX
            ReviewState.COMMENTED -> Tabler.Outline.UserEdit
            ReviewState.PENDING -> Tabler.Outline.UserSearch
            ReviewState.DISMISSED -> Tabler.Outline.UserOff
            ReviewState.UNKNOWN -> Tabler.Outline.UserQuestion
        }
    }

    @Composable
    fun stateColor(): Color = when (review.state) {
        ReviewState.APPROVED -> MaterialTheme.colorScheme.secondary
        ReviewState.CHANGES_REQUESTED -> MaterialTheme.colorScheme.error
        ReviewState.COMMENTED -> MaterialTheme.colorScheme.secondary
        ReviewState.DISMISSED -> MaterialTheme.colorScheme.secondary
        ReviewState.PENDING -> MaterialTheme.colorScheme.secondary
        ReviewState.UNKNOWN -> MaterialTheme.colorScheme.secondary
    }
}