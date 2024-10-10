package com.woowla.ghd.presentation.decorators

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.woowla.compose.icon.collections.tabler.Tabler
import com.woowla.compose.icon.collections.tabler.tabler.Outline
import com.woowla.compose.icon.collections.tabler.tabler.outline.*
import com.woowla.ghd.domain.entities.PullRequestStateWithDraft
import com.woowla.ghd.presentation.app.AppColors.gitPrClosed
import com.woowla.ghd.presentation.app.AppColors.gitPrDraft
import com.woowla.ghd.presentation.app.AppColors.gitPrMerged
import com.woowla.ghd.presentation.app.AppColors.gitPrOpen
import com.woowla.ghd.presentation.app.i18n

class PullRequestStateDecorator(private val pullRequestStateWithDraft: PullRequestStateWithDraft) {
    val text: String = when(pullRequestStateWithDraft) {
        PullRequestStateWithDraft.OPEN -> i18n.pull_request_state_open
        PullRequestStateWithDraft.CLOSED -> i18n.pull_request_state_closed
        PullRequestStateWithDraft.MERGED -> i18n.pull_request_state_merged
        PullRequestStateWithDraft.DRAFT -> i18n.pull_request_state_draft
        PullRequestStateWithDraft.UNKNOWN -> i18n.pull_request_state_unknown
    }

    val icon: ImageVector = when(pullRequestStateWithDraft) {
        PullRequestStateWithDraft.OPEN -> Tabler.Outline.GitPullRequest
        PullRequestStateWithDraft.CLOSED -> Tabler.Outline.GitPullRequestClosed
        PullRequestStateWithDraft.MERGED -> Tabler.Outline.GitMerge
        PullRequestStateWithDraft.DRAFT -> Tabler.Outline.GitPullRequestDraft
        PullRequestStateWithDraft.UNKNOWN -> Tabler.Outline.QuestionMark
    }

    @Composable
    fun iconTint(): Color = when(pullRequestStateWithDraft) {
        PullRequestStateWithDraft.OPEN -> MaterialTheme.colorScheme.gitPrOpen
        PullRequestStateWithDraft.CLOSED -> MaterialTheme.colorScheme.gitPrClosed
        PullRequestStateWithDraft.MERGED -> MaterialTheme.colorScheme.gitPrMerged
        PullRequestStateWithDraft.DRAFT -> MaterialTheme.colorScheme.gitPrDraft
        PullRequestStateWithDraft.UNKNOWN -> Color.Gray
    }
}