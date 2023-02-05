package com.woowla.ghd.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ExperimentalMaterial3Api
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
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.woowla.ghd.domain.entities.RepoToCheck
import com.woowla.ghd.presentation.app.AppDimens
import com.woowla.ghd.presentation.app.i18n
import com.woowla.ghd.presentation.components.*
import com.woowla.ghd.presentation.viewmodels.RepoToCheckEditViewModel

data class RepoToCheckEditScreen(
    private val repoToCheck: RepoToCheck?,
) : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val viewModel = rememberScreenModel { RepoToCheckEditViewModel(repoToCheck = repoToCheck) }
        val navigator = LocalNavigator.currentOrThrow
        val onBackClick: (() -> Unit) = { navigator.pop() }

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

        ScreenScrollable(
            topBar = {
                TopBar(
                    title = i18n.top_bar_title_repos_to_check_edit,
                    navOnClick = onBackClick,
                    actions = {
                        OutlinedIconButton(
                            colors = IconButtonDefaults.outlinedIconButtonColors(containerColor = MaterialTheme.colorScheme.primary),
                            onClick = { viewModel.saveRepo() }
                        ) {
                            Icon(
                                Icons.Filled.Save,
                                contentDescription = i18n.screen_edit_repo_to_check_save,
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .padding(AppDimens.contentPaddingAllDp.dp)
                    .width(AppDimens.contentWidthDp.dp)
            ) {
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
}