package com.woowla.ghd.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.woowla.ghd.domain.entities.SyncResult
import com.woowla.ghd.presentation.app.AppDimens
import com.woowla.ghd.presentation.app.i18n
import com.woowla.ghd.presentation.components.*
import com.woowla.ghd.presentation.decorators.PullRequestStateDecorator
import com.woowla.ghd.presentation.decorators.SyncResultDecorator
import com.woowla.ghd.presentation.viewmodels.PullRequestsViewModel

object PullRequestsScreen {
    @Composable
    fun Content(onSyncResultEntriesClick: (syncResult: SyncResult) -> Unit) {
        val viewModel = viewModel { PullRequestsViewModel() }
        val state = viewModel.state.collectAsState().value
        val topBarSubtitle = when(state) {
            is PullRequestsViewModel.State.Initializing -> i18n.status_bar_loading
            is PullRequestsViewModel.State.Success -> state.syncResultWithEntities?.let { SyncResultDecorator(it) }?.title ?: i18n.status_bar_synchronized_at_unknown
            is PullRequestsViewModel.State.Error -> i18n.status_bar_error
        }
        val topBarSubtitleOnClick: (() -> Unit)? = when(state) {
            is PullRequestsViewModel.State.Initializing -> null
            is PullRequestsViewModel.State.Success -> {
                state.syncResultWithEntities?.let { { onSyncResultEntriesClick.invoke(it.syncResult) } }
            }
            is PullRequestsViewModel.State.Error -> null
        }

        ScreenScrollable(
            topBar = {
                TopBar(
                    title = i18n.top_bar_title_pull_requests,
                    subtitle = topBarSubtitle,
                    subtitleOnClick = topBarSubtitleOnClick
                )
            },
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .padding(AppDimens.contentPaddingAllDp)
                    .width(AppDimens.contentWidthDp)
            ) {
                when(state) {
                    is PullRequestsViewModel.State.Initializing -> { }
                    is PullRequestsViewModel.State.Error -> {
                        Text(i18n.generic_error)
                    }
                    is PullRequestsViewModel.State.Success -> {
                        state.groupedPullRequests.forEach { groupedPullRequests ->
                            val pullRequestDecorator = PullRequestStateDecorator(groupedPullRequests.pullRequestStateWithDraft)
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.Start),
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.width(AppDimens.contentWidthDp)
                            ) {
                                Icon(
                                    painter = painterResource(pullRequestDecorator.iconResPath),
                                    contentDescription = null,
                                    modifier = Modifier.size(15.dp),
                                    tint = pullRequestDecorator.iconTint()
                                )
                                Text(
                                    text = pullRequestDecorator.text,
                                    style = MaterialTheme.typography.titleMedium,
                                )
                            }
                            groupedPullRequests.pullRequestsWithReviews.forEach { pullRequestWithReviews ->
                                PullRequestCard(
                                    pullRequestWithReviews = pullRequestWithReviews,
                                    onSeenClick = { viewModel.markAsSeen(pullRequestWithReviews.pullRequest) }
                                )
                            }
                            Divider(modifier = Modifier.padding(vertical = 10.dp).width(AppDimens.contentWidthDp))
                        }
                    }
                }
            }
        }
    }
}