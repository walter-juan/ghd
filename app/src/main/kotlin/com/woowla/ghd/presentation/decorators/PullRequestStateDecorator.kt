package com.woowla.ghd.presentation.decorators

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.woowla.compose.octoicons.GitMerge
import com.woowla.compose.octoicons.GitPullRequest
import com.woowla.compose.octoicons.GitPullRequestClosed
import com.woowla.compose.octoicons.GitPullRequestDraft
import com.woowla.compose.octoicons.OctoiconsRes
import com.woowla.compose.octoicons.Question
import com.woowla.ghd.domain.entities.PullRequestState
import com.woowla.ghd.presentation.app.AppColors
import com.woowla.ghd.presentation.app.AppColors.gitPrClosed
import com.woowla.ghd.presentation.app.AppColors.gitPrDraft
import com.woowla.ghd.presentation.app.AppColors.gitPrMerged
import com.woowla.ghd.presentation.app.AppColors.gitPrOpen
import com.woowla.ghd.presentation.app.i18n

class PullRequestStateDecorator(private val pullRequestState: PullRequestState) {
    val text: String = when(pullRequestState) {
        PullRequestState.OPEN -> i18n.pull_request_state_open
        PullRequestState.CLOSED -> i18n.pull_request_state_closed
        PullRequestState.MERGED -> i18n.pull_request_state_merged
        PullRequestState.DRAFT -> i18n.pull_request_state_draft
        PullRequestState.UNKNOWN -> i18n.pull_request_state_unknown
    }

    val iconResPath: String = when(pullRequestState) {
        PullRequestState.OPEN -> OctoiconsRes.GitPullRequest
        PullRequestState.CLOSED -> OctoiconsRes.GitPullRequestClosed
        PullRequestState.MERGED -> OctoiconsRes.GitMerge
        PullRequestState.DRAFT -> OctoiconsRes.GitPullRequestDraft
        PullRequestState.UNKNOWN -> OctoiconsRes.Question
    }

    @Composable
    fun iconTint(): Color = when(pullRequestState) {
        PullRequestState.OPEN -> MaterialTheme.colorScheme.gitPrOpen
        PullRequestState.CLOSED -> MaterialTheme.colorScheme.gitPrClosed
        PullRequestState.MERGED -> MaterialTheme.colorScheme.gitPrMerged
        PullRequestState.DRAFT -> MaterialTheme.colorScheme.gitPrDraft
        PullRequestState.UNKNOWN -> Color.Gray
    }
}