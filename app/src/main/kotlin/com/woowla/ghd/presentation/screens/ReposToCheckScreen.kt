package com.woowla.ghd.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.woowla.compose.icon.collections.tabler.Tabler
import com.woowla.compose.icon.collections.tabler.tabler.Outline
import com.woowla.compose.icon.collections.tabler.tabler.outline.CircleX
import com.woowla.compose.icon.collections.tabler.tabler.outline.Plus
import com.woowla.compose.icon.collections.tabler.tabler.outline.Search
import com.woowla.compose.icon.collections.tabler.tabler.outline.TableImport
import com.woowla.ghd.domain.entities.RepoToCheck
import com.woowla.ghd.presentation.app.AppDimens
import com.woowla.ghd.presentation.app.i18n
import com.woowla.ghd.presentation.components.*
import com.woowla.ghd.presentation.viewmodels.ReposToCheckViewModel
import com.woowla.ghd.presentation.viewmodels.ReposToCheckStateMachine
import com.woowla.ghd.utils.openWebpage
import org.koin.compose.viewmodel.koinViewModel

object ReposToCheckScreen {
    @Composable
    fun Content(
        viewModel : ReposToCheckViewModel = koinViewModel(),
        onEditRepoClick: (RepoToCheck) -> Unit,
        onAddNewRepoClick: () -> Unit,
        onBulkClick: () -> Unit,
    ) {
        val state = viewModel.state.collectAsState().value
        val showDeleteConfirmationDialog = remember { mutableStateOf(false) }
        var repoToCheckToDelete: RepoToCheck? = remember { null }

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
                        // do not show a loading because is shown only some milliseconds
                    }
                    is ReposToCheckStateMachine.St.Error -> {
                        ErrorComponent()
                    }
                    is ReposToCheckStateMachine.St.Success -> {
                        if (state.groupNameFilters.isNotEmpty()) {
                            ReposFilters(
                                searchQuery = state.searchQuery,
                                groupNameFilters = state.groupNameFilters,
                                groupNameFilterSizes = state.groupNameFilterSizes,
                                groupNameFiltersSelected = state.groupNameFiltersSelected,
                                onSearchQueryChanged = { searchQuery ->
                                    viewModel.dispatch(ReposToCheckStateMachine.Act.SearchQueryChanged(searchQuery))
                                },
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
                                onOpenClick = { repoToCheck ->
                                    openWebpage(repoToCheck.url)
                                },
                                onEditRepoClick = onEditRepoClick,
                                onDeleteRepoClick = { repoToCheck ->
                                    repoToCheckToDelete = repoToCheck
                                    showDeleteConfirmationDialog.value = true
                                },
                            )
                        }

                        if (showDeleteConfirmationDialog.value) {
                            DeleteConfirmationDialog(
                                onConfirm = {
                                    viewModel.dispatch(ReposToCheckStateMachine.Act.DeleteRepoToCheck(repoToCheckToDelete!!))
                                    showDeleteConfirmationDialog.value = false
                                },
                                onDismiss = {
                                    showDeleteConfirmationDialog.value = false
                                }
                            )
                        }

                    }
                }
            }
        }
    }

    @Composable
    private fun DeleteConfirmationDialog(
        onConfirm: () -> Unit,
        onDismiss: () -> Unit,
    ) {
        ConfirmationDialog(
            title = "Delete repository",
            message = "Are you sure you want to delete this repository?",
            onConfirm = onConfirm,
            onDismiss = onDismiss,
        )
    }

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    private fun ListRepoToCheck(
        repoToCheckList: List<RepoToCheck>,
        onOpenClick: (RepoToCheck) -> Unit,
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
                    onOpenClick = onOpenClick,
                    onEditClick = onEditRepoClick,
                    onDeleteClick = onDeleteRepoClick,
                    modifier = Modifier.sizeIn(maxWidth = AppDimens.cardMaxWidth).fillMaxWidth(),
                )
            }
        }
    }

    @Composable
    private fun ReposFilters(
        searchQuery: String,
        groupNameFilters: Set<String>,
        groupNameFilterSizes: Map<String, Int>,
        groupNameFiltersSelected: Set<String>,
        onSearchQueryChanged: (String) -> Unit,
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
            Spacer(modifier = Modifier.width(10.dp))
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChanged,
                leadingIcon = {
                    Icon(
                        Tabler.Outline.Search,
                        contentDescription = null,
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { onSearchQueryChanged.invoke("") }) {
                        Icon(imageVector = Tabler.Outline.CircleX, contentDescription = null, modifier = Modifier.size(25.dp))
                    }
                },
                supportingText = { Text("Search by name or group") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
