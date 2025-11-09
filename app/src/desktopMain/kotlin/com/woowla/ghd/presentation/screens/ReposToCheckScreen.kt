package com.woowla.ghd.presentation.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.woowla.compose.icon.collections.tabler.Tabler
import com.woowla.compose.icon.collections.tabler.tabler.Outline
import com.woowla.compose.icon.collections.tabler.tabler.outline.CircleX
import com.woowla.compose.icon.collections.tabler.tabler.outline.Filter2
import com.woowla.compose.icon.collections.tabler.tabler.outline.Filter2Check
import com.woowla.compose.icon.collections.tabler.tabler.outline.Plus
import com.woowla.compose.icon.collections.tabler.tabler.outline.Search
import com.woowla.compose.icon.collections.tabler.tabler.outline.Square
import com.woowla.compose.icon.collections.tabler.tabler.outline.SquareCheck
import com.woowla.compose.icon.collections.tabler.tabler.outline.TableImport
import com.woowla.ghd.domain.entities.RepoToCheck
import com.woowla.ghd.presentation.i18nUi
import com.woowla.ghd.presentation.app.AppDimens
import com.woowla.ghd.presentation.components.ColoredFilterChip
import com.woowla.ghd.presentation.components.ConfirmationDialog
import com.woowla.ghd.presentation.components.EmptyComponent
import com.woowla.ghd.presentation.components.ErrorComponent
import com.woowla.ghd.presentation.components.Header
import com.woowla.ghd.presentation.components.RepoToCheckCard
import com.woowla.ghd.presentation.components.ScreenScrollable
import com.woowla.ghd.presentation.components.TopBar
import com.woowla.ghd.presentation.viewmodels.ReposToCheckStateMachine
import com.woowla.ghd.presentation.viewmodels.ReposToCheckViewModel
import com.woowla.ghd.core.utils.openWebpage
import com.woowla.ghd.domain.entities.RepoToCheckFilters

object ReposToCheckScreen {
    @Composable
    fun Content(
        viewModel: ReposToCheckViewModel,
        onEditRepoClick: (RepoToCheck) -> Unit,
        onAddNewRepoClick: () -> Unit,
        onBulkClick: () -> Unit,
    ) {
        val state = viewModel.state.collectAsState().value
        val showDeleteConfirmationDialog = remember { mutableStateOf(false) }
        var repoToCheckToDelete: RepoToCheck? = remember { null }
        val textFieldFocusRequester = remember { FocusRequester() }

        val topBarSubtitle = if (state is ReposToCheckStateMachine.St.Success) {
            i18nUi.screen_app_settings_repositories_item_description(state.reposToCheck.size)
        } else {
            null
        }

        ScreenScrollable(
            topBar = {
                TopBar(
                    title = i18nUi.top_bar_title_repos_to_check,
                    subtitle = topBarSubtitle,
                    actions = {
                        OutlinedIconButton(
                            colors = IconButtonDefaults.outlinedIconButtonColors(containerColor = MaterialTheme.colorScheme.primary),
                            onClick = onBulkClick
                        ) {
                            Icon(
                                Tabler.Outline.TableImport,
                                contentDescription = i18nUi.screen_repos_to_check_bulk_item,
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
                                contentDescription = i18nUi.screen_edit_repo_to_check_save,
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
                when (state) {
                    null, ReposToCheckStateMachine.St.Initializing -> {
                        // do not show a loading because is shown only some milliseconds
                    }
                    is ReposToCheckStateMachine.St.Error -> {
                        ErrorComponent()
                    }
                    is ReposToCheckStateMachine.St.Success -> {
                        if (state.allGroupNames.isNotEmpty()) {
                            ReposFilters(
                                searchQuery = state.searchQuery,
                                allGroupNames = state.allGroupNames,
                                allGroupNamesSizes = state.allGroupNamesSizes,
                                otherFilters = state.filtersRepoToCheck,
                                focusRequester = textFieldFocusRequester,
                                onSearchQueryChanged = { searchQuery ->
                                    viewModel.dispatch(ReposToCheckStateMachine.Act.SearchQueryChanged(searchQuery))
                                },
                                onGroupNameChanged = { isSelected, groupName ->
                                    viewModel.dispatch(
                                        ReposToCheckStateMachine.Act.GroupNameFilterChanged(
                                            isSelected = isSelected,
                                            groupName = groupName,
                                        )
                                    )
                                },
                                onOtherFiltersChanged = { otherFilters ->
                                    viewModel.dispatch(
                                        ReposToCheckStateMachine.Act.FiltersChanged(
                                            filters = otherFilters,
                                        )
                                    )
                                },
                            )

                            LaunchedEffect("request-focus") {
                                // request focus after first composition to avoid FocusRequester not initialized crash
                                textFieldFocusRequester.requestFocus()
                            }
                        }

                        if (state.reposToCheckFiltered.isEmpty()) {
                            EmptyComponent()
                        } else {
                            ListRepoToCheck(
                                repoToCheckList = state.reposToCheckFiltered,
                                onOpenClick = { repoToCheck ->
                                    repoToCheck.repository?.url?.let(::openWebpage)
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
    private fun CheckIcon(checked: Boolean) {
        if (checked) {
            Icon(
                imageVector = Tabler.Outline.SquareCheck,
                contentDescription = null,
            )
        } else {
            Icon(
                imageVector = Tabler.Outline.Square,
                contentDescription = null,
            )
        }

    }

    @Composable
    private fun ReposFilters(
        searchQuery: String,
        allGroupNames: Set<String>,
        allGroupNamesSizes: Map<String, Int>,
        otherFilters: RepoToCheckFilters,
        focusRequester: FocusRequester,
        onSearchQueryChanged: (String) -> Unit,
        onGroupNameChanged: (isSelected: Boolean, groupName: String) -> Unit,
        onOtherFiltersChanged: (RepoToCheckFilters) -> Unit,
    ) {
        Header(
            flowContent = {
                allGroupNames.forEach { groupName ->
                    val count = allGroupNamesSizes[groupName] ?: 0
                    val isSelected = otherFilters.groupNames.contains(groupName)
                    ColoredFilterChip(
                        text = "$groupName ($count)",
                        selected = isSelected,
                        icon = null,
                        onClick = {
                            onGroupNameChanged.invoke(isSelected, groupName)
                        },
                    )
                }
            },
            bottomRowContent = {
                Spacer(modifier = Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    var dropdownMenuExpanded by remember { mutableStateOf(false) }
                    val filterIcon = if (otherFilters.anyFilterActive()) { Tabler.Outline.Filter2Check } else { Tabler.Outline.Filter2 }
                    val filterTint = if (otherFilters.anyFilterActive()) { MaterialTheme.colorScheme.primary } else { LocalContentColor.current }
                    OutlinedButton(
                        onClick = { dropdownMenuExpanded = true },
                        content = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(filterIcon, contentDescription = null, tint = filterTint)
                                Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
                                Text("Other filters")
                            }
                        }
                    )
                    DropdownMenu(
                        expanded = dropdownMenuExpanded,
                        onDismissRequest = { dropdownMenuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Pull Requests", fontWeight = FontWeight.Bold) },
                            enabled = false,
                            onClick = { },
                        )
                        DropdownMenuItem(
                            leadingIcon = { CheckIcon(otherFilters.pullRequestSyncEnabled) } ,
                            text = { Text("Sync enabled") },
                            onClick = {
                                onOtherFiltersChanged.invoke(otherFilters.copy(pullRequestSyncEnabled = !otherFilters.pullRequestSyncEnabled))
                                dropdownMenuExpanded = false
                            },
                        )
                        DropdownMenuItem(
                            leadingIcon = { CheckIcon(otherFilters.pullRequestNotificationsEnabled) } ,
                            text = { Text("Notifications enabled") },
                            onClick = {
                                onOtherFiltersChanged.invoke(otherFilters.copy(pullRequestNotificationsEnabled = !otherFilters.pullRequestNotificationsEnabled))
                                dropdownMenuExpanded = false
                            },
                        )
                        DropdownMenuItem(
                            leadingIcon = { CheckIcon(otherFilters.pullRequestBranchFilterActive) } ,
                            text = { Text("Branch filter active") },
                            onClick = {
                                onOtherFiltersChanged.invoke(otherFilters.copy(pullRequestBranchFilterActive = !otherFilters.pullRequestBranchFilterActive))
                                dropdownMenuExpanded = false
                            },
                        )
                        HorizontalDivider()
                        DropdownMenuItem(
                            text = { Text("Releases", fontWeight = FontWeight.Bold) },
                            enabled = false,
                            onClick = { },
                        )
                        DropdownMenuItem(
                            leadingIcon = { CheckIcon(otherFilters.releasesSyncEnabled) } ,
                            text = { Text("Sync enabled") },
                            onClick = {
                                onOtherFiltersChanged.invoke(otherFilters.copy(releasesSyncEnabled = !otherFilters.releasesSyncEnabled))
                                dropdownMenuExpanded = false
                            },
                        )
                        DropdownMenuItem(
                            leadingIcon = { CheckIcon(otherFilters.releasesNotificationsEnabled) } ,
                            text = { Text("Notifications enabled") },
                            onClick = {
                                onOtherFiltersChanged.invoke(otherFilters.copy(releasesNotificationsEnabled = !otherFilters.releasesNotificationsEnabled))
                                dropdownMenuExpanded = false
                            },
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChanged,
                        leadingIcon = {
                            AnimatedContent(
                                targetState = searchQuery.isBlank(),
                                transitionSpec = { fadeIn().togetherWith(fadeOut()) }
                            ) {
                                if (searchQuery.isBlank()) {
                                    Icon(
                                        Tabler.Outline.Search,
                                        contentDescription = null,
                                    )
                                } else {
                                    IconButton(onClick = { onSearchQueryChanged.invoke("") }) {
                                        Icon(imageVector = Tabler.Outline.CircleX, contentDescription = null, modifier = Modifier.size(25.dp))
                                    }
                                }
                            }
                        },
                        placeholder = { Text("Search by name or group") },
                        singleLine = true,
                        maxLines = 1,
                        modifier = Modifier.focusRequester(focusRequester).fillMaxWidth()
                    )
                }
            },
        )
    }
}
