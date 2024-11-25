package com.woowla.ghd.presentation.decorators

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.woowla.compose.icon.collections.tabler.Tabler
import com.woowla.compose.icon.collections.tabler.tabler.Outline
import com.woowla.compose.icon.collections.tabler.tabler.outline.*
import com.woowla.ghd.domain.entities.PullRequestStateExtended
import com.woowla.ghd.presentation.app.AppColors.gitPrClosed
import com.woowla.ghd.presentation.app.AppColors.gitPrDraft
import com.woowla.ghd.presentation.app.AppColors.gitPrMerged
import com.woowla.ghd.presentation.app.AppColors.gitPrOpen
import com.woowla.ghd.presentation.app.i18n

class PullRequestStateDecorator(private val pullRequestStateExtended: PullRequestStateExtended) {
    val text: String = when(pullRequestStateExtended) {
        PullRequestStateExtended.OPEN -> i18n.pull_request_state_open
        PullRequestStateExtended.CLOSED -> i18n.pull_request_state_closed
        PullRequestStateExtended.MERGED -> i18n.pull_request_state_merged
        PullRequestStateExtended.DRAFT -> i18n.pull_request_state_draft
        PullRequestStateExtended.UNKNOWN -> i18n.pull_request_state_unknown
    }

    val icon: ImageVector = when(pullRequestStateExtended) {
        PullRequestStateExtended.OPEN -> Tabler.Outline.GitPullRequest
        PullRequestStateExtended.CLOSED -> Tabler.Outline.GitPullRequestClosed
        PullRequestStateExtended.MERGED -> Tabler.Outline.GitMerge
        PullRequestStateExtended.DRAFT -> Tabler.Outline.GitPullRequestDraft
        PullRequestStateExtended.UNKNOWN -> Tabler.Outline.QuestionMark
    }

    @Composable
    fun iconTint(): Color = when(pullRequestStateExtended) {
        PullRequestStateExtended.OPEN -> MaterialTheme.colorScheme.gitPrOpen
        PullRequestStateExtended.CLOSED -> MaterialTheme.colorScheme.gitPrClosed
        PullRequestStateExtended.MERGED -> MaterialTheme.colorScheme.gitPrMerged
        PullRequestStateExtended.DRAFT -> MaterialTheme.colorScheme.gitPrDraft
        PullRequestStateExtended.UNKNOWN -> Color.Gray
    }
}