package com.woowla.ghd.presentation.screens

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import com.woowla.compose.icon.collections.tabler.Tabler
import com.woowla.compose.icon.collections.tabler.tabler.Filled
import com.woowla.compose.icon.collections.tabler.tabler.Outline
import com.woowla.compose.icon.collections.tabler.tabler.filled.Bell
import com.woowla.compose.icon.collections.tabler.tabler.outline.BrandGithub
import com.woowla.compose.icon.collections.tabler.tabler.outline.DeviceFloppy
import com.woowla.compose.icon.collections.tabler.tabler.outline.Filter
import com.woowla.compose.icon.collections.tabler.tabler.outline.Refresh
import com.woowla.compose.icon.collections.tabler.tabler.outline.RefreshOff
import com.woowla.ghd.presentation.app.AppDimens
import com.woowla.ghd.presentation.app.i18n
import com.woowla.ghd.presentation.components.*
import com.woowla.ghd.presentation.components.Section
import com.woowla.ghd.presentation.components.SectionItem
import com.woowla.ghd.presentation.components.SectionItemWithSwitch
import com.woowla.ghd.presentation.viewmodels.RepoToCheckEditStateMachine.St
import com.woowla.ghd.presentation.viewmodels.RepoToCheckEditStateMachine.Act
import com.woowla.ghd.presentation.viewmodels.RepoToCheckEditViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

object RepoToCheckEditScreen {
    @Composable
    fun Content(
        repoToCheckId: Long?,
        viewModel : RepoToCheckEditViewModel = koinViewModel { parametersOf(repoToCheckId) },
        onBackClick: () -> Unit
    ) {
        val state by viewModel.state.collectAsState()
        Screen(
            state = state,
            dispatchAction = viewModel::dispatch,
            onBackClick = onBackClick,
        )
    }

    @Composable
    fun Screen(
        state: St?,
        dispatchAction: (Act) -> Unit,
        onBackClick: () -> Unit
    ) {
        when(state) {
            null, St.Loading -> {
                Loading(onBackClick = onBackClick)
            }
            is St.Error -> {
                Text(i18n.generic_error)
            }
            is St.Success -> {
                Success(
                    state = state,
                    dispatchAction = dispatchAction,
                    onBackClick = onBackClick
                )
            }
        }
    }

    @Composable
    private fun Loading(onBackClick: () -> Unit) {
        ScreenScrollable(
            topBar = {
                TopBar(
                    title = i18n.top_bar_title_repos_to_check_edit,
                    subtitle = i18n.status_bar_loading,
                    navOnClick = onBackClick,
                )
            }
        ) { }
    }

    @Composable
    private fun Success(
        state: St.Success,
        dispatchAction: (Act) -> Unit,
        onBackClick: () -> Unit
    ) {
        var owner by remember { mutableStateOf(state.repoToCheck.owner) }
        var name by remember { mutableStateOf(state.repoToCheck.name) }
        var groupName by remember { mutableStateOf(state.repoToCheck.groupName ?: "") }
        var branchRegex by remember { mutableStateOf(state.repoToCheck.pullBranchRegex ?: "") }
        var arePullRequestsEnabled by remember { mutableStateOf(state.repoToCheck.arePullRequestsEnabled) }
        var arePullRequestsNotificationsEnabled by remember { mutableStateOf(state.repoToCheck.arePullRequestsNotificationsEnabled) }
        var areReleasesEnabled by remember { mutableStateOf(state.repoToCheck.areReleasesEnabled) }
        var areReleasesNotificationsEnabled by remember { mutableStateOf(state.repoToCheck.areReleasesNotificationsEnabled) }

        val textFieldFocusRequester = remember { FocusRequester() }

        if (state.savedSuccessfully == true) {
            onBackClick.invoke()
        }

        ScreenScrollable(
            topBar = {
                TopBar(
                    title = i18n.top_bar_title_repos_to_check_edit,
                    navOnClick = onBackClick,
                    actions = {
                        OutlinedIconButton(
                            colors = IconButtonDefaults.outlinedIconButtonColors(containerColor = MaterialTheme.colorScheme.primary),
                            onClick = {
                                dispatchAction.invoke(
                                    Act.Save(
                                        owner = owner,
                                        name = name,
                                        groupName = groupName,
                                        branchRegex = branchRegex,
                                        arePullRequestsEnabled = arePullRequestsEnabled,
                                        arePullRequestsNotificationsEnabled = arePullRequestsNotificationsEnabled,
                                        areReleasesEnabled = areReleasesEnabled,
                                        areReleasesNotificationsEnabled = areReleasesNotificationsEnabled,
                                    )
                                )
                            }
                        ) {
                            Icon(
                                Tabler.Outline.DeviceFloppy,
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
                RepositorySection(
                    owner = owner,
                    name = name,
                    groupName = groupName,
                    focusRequester = textFieldFocusRequester,
                    onOwnerChange = { owner = it },
                    onNameChange = { name = it },
                    onReleaseGroupChange = { groupName = it },
                )

                PullRequestSection(
                    arePullRequestsEnabled = arePullRequestsEnabled,
                    arePullRequestsNotificationsEnabled = arePullRequestsNotificationsEnabled,
                    branchRegex = branchRegex,
                    onArePullRequestsEnabledChange = { arePullRequestsEnabled = it },
                    onArePullRequestsNotificationsEnabledChange = { arePullRequestsNotificationsEnabled = it },
                    onBranchRegexChange = { branchRegex = it },
                )

                ReleaseSection(
                    areReleasesEnabled = areReleasesEnabled,
                    areReleasesNotificationsEnabled = areReleasesNotificationsEnabled,
                    onAreReleasesEnabledChange = { areReleasesEnabled = it },
                    onAreReleasesNotificationsEnabledChange = { areReleasesNotificationsEnabled = it },
                )
            }
        }

        LaunchedEffect("request-focus") {
            // request focus after first composition to avoid FocusRequester not initialized crash
            textFieldFocusRequester.requestFocus()
        }
    }

    @Composable
    private fun RepositorySection(
        owner: String,
        name: String,
        groupName: String,
        focusRequester: FocusRequester,
        onOwnerChange: (String) -> Unit,
        onNameChange: (String) -> Unit,
        onReleaseGroupChange: (String) -> Unit,
    ) {
        Section(title = i18n.screen_edit_repo_to_check_repository_section) {
            SectionItem(
                title = "GitHub repository owner/name",
                leadingIcon = {
                    Icon(
                        imageVector = Tabler.Outline.BrandGithub,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    OutlinedTextField(
                        value = owner,
                        onValueChange = onOwnerChange,
                        label = { Text(text = i18n.screen_edit_repo_to_check_owner_label) },
                        singleLine = true,
                        modifier = Modifier
                            .focusRequester(focusRequester)
                            .weight(1f),
                    )
                    OutlinedTextField(
                        value = name,
                        onValueChange = onNameChange,
                        label = { Text(text = i18n.screen_edit_repo_to_check_name_label) },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
            SectionItem(
                title = i18n.screen_edit_repo_to_check_group_item,
                description = i18n.screen_edit_repo_to_check_group_item_description,
            ) {
                OutlinedTextField(
                    value = groupName,
                    onValueChange = onReleaseGroupChange,
                    label = { Text(text = i18n.screen_edit_repo_to_check_group_name_label) },
                    singleLine = true,
                )
            }
        }
    }

    @Composable
    private fun PullRequestSection(
        arePullRequestsEnabled: Boolean,
        arePullRequestsNotificationsEnabled: Boolean,
        branchRegex: String,
        onArePullRequestsEnabledChange: (Boolean) -> Unit,
        onArePullRequestsNotificationsEnabledChange: (Boolean) -> Unit,
        onBranchRegexChange: (String) -> Unit,
    ) {
        Section(title = "Pull requests") {
            SectionItemWithSwitch(
                title = i18n.screen_edit_repo_to_check_pull_request_section,
                checked = arePullRequestsEnabled,
                leadingIcon = {
                    SynchAndNotificationsIcon(
                        syncEnabled = arePullRequestsEnabled,
                        notificationsEnabled = arePullRequestsNotificationsEnabled,
                    )
                },
                onCheckedChange = {
                    onArePullRequestsEnabledChange.invoke(it)
                    onArePullRequestsNotificationsEnabledChange.invoke(false)
                }
            )
            SectionItemWithSwitch(
                title = "Enable notifications",
                checked = arePullRequestsNotificationsEnabled,
                onCheckedChange = onArePullRequestsNotificationsEnabledChange,
                enabled = arePullRequestsEnabled,
            )
            SectionItem(
                title = i18n.screen_edit_repo_to_check_filter_by_branch_item,
                description = i18n.screen_edit_repo_to_check_filter_by_branch_item_description,
                leadingIcon = {
                    Icon(
                        imageVector = Tabler.Outline.Filter,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                enabled = arePullRequestsEnabled,
            ) {
                OutlinedTextField(
                    value = branchRegex,
                    onValueChange = onBranchRegexChange,
                    label = { Text(text = i18n.screen_edit_repo_to_check_href_branch_regex_label) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = arePullRequestsEnabled,
                    singleLine = true
                )
            }
        }
    }

    @Composable
    private fun ReleaseSection(
        areReleasesEnabled: Boolean,
        areReleasesNotificationsEnabled: Boolean,
        onAreReleasesEnabledChange: (Boolean) -> Unit,
        onAreReleasesNotificationsEnabledChange: (Boolean) -> Unit,
    ) {
        Section(title = "Releases") {
            SectionItemWithSwitch(
                title = i18n.screen_edit_repo_to_check_releaes_section,
                checked = areReleasesEnabled,
                leadingIcon = {
                    SynchAndNotificationsIcon(
                        syncEnabled = areReleasesEnabled,
                        notificationsEnabled = areReleasesNotificationsEnabled,
                    )
                },
                onCheckedChange = {
                    onAreReleasesEnabledChange.invoke(it)
                    onAreReleasesNotificationsEnabledChange.invoke(false)
                },
            )
            SectionItemWithSwitch(
                title = "Enable notifications",
                checked = areReleasesNotificationsEnabled,
                onCheckedChange = onAreReleasesNotificationsEnabledChange,
                enabled = areReleasesEnabled,
            )
        }
    }

    @Composable
    private fun SynchAndNotificationsIcon(
        syncEnabled: Boolean,
        notificationsEnabled: Boolean
    ) {
        val icon = when {
            syncEnabled && notificationsEnabled -> Tabler.Filled.Bell
            syncEnabled -> Tabler.Outline.Refresh
            else -> Tabler.Outline.RefreshOff
        }
        Crossfade(icon) {
            when(icon) {
                Tabler.Filled.Bell -> {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Tabler.Outline.Refresh -> {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Tabler.Outline.RefreshOff -> {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}