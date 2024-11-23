package com.woowla.ghd.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.woowla.compose.icon.collections.tabler.Tabler
import com.woowla.compose.icon.collections.tabler.tabler.Outline
import com.woowla.compose.icon.collections.tabler.tabler.outline.DeviceFloppy
import com.woowla.compose.icon.collections.tabler.tabler.outline.Eye
import com.woowla.compose.icon.collections.tabler.tabler.outline.EyeOff
import com.woowla.ghd.domain.entities.SyncSettings
import com.woowla.ghd.presentation.app.AppDimens
import com.woowla.ghd.presentation.app.i18n
import com.woowla.ghd.presentation.components.*
import com.woowla.ghd.presentation.viewmodels.SettingsViewModel

object SettingsScreen {
    @Composable
    fun Content(
        onSyncResultsClicked: () -> Unit,
    ) {
        val viewModel = viewModel { SettingsViewModel() }
        val snackbarHostState = remember { SnackbarHostState() }

        val settingsState by viewModel.state.collectAsState()

        LaunchedEffect(snackbarHostState) {
            viewModel.events.collect { event->
                when (event) {
                    SettingsViewModel.Events.Saved -> {
                        snackbarHostState.showSnackbar(i18n.screen_app_settings_saved)
                    }
                }
            }
        }

        ScreenScrollable(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopBar(
                    title = i18n.top_bar_title_settings,
                    actions = {
                        OutlinedIconButton(
                            colors = IconButtonDefaults.outlinedIconButtonColors(containerColor = MaterialTheme.colorScheme.primary),
                            onClick = { viewModel.saveSettings() }
                        ) {
                            Icon(
                                Tabler.Outline.DeviceFloppy,
                                contentDescription = i18n.screen_app_settings_save,
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
                when(settingsState) {
                    SettingsViewModel.State.Initializing -> { }
                    is SettingsViewModel.State.Error -> {
                        Text(i18n.generic_error)
                    }
                    is SettingsViewModel.State.Success -> {
                        val successState = (settingsState as SettingsViewModel.State.Success)

                        var gitHubPatToken by remember { mutableStateOf(successState.syncSettings.githubPatToken) }
                        var passwordVisible by remember { mutableStateOf(false) }

                        var pullRequestFilterOptionsOpen by remember { mutableStateOf(successState.appSettings.pullRequestNotificationsFilterOptions.open) }
                        var pullRequestFilterOptionsClosed by remember { mutableStateOf(successState.appSettings.pullRequestNotificationsFilterOptions.closed) }
                        var pullRequestFilterOptionsMerged by remember { mutableStateOf(successState.appSettings.pullRequestNotificationsFilterOptions.merged) }
                        var pullRequestFilterOptionsDraft by remember { mutableStateOf(successState.appSettings.pullRequestNotificationsFilterOptions.draft) }

                        var pullRequestStateNotifications by remember { mutableStateOf(successState.appSettings.pullRequestStateNotificationsEnabled) }
                        var pullRequestActivityNotifications by remember { mutableStateOf(successState.appSettings.pullRequestActivityNotificationsEnabled) }

                        var newReleaseNotificationsEnabled by remember { mutableStateOf(successState.appSettings.newReleaseNotificationsEnabled) }
                        var updatedReleaseNotificationsEnabled by remember { mutableStateOf(successState.appSettings.updatedReleaseNotificationsEnabled) }

                        SectionCategory(i18n.screen_app_settings_synchronization_section) {
                            SectionItem(
                                title = i18n.screen_app_settings_github_token_item,
                                description = i18n.screen_app_settings_github_token_item_description
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)
                                        .padding(PaddingValues(bottom = 10.dp))
                                ) {
                                    OutlinedTextField(
                                        value = gitHubPatToken,
                                        onValueChange = {
                                            gitHubPatToken = it
                                            viewModel.patTokenUpdated(gitHubPatToken = it)
                                        },
                                        label = { Text(i18n.screen_app_settings_github_token_field_label) },
                                        visualTransformation = if (passwordVisible) {
                                            VisualTransformation.None
                                        } else {
                                            PasswordVisualTransformation()
                                        },
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                        trailingIcon = {
                                            val image = if (passwordVisible) {
                                                Tabler.Outline.EyeOff
                                            } else {
                                                Tabler.Outline.Eye
                                            }
                                            val description = if (passwordVisible) {
                                                i18n.screen_app_settings_github_field_hide
                                            } else {
                                                i18n.screen_app_settings_github_field_show
                                            }
                                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                                Icon(imageVector = image, contentDescription = description, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(25.dp))
                                            }
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                            SectionItem(
                                title = i18n.screen_app_settings_checkout_timeout_item,
                                description = i18n.screen_app_settings_checkout_timeout_item_description
                            ) {
                                val checkTimeoutMinutes = SyncSettings.availableCheckTimeouts.associateWith { checkTimeout ->
                                    i18n.app_settings_checkout_time_in_minutes(checkTimeout)
                                }.toList()

                                OutlinedSelectField(
                                    selected = successState.syncSettings.validCheckTimeout,
                                    values = checkTimeoutMinutes,
                                    emptyText = i18n.app_settings_checkout_time_unknown,
                                    modifier = Modifier.padding(PaddingValues(bottom = 10.dp))
                                ) { value, _ ->
                                    viewModel.checkTimeoutUpdated(checkTimeout = value)
                                }
                            }

                            SectionItem(
                                title = i18n.screen_app_settings_pull_requests_clean_up_item,
                                description = i18n.screen_app_settings_pull_requests_clean_up_item_description
                            ) {
                                val cleanUpTimeoutHours = SyncSettings.availablePullRequestCleanUpTimeout.associateWith { cleanUpTimeout ->
                                    i18n.app_settings_pr_cleanup_in_hours(cleanUpTimeout)
                                }.toList()

                                OutlinedSelectField(
                                    selected = successState.syncSettings.validPullRequestCleanUpTimeout,
                                    values = cleanUpTimeoutHours,
                                    emptyText = i18n.app_settings_pr_cleanup_unknown,
                                    modifier = Modifier.padding(PaddingValues(bottom = 10.dp))
                                ) { value, _ ->
                                    viewModel.pullRequestCleanUpTimeoutUpdated(cleanUpTimeout = value)
                                }
                            }

                            SectionItem(
                                title = i18n.screen_app_settings_last_synchronization_results_item,
                            ) {
                                Button(onClick = onSyncResultsClicked) {
                                    Text(i18n.screen_app_settings_last_synchronization_results_button)
                                }
                            }
                        }

                        SectionCategory(i18n.screen_app_settings_appliation_section) {
                            SectionItem(
                                title = i18n.screen_app_settings_theme_item,
                                description = i18n.screen_app_settings_theme_item_description
                            ) {
                                val appThemeValues = listOf(null to i18n.app_theme_system_default, true to i18n.app_theme_dark, false to i18n.app_theme_light)

                                OutlinedSelectField(
                                    selected = successState.appSettings.darkTheme,
                                    values = appThemeValues,
                                    modifier = Modifier.padding(PaddingValues(bottom = 10.dp))
                                ) { value, _ ->
                                    viewModel.appThemeUpdated(appDarkTheme = value)
                                }
                            }
                        }

                        SectionCategory(i18n.screen_app_settings_pull_requests_notifications_section) {
                            SectionItem(
                                title = i18n.screen_app_settings_notifications_pr_filter_out_title,
                                description = i18n.screen_app_settings_notifications_pr_filter_out_description,
                                content = {
                                    Row {
                                        LabelledCheckBox(
                                            label = i18n.pull_request_state_draft,
                                            checked = pullRequestFilterOptionsDraft,
                                            onCheckedChange = {
                                                pullRequestFilterOptionsDraft = it
                                                viewModel.newPullRequestsNotificationsOptions(
                                                    successState.appSettings.pullRequestNotificationsFilterOptions.copy(
                                                        draft = it
                                                    )
                                                )
                                            }
                                        )
                                        Spacer(Modifier.width(10.dp))
                                        LabelledCheckBox(
                                            label = i18n.pull_request_state_open,
                                            checked = pullRequestFilterOptionsOpen,
                                            onCheckedChange = {
                                                pullRequestFilterOptionsOpen = it
                                                viewModel.newPullRequestsNotificationsOptions(
                                                    successState.appSettings.pullRequestNotificationsFilterOptions.copy(
                                                        open = it
                                                    )
                                                )
                                            }
                                        )
                                        Spacer(Modifier.width(10.dp))
                                        LabelledCheckBox(
                                            label = i18n.pull_request_state_closed,
                                            checked = pullRequestFilterOptionsClosed,
                                            onCheckedChange = {
                                                pullRequestFilterOptionsClosed = it
                                                viewModel.newPullRequestsNotificationsOptions(
                                                    successState.appSettings.pullRequestNotificationsFilterOptions.copy(
                                                        closed = it
                                                    )
                                                )
                                            }
                                        )
                                        Spacer(Modifier.width(10.dp))
                                        LabelledCheckBox(
                                            label = i18n.pull_request_state_merged,
                                            checked = pullRequestFilterOptionsMerged,
                                            onCheckedChange = {
                                                pullRequestFilterOptionsMerged = it
                                                viewModel.newPullRequestsNotificationsOptions(
                                                    successState.appSettings.pullRequestNotificationsFilterOptions.copy(
                                                        merged = it
                                                    )
                                                )
                                            }
                                        )
                                    }
                                }
                            )
                            SectionItem(
                                title = i18n.screen_app_settings_notifications_pr_state_title,
                                description = i18n.screen_app_settings_notifications_pr_state_description,
                                content = {
                                    LabelledCheckBox(
                                        label = i18n.screen_app_settings_notifications_pr_state_checkbox_label,
                                        checked = pullRequestStateNotifications,
                                        onCheckedChange = {
                                            pullRequestStateNotifications = it
                                            viewModel.newPullRequestsNotificationsEnabledUpdated(it)
                                        }
                                    )
                                }
                            )
                            SectionItem(
                                title = i18n.screen_app_settings_notifications_pr_activity_title,
                                description = i18n.screen_app_settings_notifications_pr_activity_description,
                                content = {
                                    LabelledCheckBox(
                                        label = i18n.screen_app_settings_notifications_pr_activity_checkbox_label,
                                        checked = pullRequestActivityNotifications,
                                        onCheckedChange = {
                                            pullRequestActivityNotifications = it
                                            viewModel.updatedPullRequestsNotificationsEnabledUpdated(it)
                                        }
                                    )
                                }
                            )
                        }

                        SectionCategory(i18n.screen_app_settings_releases_notifications_section) {
                            SectionItemSwitch(
                                title = i18n.screen_app_settings_notifications_new_release_title,
                                description = i18n.screen_app_settings_notifications_new_release_description,
                                checked = newReleaseNotificationsEnabled,
                                onCheckedChange = {
                                    newReleaseNotificationsEnabled = it
                                    viewModel.newReleaseNotificationsEnabledUpdated(it)
                                },
                            )
                            SectionItemSwitch(
                                title = i18n.screen_app_settings_notifications_update_release_title,
                                description = i18n.screen_app_settings_notifications_update_release_description,
                                checked = updatedReleaseNotificationsEnabled,
                                onCheckedChange = {
                                    updatedReleaseNotificationsEnabled = it
                                    viewModel.updatedReleaseNotificationsEnabledUpdated(it)
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}