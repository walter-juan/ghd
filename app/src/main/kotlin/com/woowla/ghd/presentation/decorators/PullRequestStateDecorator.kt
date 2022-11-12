package com.woowla.ghd.presentation.decorators

import androidx.compose.ui.graphics.Color
import com.woowla.ghd.domain.entities.PullRequestState
import com.woowla.ghd.presentation.app.AppColors
import com.woowla.ghd.presentation.app.AppIcons
import com.woowla.ghd.presentation.app.i18n

class PullRequestStateDecorator(pullRequestState: PullRequestState) {
    val text: String = when(pullRequestState) {
        PullRequestState.OPEN -> i18n.pull_request_state_open
        PullRequestState.CLOSED -> i18n.pull_request_state_closed
        PullRequestState.MERGED -> i18n.pull_request_state_merged
        PullRequestState.DRAFT -> i18n.pull_request_state_draft
        PullRequestState.UNKNOWN -> i18n.pull_request_state_unknown
    }

    val iconResPath: String = when(pullRequestState) {
        PullRequestState.OPEN -> AppIcons.gitHubPrOpen
        PullRequestState.CLOSED -> AppIcons.gitHubPrClosed
        PullRequestState.MERGED -> AppIcons.gitHubPrMerged
        PullRequestState.DRAFT -> AppIcons.gitHubPrDraft
        PullRequestState.UNKNOWN -> AppIcons.gitHubPrUnknown
    }

    val iconTint: Color = when(pullRequestState) {
        PullRequestState.OPEN -> AppColors.gitPrOpen
        PullRequestState.CLOSED -> AppColors.gitPrClosed
        PullRequestState.MERGED -> AppColors.gitPrMerged
        PullRequestState.DRAFT -> AppColors.gitPrDraft
        PullRequestState.UNKNOWN -> Color.Gray
    }
}