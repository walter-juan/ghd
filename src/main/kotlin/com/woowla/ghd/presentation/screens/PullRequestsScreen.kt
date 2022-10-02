package com.woowla.ghd.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.woowla.ghd.domain.entities.PullRequest
import com.woowla.ghd.domain.entities.PullRequestState
import com.woowla.ghd.presentation.app.i18n
import com.woowla.ghd.presentation.components.PullRequestCard
import com.woowla.ghd.presentation.components.Screen
import com.woowla.ghd.presentation.components.TopBar
import com.woowla.ghd.presentation.decorators.PullRequestDecorator
import com.woowla.ghd.presentation.decorators.PullRequestStateDecorator
import com.woowla.ghd.presentation.viewmodels.PullRequestsViewModel

@Composable
fun PullRequestsScreen(viewModel: PullRequestsViewModel = PullRequestsViewModel()) {
    val pullsState by viewModel.state.collectAsState()
    val lockedState = pullsState
    val statusBarText = when(lockedState) {
        is PullRequestsViewModel.State.Error -> i18n.status_bar_error
        is PullRequestsViewModel.State.Loading -> i18n.status_bar_loading
        is PullRequestsViewModel.State.Success -> i18n.status_bar_synchronized_at(lockedState.synchronizedAt)
    }

    val lazyListState = rememberLazyListState()
    Screen(
        topBar = { TopBar(title = i18n.top_bar_title_pull_requests) },
        statusBarText = statusBarText,
        lazyListState = lazyListState,
    ) {

        when(lockedState) {
            is PullRequestsViewModel.State.Error -> item { Text(i18n.generic_error) }
            is PullRequestsViewModel.State.Loading -> {
                lockedState.pulls.forEach { (pullRequestState, pullRequests) ->
                    PullStatusWithPulls(viewModel, pullRequestState, pullRequests)
                }
            }
            is PullRequestsViewModel.State.Success -> {
                lockedState.pulls.forEach { (pullRequestState, pullRequests) ->
                    PullStatusWithPulls(viewModel, pullRequestState, pullRequests)
                }
            }
        }
    }
}

private fun LazyListScope.PullStatusWithPulls(viewModel: PullRequestsViewModel, pullRequestState: PullRequestState, pullRequests: List<PullRequest>) {
    val pullRequestDecorator = PullRequestStateDecorator(pullRequestState)
    item {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.Start),
            verticalAlignment = Alignment.CenterVertically
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
    }
    items(pullRequests) { pullRequest ->
        val pullRequestDecorator = PullRequestDecorator(pullRequest)

        PullRequestCard(
            text = pullRequestDecorator.fullRepo,
            overlineText = pullRequestDecorator.updatedAt,
            secondaryText = pullRequestDecorator.authorWithTitle,
            categoryColor = pullRequestDecorator.state.iconTint,
            linkUrl = pullRequest.url,
            imageUrl = pullRequest.authorAvatarUrl,
            seen = pullRequest.appSeen,
            onTickClick = { viewModel.markAsSeen(pullRequest) }
        )
    }
    item {
        Divider(modifier = Modifier.padding(vertical = 10.dp))
    }
}