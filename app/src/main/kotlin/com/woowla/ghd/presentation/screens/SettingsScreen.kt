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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
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
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import com.woowla.compose.remixicon.DeviceSave2Fill
import com.woowla.compose.remixicon.RemixiconPainter
import com.woowla.compose.remixicon.SystemEyeFill
import com.woowla.compose.remixicon.SystemEyeOffFill
import com.woowla.ghd.domain.entities.SyncSettings
import com.woowla.ghd.presentation.app.AppDimens
import com.woowla.ghd.presentation.app.i18n
import com.woowla.ghd.presentation.components.*
import com.woowla.ghd.presentation.viewmodels.SettingsViewModel

class SettingsScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val viewModel = rememberScreenModel { SettingsViewModel() }
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
                                RemixiconPainter.DeviceSave2Fill,
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

                        var gitHubPatToken by remember { mutableStateOf(successState.syncSettings.githubPatToken ?: "") }
                        var passwordVisible by remember { mutableStateOf(false) }

                        var enableFeaturePreviewNewCards by remember { mutableStateOf(successState.appSettings.featurePreviewNewCards ?: false) }
                        var enableFeaturePreviewNewCardsBoldStyle by remember { mutableStateOf(successState.appSettings.featurePreviewNewCardsBoldStyle ?: false) }

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
                                                RemixiconPainter.SystemEyeFill
                                            } else {
                                                RemixiconPainter.SystemEyeOffFill
                                            }
                                            val description = if (passwordVisible) {
                                                i18n.screen_app_settings_github_field_hide
                                            } else {
                                                i18n.screen_app_settings_github_field_show
                                            }
                                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                                Icon(painter = image, contentDescription = description, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(25.dp))
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
                                    selected = successState.syncSettings.checkTimeout,
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
                                    selected = successState.syncSettings.pullRequestCleanUpTimeout,
                                    values = cleanUpTimeoutHours,
                                    emptyText = i18n.app_settings_pr_cleanup_unknown,
                                    modifier = Modifier.padding(PaddingValues(bottom = 10.dp))
                                ) { value, _ ->
                                    viewModel.pullRequestCleanUpTimeoutUpdated(cleanUpTimeout = value)
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

                        SectionCategory(i18n.screen_app_settings_feature_preview_section) {
                            SwitchText(
                                text = i18n.screen_app_settings_feature_preview_new_cards,
                                checked = enableFeaturePreviewNewCards,
                                onCheckedChange = {
                                    enableFeaturePreviewNewCards = it
                                    viewModel.featurePreviewNewCardsUpdated(it)
                                },
                            )

                            SwitchText(
                                text = i18n.screen_app_settings_feature_preview_new_cards_bold_style,
                                checked = enableFeaturePreviewNewCardsBoldStyle,
                                onCheckedChange = {
                                    enableFeaturePreviewNewCardsBoldStyle = it
                                    viewModel.featurePreviewNewCardsBoldStyleUpdated(it)
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}