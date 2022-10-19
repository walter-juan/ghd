package com.woowla.ghd.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import com.woowla.ghd.domain.entities.AppSettings
import com.woowla.ghd.presentation.app.i18n
import com.woowla.ghd.presentation.components.Screen
import com.woowla.ghd.presentation.components.SectionCategory
import com.woowla.ghd.presentation.components.SectionItem
import com.woowla.ghd.presentation.components.TopBar
import com.woowla.ghd.presentation.viewmodels.AppSettingsViewModel

class AppSettingsScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel = rememberScreenModel { AppSettingsViewModel() }
        val scaffoldState = rememberScaffoldState()

        val settingsState by viewModel.state.collectAsState()

        LaunchedEffect(scaffoldState.snackbarHostState) {
            viewModel.events.collect { event->
                when (event) {
                    AppSettingsViewModel.Events.Saved -> {
                        scaffoldState.snackbarHostState.showSnackbar(i18n.screen_app_settings_saved)
                    }
                }
            }
        }

        Screen(
            scaffoldState = scaffoldState,
            topBar = {
                TopBar(
                    title = i18n.top_bar_title_settings,
                    actions = {
                        IconButton(
                            onClick = { viewModel.saveSettings() }
                        ) {
                            Icon(Icons.Filled.Save, contentDescription = i18n.screen_app_settings_save)
                        }
                    }
                )
            }
        ) {
            when(settingsState) {
                AppSettingsViewModel.State.Loading -> item { /* nothing, this should be fast to load? */ }
                is AppSettingsViewModel.State.Error -> item { Text(i18n.generic_error) }
                is AppSettingsViewModel.State.Success -> {
                    val successState = (settingsState as AppSettingsViewModel.State.Success)

                    item {
                        var gitHubPatToken by remember { mutableStateOf(successState.appSettings.githubPatToken ?: "") }
                        var passwordVisible by remember { mutableStateOf(false) }
                        SectionCategory(i18n.screen_app_settings_networking_section) {
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
                                                Icons.Filled.Visibility
                                            } else {
                                                Icons.Filled.VisibilityOff
                                            }
                                            val description = if (passwordVisible) {
                                                i18n.screen_app_settings_github_field_hide
                                            } else {
                                                i18n.screen_app_settings_github_field_show
                                            }
                                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                                Icon(imageVector = image, description)
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
                                val checkTimeoutMinutes = AppSettings.availableCheckTimeouts.associateWith { checkTimeout ->
                                    i18n.app_settings_checkout_time_in_minutes(checkTimeout)
                                }

                                var expanded by remember { mutableStateOf(false) }
                                var textFieldText by remember { mutableStateOf(checkTimeoutMinutes[successState.appSettings.checkTimeout] ?: i18n.app_settings_checkout_time_unknown) }
                                var textFieldSize by remember { mutableStateOf(Size.Zero) }
                                val icon = if (expanded) { Icons.Filled.KeyboardArrowUp } else { Icons.Filled.KeyboardArrowDown }

                                Column(
                                    modifier = Modifier.padding(PaddingValues(bottom = 10.dp))
                                ) {
                                    OutlinedTextField(
                                        value = textFieldText,
                                        onValueChange = { textFieldText = it },
                                        enabled = false,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .onGloballyPositioned { coordinates ->
                                                // this value is used to assign to the DropDown the same width
                                                textFieldSize = coordinates.size.toSize()
                                            }
                                            .clickable { expanded = !expanded },
                                        trailingIcon = { Icon(icon, contentDescription = null) }
                                    )
                                    DropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false },
                                        modifier = Modifier
                                            .width(with(LocalDensity.current){ textFieldSize.width.toDp() })
                                    ) {
                                        checkTimeoutMinutes.forEach { (checkTimeout, text) ->
                                            DropdownMenuItem(onClick = {
                                                textFieldText = text
                                                expanded = false
                                                viewModel.checkTimeoutUpdated(checkTimeout = checkTimeout)
                                            }) {
                                                Text(text = text)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        SectionCategory(i18n.screen_app_settings_appliation_section) {
                            SectionItem(
                                title = i18n.screen_app_settings_pull_requests_clean_up_item,
                                description = i18n.screen_app_settings_pull_requests_clean_up_item_description
                            ) {
                                val cleanUpTimeoutHours = AppSettings.availablePullRequestCleanUpTimeout.associateWith { cleanUpTimeout ->
                                    i18n.app_settings_pr_cleanup_in_hours(cleanUpTimeout)
                                }

                                var expanded by remember { mutableStateOf(false) }
                                var textFieldText by remember { mutableStateOf(cleanUpTimeoutHours[successState.appSettings.pullRequestCleanUpTimeout] ?: i18n.app_settings_pr_cleanup_unknown) }
                                var textFieldSize by remember { mutableStateOf(Size.Zero) }
                                val icon = if (expanded) { Icons.Filled.KeyboardArrowUp } else { Icons.Filled.KeyboardArrowDown }

                                Column(
                                    modifier = Modifier.padding(PaddingValues(bottom = 10.dp))
                                ) {
                                    OutlinedTextField(
                                        value = textFieldText,
                                        onValueChange = { textFieldText = it },
                                        enabled = false,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .onGloballyPositioned { coordinates ->
                                                // this value is used to assign to the DropDown the same width
                                                textFieldSize = coordinates.size.toSize()
                                            }
                                            .clickable { expanded = !expanded },
                                        trailingIcon = { Icon(icon, contentDescription = null) }
                                    )
                                    DropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false },
                                        modifier = Modifier
                                            .width(with(LocalDensity.current){ textFieldSize.width.toDp() })
                                    ) {
                                        cleanUpTimeoutHours.forEach { (cleanUpTimeout, text) ->
                                            DropdownMenuItem(onClick = {
                                                textFieldText = text
                                                expanded = false
                                                viewModel.pullRequestCleanUpTimeoutUpdated(cleanUpTimeout = cleanUpTimeout)
                                            }) {
                                                Text(text = text)
                                            }
                                        }
                                    }
                                }
                            }

                            SectionItem(
                                title = i18n.screen_app_settings_theme_item,
                                description = i18n.screen_app_settings_theme_item_description
                            ) {
                                val appThemeValues = mapOf(null to i18n.app_theme_system_default, true to i18n.app_theme_dark, false to i18n.app_theme_light)
                                var expanded by remember { mutableStateOf(false) }

                                var textFieldText by remember { mutableStateOf(appThemeValues[successState.appSettings.appDarkTheme] ?: i18n.app_settings_app_theme_unknown) }
                                var textFieldSize by remember { mutableStateOf(Size.Zero) }
                                val icon = if (expanded) { Icons.Filled.KeyboardArrowUp } else { Icons.Filled.KeyboardArrowDown }

                                Column(
                                    modifier = Modifier.padding(PaddingValues(bottom = 10.dp))
                                ) {
                                    OutlinedTextField(
                                        value = textFieldText,
                                        onValueChange = { textFieldText = it },
                                        enabled = false,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .onGloballyPositioned { coordinates ->
                                                // this value is used to assign to the DropDown the same width
                                                textFieldSize = coordinates.size.toSize()
                                            }
                                            .clickable { expanded = !expanded },
                                        trailingIcon = { Icon(icon, contentDescription = null) }
                                    )
                                    DropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false },
                                        modifier = Modifier
                                            .width(with(LocalDensity.current){textFieldSize.width.toDp()})
                                    ) {
                                        appThemeValues.forEach { (appDarkTheme, text) ->
                                            DropdownMenuItem(onClick = {
                                                textFieldText = text
                                                expanded = false
                                                viewModel.appThemeUpdated(appDarkTheme = appDarkTheme)
                                            }) {
                                                Text(text = text)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}