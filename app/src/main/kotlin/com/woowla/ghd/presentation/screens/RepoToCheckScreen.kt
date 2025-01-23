package com.woowla.ghd.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.woowla.compose.icon.collections.tabler.Tabler
import com.woowla.compose.icon.collections.tabler.tabler.Outline
import com.woowla.compose.icon.collections.tabler.tabler.outline.Plus
import com.woowla.compose.icon.collections.tabler.tabler.outline.TableImport
import com.woowla.ghd.domain.entities.RepoToCheck
import com.woowla.ghd.presentation.app.AppDimens
import com.woowla.ghd.presentation.app.i18n
import com.woowla.ghd.presentation.components.*
import com.woowla.ghd.presentation.viewmodels.ReposToCheckViewModel
import com.woowla.ghd.presentation.viewmodels.ReposToCheckStateMachine

object RepoToCheckScreen {
    @Composable
    fun Content(
        onEditRepoClick: (RepoToCheck) -> Unit,
        onAddNewRepoClick: () -> Unit,
        onBulkClick: () -> Unit,
    ) {
        val viewModel = viewModel { ReposToCheckViewModel() }
        val state = viewModel.state.collectAsState().value

        val topBarSubtitle = if (state is ReposToCheckStateMachine.St.Success) {
            i18n.screen_app_settings_repositories_item_description(state.reposToCheck.size)
        } else {
            null
        }

        ScreenScrollable(
            topBar = {
                TopBar(
                    title = i18n.top_bar_title_repos_to_check,
                    subtitle = topBarSubtitle,
                    actions = {
                        OutlinedIconButton(
                            colors = IconButtonDefaults.outlinedIconButtonColors(containerColor = MaterialTheme.colorScheme.primary),
                            onClick = onBulkClick
                        ) {
                            Icon(
                                Tabler.Outline.TableImport,
                                contentDescription = i18n.screen_repos_to_check_bulk_item,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.padding(10.dp).fillMaxWidth()
                            )
                        }
                        OutlinedIconButton(
                            colors = IconButtonDefaults.outlinedIconButtonColors(containerColor = MaterialTheme.colorScheme.primary),
                            onClick = onAddNewRepoClick
                        ) {
                            Icon(
                                Tabler.Outline.Plus,
                                contentDescription = i18n.screen_edit_repo_to_check_save,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.padding(10.dp).fillMaxWidth()
                            )
                        }
                    }
                )
            }
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .padding(AppDimens.screenPadding)
                    .fillMaxWidth()
            ) {
                when(state) {
                    null, ReposToCheckStateMachine.St.Initializing -> {
                        LoadingComponent()
                    }
                    is ReposToCheckStateMachine.St.Error -> {
                        ErrorComponent()
                    }
                    is ReposToCheckStateMachine.St.Success -> {
                        if (state.groupNameFilters.isNotEmpty()) {
                            ReposFilters(
                                groupNameFilters = state.groupNameFilters,
                                groupNameFilterSizes = state.groupNameFilterSizes,
                                groupNameFiltersSelected = state.groupNameFiltersSelected,
                                onGroupNameChanged = { isSelected, groupName ->
                                    viewModel.dispatch(
                                        ReposToCheckStateMachine.Act.GroupNameFilterSelected(
                                            isSelected = isSelected,
                                            groupName = groupName,
                                        )
                                    )
                                }
                            )
                        }

                        if (state.reposToCheckFiltered.isEmpty()) {
                            EmptyComponent()
                        } else {
                            ListRepoToCheck(
                                repoToCheckList = state.reposToCheckFiltered,
                                onEditRepoClick = onEditRepoClick,
                                onDeleteRepoClick = { repoToCheck ->
                                    viewModel.dispatch(ReposToCheckStateMachine.Act.DeleteRepoToCheck(repoToCheck))
                                },
                            )
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    private fun ListRepoToCheck(
        repoToCheckList: List<RepoToCheck>,
        onEditRepoClick: (RepoToCheck) -> Unit,
        onDeleteRepoClick: (RepoToCheck) -> Unit,
    ) {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(space = AppDimens.cardHorizontalSpaceBetween, alignment = Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(AppDimens.cardVerticalSpaceBetween),
            maxItemsInEachRow = 2
        ) {
            repoToCheckList.forEach { repoToCheck ->
                RepoToCheckCard(
                    repoToCheck = repoToCheck,
                    onEditClick = onEditRepoClick,
                    onDeleteClick = onDeleteRepoClick,
                    modifier = Modifier.sizeIn(maxWidth = AppDimens.cardMaxWidth).fillMaxWidth(),
                )
            }
        }
    }

    @Composable
    private fun ReposFilters(
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
}
