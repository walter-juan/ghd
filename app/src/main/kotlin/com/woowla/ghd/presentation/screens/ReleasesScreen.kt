package com.woowla.ghd.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.woowla.ghd.domain.entities.ReleaseWithRepo
import com.woowla.ghd.domain.entities.SyncResult
import com.woowla.ghd.presentation.app.AppDimens
import com.woowla.ghd.presentation.app.i18n
import com.woowla.ghd.presentation.components.*
import com.woowla.ghd.presentation.decorators.SyncResultDecorator
import com.woowla.ghd.presentation.viewmodels.ReleasesViewModel
import com.woowla.ghd.presentation.viewmodels.ReleasesStateMachine
import org.koin.compose.viewmodel.koinViewModel

object ReleasesScreen {
    @Composable
    fun Content(
        viewModel : ReleasesViewModel = koinViewModel(),
        onSyncResultEntriesClick: (SyncResult) -> Unit
    ) {
        val state = viewModel.state.collectAsState().value

        ScreenScrollable(
            topBar = {
                TopBar(
                    title = i18n.top_bar_title_releases,
                    subtitle = {
                        TopBarSubtitle(
                            state = state,
                            onSyncResultEntriesClick = onSyncResultEntriesClick
                        )
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
                    null, ReleasesStateMachine.St.Initializing -> {
                        // do not show a loading because is shown only some milliseconds
                    }
                    is ReleasesStateMachine.St.Error -> {
                        ErrorComponent()
                    }
                    is ReleasesStateMachine.St.Success -> {
                        if (state.groupNameFilters.isNotEmpty()) {
                            ReleaseFilters(
                                groupNameFilters = state.groupNameFilters,
                                groupNameFilterSizes = state.groupNameFilterSizes,
                                groupNameFiltersSelected = state.groupNameFiltersSelected,
                                onGroupNameChanged = { isSelected, groupName ->
                                    viewModel.dispatch(ReleasesStateMachine.Act.GroupNameFilterSelected(
                                        isSelected = isSelected,
                                        groupName = groupName,
                                    ))
                                }
                            )
                        }

                        if (state.releasesFiltered.isEmpty()) {
                            EmptyComponent()
                        } else {
                            ListReleases(state.releasesFiltered)
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun TopBarSubtitle(state: ReleasesStateMachine.St?, onSyncResultEntriesClick: (SyncResult) -> Unit) {
        if (state is ReleasesStateMachine.St.Success && state.syncResultWithEntries != null) {
            SyncResultDecorator(state.syncResultWithEntries).TitleWithDate(
                onClick = { onSyncResultEntriesClick.invoke(state.syncResultWithEntries.syncResult) }
            )
        }
    }

    @Composable
    private fun ReleaseFilters(
        groupNameFilters: Set<String>,
        groupNameFilterSizes: Map<String, Int>,
        groupNameFiltersSelected: Set<String>,
        onGroupNameChanged: (isSelected: Boolean, groupName: String) -> Unit,
    ) {
        Header {
            groupNameFilters.forEach { groupName ->
                val count = groupNameFilterSizes[groupName] ?: 0
                val isSelected = groupNameFiltersSelected.contains(groupName)
                ColoredFilterChip(
                    text = "$groupName ($count)",
                    selected = isSelected,
                    icon = null,
                    onClick = {
                        onGroupNameChanged.invoke(isSelected, groupName)
                    },
                )
            }
        }
    }

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    private fun ListReleases(releases: List<ReleaseWithRepo>) {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(space = AppDimens.cardHorizontalSpaceBetween, alignment = Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(AppDimens.cardVerticalSpaceBetween),
            maxItemsInEachRow = 2
        ) {
            releases.forEach { release ->
                ReleaseCard(
                    releaseWithRepo = release,
                    modifier = Modifier.sizeIn(maxWidth = AppDimens.cardMaxWidth).fillMaxWidth(),
                )
            }
        }
    }
}
