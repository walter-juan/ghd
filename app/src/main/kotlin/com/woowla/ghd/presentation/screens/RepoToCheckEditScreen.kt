package com.woowla.ghd.presentation.screens

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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.woowla.compose.icon.collections.tabler.Tabler
import com.woowla.compose.icon.collections.tabler.tabler.Outline
import com.woowla.compose.icon.collections.tabler.tabler.outline.DeviceFloppy
import com.woowla.ghd.domain.entities.RepoToCheck
import com.woowla.ghd.presentation.app.AppDimens
import com.woowla.ghd.presentation.app.i18n
import com.woowla.ghd.presentation.components.*
import com.woowla.ghd.presentation.viewmodels.RepoToCheckEditViewModel

object RepoToCheckEditScreen {
    @Composable
    fun Content(
        repoToCheckId: Long?,
        onBackClick: () -> Unit
    ) {
        val viewModel = viewModel { RepoToCheckEditViewModel(repoToCheckId = repoToCheckId) }
        val state = viewModel.state.collectAsState().value

        LaunchedEffect(key1 = Unit) {
            viewModel.events.collect { event ->
                when (event) {
                    RepoToCheckEditViewModel.Events.Saved -> {
                        onBackClick.invoke()
                    }
                }
            }
        }

        when(state) {
            RepoToCheckEditViewModel.State.Initializing -> {
                Loading(onBackClick = onBackClick)
            }
            is RepoToCheckEditViewModel.State.Error -> {
                Text(i18n.generic_error)
            }
            is RepoToCheckEditViewModel.State.Success -> {
                Success(
                    repoToCheck = state.repoToCheck,
                    viewModel = viewModel,
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
        repoToCheck: RepoToCheck,
        viewModel: RepoToCheckEditViewModel,
        onBackClick: () -> Unit
    ) {
        var owner by remember { mutableStateOf(repoToCheck.owner) }
        var name by remember { mutableStateOf(repoToCheck.name) }
        var releaseGroup by remember { mutableStateOf(repoToCheck.groupName ?: "") }
        var branchRegex by remember { mutableStateOf(repoToCheck.pullBranchRegex ?: "") }
        var arePullRequestsEnabled by remember { mutableStateOf(repoToCheck.arePullRequestsEnabled) }
        var areReleasesEnabled by remember { mutableStateOf(repoToCheck.areReleasesEnabled) }

        ScreenScrollable(
            topBar = {
                TopBar(
                    title = i18n.top_bar_title_repos_to_check_edit,
                    navOnClick = onBackClick,
                    actions = {
                        OutlinedIconButton(
                            colors = IconButtonDefaults.outlinedIconButtonColors(containerColor = MaterialTheme.colorScheme.primary),
                            onClick = {
                                viewModel.saveRepo(
                                    owner = owner,
                                    name = name,
                                    groupName = releaseGroup,
                                    branchRegex = branchRegex,
                                    arePullRequestsEnabled = arePullRequestsEnabled,
                                    areReleasesEnabled = areReleasesEnabled,
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
                modifier = Modifier
                    .padding(AppDimens.contentPaddingAllDp)
                    .width(AppDimens.contentWidthDp)
            ) {
                SectionCategory(i18n.screen_edit_repo_to_check_repository_section) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                    ) {
                        OutlinedTextField(
                            value = owner,
                            onValueChange = {
                                owner = it
                            },
                            label = { Text(text = i18n.screen_edit_repo_to_check_owner_label) },
                            modifier = Modifier.weight(1f),
                        )
                        OutlinedTextField(
                            value = name,
                            onValueChange = {
                                name = it
                            },
                            label = { Text(text = i18n.screen_edit_repo_to_check_name_label) },
                            modifier = Modifier.weight(1f),
                        )
                    }

                    Spacer(modifier = Modifier.padding(5.dp))

                    SectionItem(
                        title = i18n.screen_edit_repo_to_check_group_item,
                        description = i18n.screen_edit_repo_to_check_group_item_description,
                    ) {
                        OutlinedTextField(
                            value = releaseGroup,
                            onValueChange = {
                                releaseGroup = it
                            },
                            label = { Text(text = i18n.screen_edit_repo_to_check_group_name_label) },
                        )
                    }
                }

                SectionCategory(i18n.screen_edit_repo_to_check_pull_request_section) {
                    SwitchText(
                        text = i18n.screen_edit_repo_to_check_enable_pull_requests_item,
                        checked = arePullRequestsEnabled,
                        onCheckedChange = {
                            arePullRequestsEnabled = it
                        },
                    )

                    SectionItem(
                        title = i18n.screen_edit_repo_to_check_filter_by_branch_item,
                        description = i18n.screen_edit_repo_to_check_filter_by_branch_item_description
                    ) {
                        OutlinedTextField(
                            value = branchRegex,
                            onValueChange = {
                                branchRegex = it
                            },
                            label = { Text(text = i18n.screen_edit_repo_to_check_href_branch_regex_label) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                }

                SectionCategory(i18n.screen_edit_repo_to_check_releaes_section) {
                    SwitchText(
                        text = i18n.screen_edit_repo_to_check_enable_releases_item,
                        checked = areReleasesEnabled,
                        onCheckedChange = {
                            areReleasesEnabled = it
                        },
                    )
                }
            }
        }
    }
}