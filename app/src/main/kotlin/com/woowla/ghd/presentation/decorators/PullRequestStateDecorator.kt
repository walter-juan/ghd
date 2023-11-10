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

    val iconResPath: String = when(pullRequestStateWithDraft) {
        PullRequestStateWithDraft.OPEN -> OctoiconsRes.GitPullRequest
        PullRequestStateWithDraft.CLOSED -> OctoiconsRes.GitPullRequestClosed
        PullRequestStateWithDraft.MERGED -> OctoiconsRes.GitMerge
        PullRequestStateWithDraft.DRAFT -> OctoiconsRes.GitPullRequestDraft
        PullRequestStateWithDraft.UNKNOWN -> OctoiconsRes.Question
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