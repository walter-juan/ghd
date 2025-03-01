package com.woowla.ghd.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.woowla.compose.icon.collections.tabler.Tabler
import com.woowla.compose.icon.collections.tabler.tabler.Outline
import com.woowla.compose.icon.collections.tabler.tabler.outline.DeviceFloppy
import com.woowla.compose.icon.collections.tabler.tabler.outline.Eye
import com.woowla.compose.icon.collections.tabler.tabler.outline.EyeOff
import com.woowla.compose.icon.collections.tabler.tabler.outline.Key
import com.woowla.compose.icon.collections.tabler.tabler.outline.Palette
import com.woowla.compose.icon.collections.tabler.tabler.outline.Refresh
import com.woowla.compose.icon.collections.tabler.tabler.outline.Trash
import com.woowla.ghd.domain.entities.SyncSettings
import com.woowla.ghd.presentation.i18nUi
import com.woowla.ghd.presentation.app.AppDimens
import com.woowla.ghd.presentation.components.OutlinedSelectField
import com.woowla.ghd.presentation.components.ScreenScrollable
import com.woowla.ghd.presentation.components.Section
import com.woowla.ghd.presentation.components.SectionItem
import com.woowla.ghd.presentation.components.TopBar
import com.woowla.ghd.presentation.viewmodels.SettingsStateMachine.Act
import com.woowla.ghd.presentation.viewmodels.SettingsStateMachine.St
import com.woowla.ghd.presentation.viewmodels.SettingsViewModel

object SettingsScreen {
    @Composable
    fun Content(
        viewModel: SettingsViewModel,
        onSyncResultsClicked: () -> Unit,
    ) {
        val state by viewModel.state.collectAsState()
        Screen(
            state = state,
            dispatchAction = viewModel::dispatch,
            onSyncResultsClicked = onSyncResultsClicked
        )
    }

    @Composable
    private fun Screen(
        state: St?,
        dispatchAction: (Act) -> Unit,
        onSyncResultsClicked: () -> Unit,
    ) {
        val snackbarHostState = remember { SnackbarHostState() }

        if (state is St.Success) {
            val snackbarMessage = when {
                state.savedSuccessfully == true -> i18nUi.generic_saved
                state.savedSuccessfully == false -> i18nUi.generic_error
                else -> null
            }
            if (snackbarMessage != null) {
                LaunchedEffect(snackbarHostState) {
                    val result = snackbarHostState.showSnackbar(message = snackbarMessage, withDismissAction = true, duration = SnackbarDuration.Indefinite)
                    if (result == SnackbarResult.Dismissed) {
                        dispatchAction.invoke(Act.CleanUpSaveSuccessfully)
                    }
                }
            }
        }

        ScreenScrollable(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopBar(
                    title = i18nUi.top_bar_title_settings,
                    actions = {
                        OutlinedIconButton(
                            colors = IconButtonDefaults.outlinedIconButtonColors(containerColor = MaterialTheme.colorScheme.primary),
                            onClick = { dispatchAction(Act.Save) }
                        ) {
                            Icon(
                                Tabler.Outline.DeviceFloppy,
                                contentDescription = i18nUi.screen_app_settings_save,
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
                    null, St.Initializing -> { }
                    is St.Error -> {
                        Text(i18nUi.generic_error)
                    }
                    is St.Success -> {
                        SynchronizationSection(
                            githubPatToken = state.syncSettings.githubPatToken,
                            onGithubPatTokenUpdated = { dispatchAction(Act.UpdatePatToken(it)) },
                            selectedCheckTimeoutMinutes = state.syncSettings.validCheckTimeout,
                            availableCheckTimeoutMinutes = SyncSettings.availableCheckTimeouts.associateWith { checkTimeout ->
                                i18nUi.app_settings_checkout_time_in_minutes(checkTimeout)
                            }.toList(),
                            onCheckTimeoutMinutes = { dispatchAction(Act.UpdateCheckTimeout(it)) },
                            selectedCleanUpTimeoutHours = state.syncSettings.validPullRequestCleanUpTimeout,
                            availableCleanUpTimeoutHours = SyncSettings.availablePullRequestCleanUpTimeout.associateWith { cleanUpTimeout ->
                                i18nUi.app_settings_pr_cleanup_in_hours(cleanUpTimeout)
                            }.toList(),
                            onCleanUpTimeoutHours = { dispatchAction(Act.UpdatePullRequestCleanUpTimeout(it)) },
                            onSyncResultsClicked = onSyncResultsClicked,
                        )

                        ApplicationSection(
                            selectedTheme = state.appSettings.darkTheme,
                            availableThemes  = listOf(null to i18nUi.app_theme_system_default, true to i18nUi.app_theme_dark, false to i18nUi.app_theme_light),
                            onSelectedTheme = { dispatchAction(Act.UpdateAppTheme(it)) }
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun SynchronizationSection(
        githubPatToken: String,
        onGithubPatTokenUpdated: (String) -> Unit,
        selectedCheckTimeoutMinutes: Long,
        availableCheckTimeoutMinutes: List<Pair<Long, String>>,
        onCheckTimeoutMinutes: (Long) -> Unit,
        selectedCleanUpTimeoutHours: Long,
        availableCleanUpTimeoutHours: List<Pair<Long, String>>,
        onCleanUpTimeoutHours: (Long) -> Unit,
        onSyncResultsClicked: () -> Unit,
    ) {
        var passwordVisible by remember { mutableStateOf(false) }

        Section(title = "GitHub settings") {
            SectionItem(
                title = i18nUi.screen_app_settings_github_token_item,
                description = i18nUi.screen_app_settings_github_token_item_description,
                leadingIcon = {
                    Icon(
                        imageVector = Tabler.Outline.Key,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)
                        .padding(PaddingValues(bottom = 10.dp))
                ) {
                    OutlinedTextField(
                        value = githubPatToken,
                        onValueChange = onGithubPatTokenUpdated,
                        label = { Text(i18nUi.screen_app_settings_github_token_field_label) },
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
                                i18nUi.screen_app_settings_github_field_hide
                            } else {
                                i18nUi.screen_app_settings_github_field_show
                            }
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(imageVector = image, contentDescription = description, modifier = Modifier.size(25.dp))
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Section(i18nUi.screen_app_settings_synchronization_section) {
            SectionItem(
                title = i18nUi.screen_app_settings_checkout_timeout_item,
                description = i18nUi.screen_app_settings_checkout_timeout_item_description,
                leadingIcon = {
                    Icon(
                        imageVector = Tabler.Outline.Refresh,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
            ) {
                OutlinedSelectField(
                    selected = selectedCheckTimeoutMinutes,
                    values = availableCheckTimeoutMinutes,
                    emptyText = i18nUi.app_settings_checkout_time_unknown,
                    modifier = Modifier.padding(PaddingValues(bottom = 10.dp))
                ) { value, _ ->
                    onCheckTimeoutMinutes.invoke(value)
                }
            }

            SectionItem(
                title = i18nUi.screen_app_settings_pull_requests_clean_up_item,
                description = i18nUi.screen_app_settings_pull_requests_clean_up_item_description,
                leadingIcon = {
                    Icon(
                        imageVector = Tabler.Outline.Trash,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
            ) {
                OutlinedSelectField(
                    selected = selectedCleanUpTimeoutHours,
                    values = availableCleanUpTimeoutHours,
                    emptyText = i18nUi.app_settings_pr_cleanup_unknown,
                    modifier = Modifier.padding(PaddingValues(bottom = 10.dp))
                ) { value, _ ->
                    onCleanUpTimeoutHours.invoke(value)
                }
            }

            SectionItem(
                title = i18nUi.screen_app_settings_last_synchronization_results_item,
            ) {
                Button(onClick = onSyncResultsClicked) {
                    Text(i18nUi.screen_app_settings_last_synchronization_results_button)
                }
            }
        }
    }

    @Composable
    private fun ApplicationSection(
        selectedTheme: Boolean?,
        availableThemes: List<Pair<Boolean?, String>>,
        onSelectedTheme: (Boolean?) -> Unit,
    ) {
        Section(i18nUi.screen_app_settings_appliation_section) {
            SectionItem(
                title = i18nUi.screen_app_settings_theme_item,
                description = i18nUi.screen_app_settings_theme_item_description,
                leadingIcon = {
                    Icon(
                        imageVector = Tabler.Outline.Palette,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
            ) {
                OutlinedSelectField(
                    selected = selectedTheme,
                    values = availableThemes,
                    modifier = Modifier.padding(PaddingValues(bottom = 10.dp))
                ) { value, _ ->
                    onSelectedTheme.invoke(value)
                }
            }
        }
    }
}