package com.woowla.ghd.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import com.woowla.ghd.presentation.app.AppDimens
import com.woowla.ghd.presentation.app.i18n
import com.woowla.ghd.presentation.components.*
import com.woowla.ghd.presentation.decorators.PullRequestStateDecorator
import com.woowla.ghd.presentation.viewmodels.PullRequestsViewModel

class PullRequestsScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel = rememberScreenModel { PullRequestsViewModel() }
        val state = viewModel.state.collectAsState().value
        val statusBarText = when(state) {
            is PullRequestsViewModel.State.Initializing -> i18n.status_bar_loading
            is PullRequestsViewModel.State.Success -> i18n.status_bar_synchronized_at(state.synchronizedAt)
            is PullRequestsViewModel.State.Error -> i18n.status_bar_error
        }

        ScreenScrollable(
            topBar = { TopBar(title = i18n.top_bar_title_pull_requests) },
            statusBarText = statusBarText,
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .padding(AppDimens.contentPaddingAllDp.dp)
                    .width(AppDimens.contentWidthDp.dp)
            ) {
                when(state) {
                    is PullRequestsViewModel.State.Initializing -> { }
                    is PullRequestsViewModel.State.Error -> {
                        Text(i18n.generic_error)
                    }
                    is PullRequestsViewModel.State.Success -> {
                        state.groupedPullRequests.forEach { groupedPullRequests ->
                            val pullRequestDecorator = PullRequestStateDecorator(groupedPullRequests.pullRequestState)
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.Start),
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.width(AppDimens.contentWidthDp.dp)
                            ) {
                                Icon(
                                    painter = painterResource(pullRequestDecorator.iconResPath),
                                    contentDescription = null,
                                    modifier = Modifier.size(15.dp),
                                    tint = pullRequestDecorator.iconTint
                                )
                                Text(
                                    text = pullRequestDecorator.text,
                                    style = MaterialTheme.typography.subtitle1,
                                )
                            }
                            groupedPullRequests.pullRequests.forEach { pullRequest ->
                                PullRequestCard(
                                    pullRequest = pullRequest,
                                    onSeenClick = { viewModel.markAsSeen(pullRequest) }
                                )
                            }
                            Divider(modifier = Modifier.padding(vertical = 10.dp).width(AppDimens.contentWidthDp.dp))
                        }
                    }
                }
            }
        }
    }
}