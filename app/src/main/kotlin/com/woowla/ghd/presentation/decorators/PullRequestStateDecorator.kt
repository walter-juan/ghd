package com.woowla.ghd.presentation.decorators

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.woowla.compose.tabler.OutlineGitMerge
import com.woowla.compose.tabler.OutlineGitPullRequest
import com.woowla.compose.tabler.OutlineGitPullRequestClosed
import com.woowla.compose.tabler.OutlineGitPullRequestDraft
import com.woowla.compose.tabler.OutlineQuestionMark
import com.woowla.compose.tabler.TablerIconsRes
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
        PullRequestStateWithDraft.OPEN -> TablerIconsRes.OutlineGitPullRequest
        PullRequestStateWithDraft.CLOSED -> TablerIconsRes.OutlineGitPullRequestClosed
        PullRequestStateWithDraft.MERGED -> TablerIconsRes.OutlineGitMerge
        PullRequestStateWithDraft.DRAFT -> TablerIconsRes.OutlineGitPullRequestDraft
        PullRequestStateWithDraft.UNKNOWN -> TablerIconsRes.OutlineQuestionMark
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