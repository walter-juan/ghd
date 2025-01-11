package com.woowla.ghd.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.woowla.ghd.domain.entities.PullRequestStateExtended
import com.woowla.ghd.domain.entities.PullRequestWithRepoAndReviews
import com.woowla.ghd.domain.entities.SyncResult
import com.woowla.ghd.presentation.app.AppDimens
import com.woowla.ghd.presentation.app.i18n
import com.woowla.ghd.presentation.components.*
import com.woowla.ghd.presentation.decorators.PullRequestStateDecorator
import com.woowla.ghd.presentation.decorators.SyncResultDecorator
import com.woowla.ghd.presentation.viewmodels.PullRequestsStateMachine
import com.woowla.ghd.presentation.viewmodels.PullRequestsViewModel

object PullRequestsScreen {
    @Composable
    fun Content(onSyncResultEntriesClick: (syncResult: SyncResult) -> Unit) {
        val viewModel = viewModel { PullRequestsViewModel() }
        val state = viewModel.state.collectAsState().value

        ScreenScrollable(
            topBar = {
                TopBar(
                    title = i18n.top_bar_title_pull_requests,
                    subtitle = {
                        TopBarSubtitle(state = state, onSyncResultEntriesClick = onSyncResultEntriesClick)
                    },
                )
            },
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .padding(AppDimens.screenPadding)
                    .fillMaxWidth()
            ) {
                when(state) {
                    null, is PullRequestsStateMachine.St.Initializing -> {
                        LoadingComponent()
                    }
                    is PullRequestsStateMachine.St.Error -> {
                        ErrorComponent()
                    }
                    is PullRequestsStateMachine.St.Success -> {
                        PullRequestFilters(
                            stateExtendedFilterSizes = state.stateExtendedFilterSizes,
                            stateExtendedFilters = state.stateExtendedFilters,
                            stateExtendedFiltersSelected = state.stateExtendedFiltersSelected,
                            onStateChanged = { isSelected, pullRequestState ->
                                viewModel.dispatch(PullRequestsStateMachine.Act.StateExtendedFilterSelected(
                                    isSelected = isSelected,
                                    pullRequestStateExtended = pullRequestState
                                ))
                            }
                        )

                        if (state.pullRequestsFiltered.isEmpty()) {
                            EmptyComponent()
                        } else {
                            ListPullRequests(
                                pullRequests = state.pullRequestsFiltered
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun TopBarSubtitle(state: PullRequestsStateMachine.St?, onSyncResultEntriesClick: (SyncResult) -> Unit) {
        if (state is PullRequestsStateMachine.St.Success && state.syncResultWithEntries != null) {
            SyncResultDecorator(state.syncResultWithEntries).TitleWithDate(
                onClick = { onSyncResultEntriesClick.invoke(state.syncResultWithEntries.syncResult) }
            )
        }
    }

    @Composable
    private fun PullRequestFilters(
        stateExtendedFilters: Set<PullRequestStateExtended>,
        stateExtendedFilterSizes: Map<PullRequestStateExtended, Int>,
        stateExtendedFiltersSelected: Set<PullRequestStateExtended>,
        onStateChanged: (isSelected: Boolean, pullRequestState: PullRequestStateExtended) -> Unit,
    ) {
        Header {
            stateExtendedFilters.forEach { stateExtended ->
                val decorator = PullRequestStateDecorator(stateExtended)
                val count = stateExtendedFilterSizes[stateExtended] ?: 0
                val isSelected = stateExtendedFiltersSelected.contains(stateExtended)
                ColoredFilterChip(
                    text = "${decorator.text} ($count)",
                    color = decorator.iconTint(),
                    selected = isSelected,
                    icon = decorator.icon,
                    onClick = {
                        onStateChanged.invoke(isSelected, stateExtended)
                    },
                )
            }
        }
    }

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    private fun ListPullRequests(pullRequests: List<PullRequestWithRepoAndReviews>) {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(space = AppDimens.cardHorizontalSpaceBetween, alignment = Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(AppDimens.cardVerticalSpaceBetween),
            maxItemsInEachRow = 2
        ) {
            pullRequests.forEach { pullRequest ->
                PullRequestCard(
                    pullRequestWithReviews = pullRequest,
                    modifier = Modifier.sizeIn(maxWidth = AppDimens.cardMaxWidth).fillMaxWidth(),
                )
            }
        }
    }
}
