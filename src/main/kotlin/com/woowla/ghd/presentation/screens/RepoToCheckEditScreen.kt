package com.woowla.ghd.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.woowla.ghd.presentation.components.Screen
import com.woowla.ghd.presentation.components.SectionCategory
import com.woowla.ghd.presentation.components.SectionItem
import com.woowla.ghd.presentation.components.TopBar
import com.woowla.ghd.presentation.app.i18n
import com.woowla.ghd.presentation.components.SwitchText
import com.woowla.ghd.presentation.viewmodels.RepoToCheckEditViewModel

@Composable
fun RepoToCheckEditScreen(
    viewModel: RepoToCheckEditViewModel,
    onBackClick: (() -> Unit)
) {
    val updateRequestState = viewModel.updateRequest.collectAsState()

    var owner by remember { mutableStateOf(updateRequestState.value.owner) }
    var name by remember { mutableStateOf(updateRequestState.value.name) }
    var releaseGroup by remember { mutableStateOf(updateRequestState.value.groupName ?: "") }
    var branchRegex by remember { mutableStateOf(updateRequestState.value.pullBranchRegex ?: "") }
    var enablePullNotifications by remember { mutableStateOf(updateRequestState.value.pullNotificationsEnabled) }
    var enableReleaseNotifications by remember { mutableStateOf(updateRequestState.value.releaseNotificationsEnabled) }

    LaunchedEffect(key1 = Unit) {
        viewModel.events.collect { event ->
            when (event) {
                RepoToCheckEditViewModel.Events.Saved -> {
                    onBackClick.invoke()
                }
            }
        }
    }

    Screen(
        topBar = {
            TopBar(
                title = i18n.top_bar_title_repos_to_check_edit,
                navOnClick = onBackClick,
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.saveRepo()
                        }
                    ) {
                        Icon(Icons.Filled.Save, contentDescription = i18n.screen_edit_repo_to_check_save)
                    }
                }
            )
        }
    ) {
        item {
            SectionCategory(i18n.screen_edit_repo_to_check_repository_section) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    OutlinedTextField(
                        value = owner,
                        onValueChange = {
                            owner = it
                            viewModel.ownerUpdated(it)
                        },
                        label = { Text(text = i18n.screen_edit_repo_to_check_owner_label) },
                        modifier = Modifier.weight(1f),
                    )
                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            viewModel.nameUpdated(it)
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
                            viewModel.groupUpdated(it)
                        },
                        label = { Text(text = i18n.screen_edit_repo_to_check_group_name_label) },
                    )
                }
            }

            SectionCategory(i18n.screen_edit_repo_to_check_pull_request_section) {
                SwitchText(
                    text = i18n.screen_edit_repo_to_check_enable_notifications_item,
                    checked = enablePullNotifications,
                    onCheckedChange = {
                        enablePullNotifications = it
                        viewModel.pullNotificationsEnabledUpdated(it)
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
                            viewModel.branchRegexUpdated(it)
                        },
                        label = { Text(text = i18n.screen_edit_repo_to_check_href_branch_regex_label) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }

            SectionCategory(i18n.screen_edit_repo_to_check_releaes_section) {
                SwitchText(
                    text = i18n.screen_edit_repo_to_check_enable_notifications_item,
                    checked = enableReleaseNotifications,
                    onCheckedChange = {
                        enableReleaseNotifications = it
                        viewModel.releaseNotificationsEnabledUpdated(it)
                    },
                )
            }
        }
    }
}