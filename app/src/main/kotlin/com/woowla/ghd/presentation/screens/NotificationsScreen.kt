package com.woowla.ghd.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.ContentAlpha
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.woowla.compose.icon.collections.tabler.Tabler
import com.woowla.compose.icon.collections.tabler.tabler.Outline
import com.woowla.compose.icon.collections.tabler.tabler.outline.Activity
import com.woowla.compose.icon.collections.tabler.tabler.outline.DeviceFloppy
import com.woowla.compose.icon.collections.tabler.tabler.outline.StatusChange
import com.woowla.compose.icon.collections.tabler.tabler.outline.Tag
import com.woowla.compose.icon.collections.tabler.tabler.outline.User
import com.woowla.compose.icon.collections.tabler.tabler.outline.Users
import com.woowla.ghd.domain.entities.NotificationsSettings
import com.woowla.ghd.presentation.app.AppDimens
import com.woowla.ghd.presentation.app.i18n
import com.woowla.ghd.presentation.components.LabelledRadioButton
import com.woowla.ghd.presentation.components.ScreenScrollable
import com.woowla.ghd.presentation.components.TableCell
import com.woowla.ghd.presentation.components.TopBar
import com.woowla.ghd.presentation.components.Section
import com.woowla.ghd.presentation.components.SectionItem
import com.woowla.ghd.presentation.components.SectionItemWithSwitch
import com.woowla.ghd.presentation.viewmodels.NotificationsStateMachine.St
import com.woowla.ghd.presentation.viewmodels.NotificationsStateMachine.Act
import com.woowla.ghd.presentation.viewmodels.NotificationsViewModel
import org.koin.compose.viewmodel.koinViewModel

object NotificationsScreen {
    @Composable
    fun Content(
        viewModel : NotificationsViewModel = koinViewModel(),
        onBackClick: (() -> Unit)? = null,
    ) {
        val state by viewModel.state.collectAsState()
        Screen(
            state = state,
            dispatchAction = viewModel::dispatch,
            onBackClick = onBackClick,
        )
    }

    @Composable
    private fun Screen(
        state: St?,
        dispatchAction: (Act) -> Unit,
        onBackClick: (() -> Unit)? = null,
    ) {
        val snackbarHostState = remember { SnackbarHostState() }

        ScreenScrollable(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopBar(
                    title = i18n.top_bar_title_notifications,
                    navOnClick = onBackClick,
                    actions = {
                        if (state is St.Success) {
                            OutlinedIconButton(
                                colors = IconButtonDefaults.outlinedIconButtonColors(containerColor = MaterialTheme.colorScheme.primary),
                                onClick = { dispatchAction(Act.Save) }
                            ) {
                                Icon(
                                    Tabler.Outline.DeviceFloppy,
                                    contentDescription = i18n.screen_app_settings_save,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.padding(10.dp).fillMaxWidth()
                                )
                            }
                        }
                    }
                )
            }
        ) {

            when(state) {
                null, St.Loading -> {
                    Text(i18n.generic_loading)
                }
                is St.Success -> {
                    NotificationsContent(
                        state = state,
                        dispatchAction = dispatchAction,
                    )
                    val snackbarMessage = when {
                        state.savedSuccessfully == true -> i18n.generic_saved
                        state.savedSuccessfully == false -> i18n.generic_error
                        else -> null
                    }
                    if (snackbarMessage != null) {
                        LaunchedEffect(snackbarHostState) {
                            val result = snackbarHostState.showSnackbar(
                                message = snackbarMessage,
                                withDismissAction = true,
                                duration = SnackbarDuration.Indefinite,
                            )
                            if (result == SnackbarResult.Dismissed) {
                                dispatchAction.invoke(Act.CleanUpSaveSuccessfully)
                            }
                        }
                    }
                }
                is St.Error -> {
                    Text(i18n.generic_error)
                }
            }
        }
    }

    @Composable
    private fun NotificationsContent(
        state: St.Success,
        dispatchAction: (Act) -> Unit,
    ) {
        val notificationsSettings = state.notificationsSettings
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .padding(AppDimens.screenPadding)
                .fillMaxWidth()
        ) {

            Section("Filters") {
                SectionItem(
                    title = "Username",
                    description = "Add your login username in order to have a better notifications. Adding your username will enable the specific notifications.",
                    content = {
                        OutlinedTextField(
                            value = notificationsSettings.filterUsername,
                            onValueChange = {
                                dispatchAction.invoke(Act.UpdateFilterUsername(it))
                            },
                            label = { Text("Username") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                )
            }

            Section(i18n.screen_app_settings_pull_requests_notifications_section) {
                SectionItem(
                    title = "State notifications",
                    description = "Notify me when a pull request has been created in an specific state or changed for example from Draft to Open",
                    leadingIcon = {
                        Icon(
                            imageVector = Tabler.Outline.StatusChange,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    content = {
                        SelectedOptions(
                            notificationsSettings = notificationsSettings,
                            selectedOption = notificationsSettings.validStateEnabledOption,
                            onSelectionChange = {
                                dispatchAction.invoke(Act.UpdateStateEnabledOption(it))
                            }
                        )

                        StateNotificationsTableRow(
                            title = i18n.pull_request_state_draft,
                            enabled = notificationsSettings.validStateEnabledOption == NotificationsSettings.EnabledOption.FILTERED,
                            othersPullRequestsSelected = notificationsSettings.stateDraftFromOthersPullRequestsEnabled,
                            onOthersPullRequestsSelectedClick = {
                                dispatchAction.invoke(Act.UpdateStateDraftFromOthersPullRequestsEnabled(!notificationsSettings.stateDraftFromOthersPullRequestsEnabled))
                            },
                        )

                        StateNotificationsTableRow(
                            title = i18n.pull_request_state_open,
                            enabled = notificationsSettings.validStateEnabledOption == NotificationsSettings.EnabledOption.FILTERED,
                            othersPullRequestsSelected = notificationsSettings.stateOpenFromOthersPullRequestsEnabled,
                            onOthersPullRequestsSelectedClick = {
                                dispatchAction.invoke(Act.UpdateStateOpenFromOthersPullRequestsEnabled(!notificationsSettings.stateOpenFromOthersPullRequestsEnabled))
                            },
                        )

                        StateNotificationsTableRow(
                            title = i18n.pull_request_state_closed,
                            enabled = notificationsSettings.validStateEnabledOption == NotificationsSettings.EnabledOption.FILTERED,
                            othersPullRequestsSelected = notificationsSettings.stateClosedFromOthersPullRequestsEnabled,
                            onOthersPullRequestsSelectedClick = {
                                dispatchAction.invoke(Act.UpdateStateClosedFromOthersPullRequestsEnabled(!notificationsSettings.stateClosedFromOthersPullRequestsEnabled))
                            },
                        )

                        StateNotificationsTableRow(
                            title = i18n.pull_request_state_merged,
                            enabled = notificationsSettings.validStateEnabledOption == NotificationsSettings.EnabledOption.FILTERED,
                            othersPullRequestsSelected = notificationsSettings.stateMergedFromOthersPullRequestsEnabled,
                            onOthersPullRequestsSelectedClick = {
                                dispatchAction.invoke(Act.UpdateStateMergedFromOthersPullRequestsEnabled(!notificationsSettings.stateMergedFromOthersPullRequestsEnabled))
                            },
                        )
                    }
                )
                SectionItem(
                    title = i18n.screen_app_settings_notifications_pr_activity_title,
                    description = i18n.screen_app_settings_notifications_pr_activity_description,
                    leadingIcon = {
                        Icon(
                            imageVector = Tabler.Outline.Activity,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    content = {
                        SelectedOptions(
                            notificationsSettings = notificationsSettings,
                            selectedOption = notificationsSettings.validActivityEnabledOption,
                            onSelectionChange = {
                                dispatchAction.invoke(Act.UpdateActivityEnabledOption(it))
                            }
                        )
                        TableRow(
                            title = "Reviews",
                            description = "You will be notified when a new review has been proposed to your pull requests and/or a some requested a new review from you.",
                            enabled = notificationsSettings.validActivityEnabledOption == NotificationsSettings.EnabledOption.FILTERED,
                            column2 = {
                                FilterChip(
                                    label = { Text("From your PRs") },
                                    enabled = notificationsSettings.validActivityEnabledOption == NotificationsSettings.EnabledOption.FILTERED,
                                    leadingIcon = { Icon(Tabler.Outline.User, contentDescription = null) },
                                    selected = notificationsSettings.activityReviewsFromYourPullRequestsEnabled,
                                    onClick = {
                                        dispatchAction.invoke(Act.UpdateActivityReviewsFromYourPullRequestsEnabled(!notificationsSettings.activityReviewsFromYourPullRequestsEnabled))
                                    }
                                )
                            },
                            column3 = {
                                // TODO [review re-request] disabled
//                                FilterChip(
//                                    label = { Text("Your reviews re-requests") },
//                                    enabled = notificationsSettings.validActivityEnabledOption == NotificationsSettings.EnabledOption.FILTERED,
//                                    leadingIcon = { Icon(Tabler.Outline.Refresh, contentDescription = null) },
//                                    selected = notificationsSettings.activityReviewsReRequestEnabled,
//                                    onClick = {
//                                        dispatchAction.invoke(Act.UpdateActivityReviewsReRequestEnabled(!notificationsSettings.activityReviewsReRequestEnabled))
//                                    }
//                                )
                            },
                        )

                        TableRow(
                            title = "Checks",
                            description = "You will be notified when the statuses checks has been changed in your pull requests",
                            enabled = notificationsSettings.validActivityEnabledOption == NotificationsSettings.EnabledOption.FILTERED,
                            column2 = {
                                FilterChip(
                                    label = { Text("From your PRs") },
                                    enabled = notificationsSettings.validActivityEnabledOption == NotificationsSettings.EnabledOption.FILTERED,
                                    leadingIcon = { Icon(Tabler.Outline.User, contentDescription = null) },
                                    selected = notificationsSettings.activityChecksFromYourPullRequestsEnabled,
                                    onClick = {
                                        dispatchAction.invoke(Act.UpdateActivityChecksFromYourPullRequestsEnabled(!notificationsSettings.activityChecksFromYourPullRequestsEnabled))
                                    }
                                )
                            },
                            column3 = { },
                        )

                        TableRow(
                            title = "Mergeable",
                            description = "You will be notified when the a pull request can be merged",
                            enabled = notificationsSettings.validActivityEnabledOption == NotificationsSettings.EnabledOption.FILTERED,
                            column2 = {
                                FilterChip(
                                    label = { Text("From your PRs") },
                                    enabled = notificationsSettings.validActivityEnabledOption == NotificationsSettings.EnabledOption.FILTERED,
                                    leadingIcon = { Icon(Tabler.Outline.User, contentDescription = null) },
                                    selected = notificationsSettings.activityMergeableFromYourPullRequestsEnabled,
                                    onClick = {
                                        dispatchAction.invoke(Act.UpdateActivityMergeableFromYourPullRequestsEnabled(!notificationsSettings.activityMergeableFromYourPullRequestsEnabled))
                                    }
                                )
                            },
                            column3 = { },
                        )
                    }
                )
            }

            Section(i18n.screen_app_settings_releases_notifications_section) {
                SectionItemWithSwitch(
                    title = i18n.screen_app_settings_notifications_new_release_title,
                    description = i18n.screen_app_settings_notifications_new_release_description,
                    leadingIcon = {
                        Icon(
                            imageVector = Tabler.Outline.Tag,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    checked = notificationsSettings.newReleaseEnabled,
                    onCheckedChange = {
                        dispatchAction.invoke(Act.UpdateNewReleaseEnabled(!notificationsSettings.newReleaseEnabled))
                    }
                )
            }
        }
    }

    @Composable
    private fun SelectedOptions(
        notificationsSettings: NotificationsSettings,
        selectedOption: NotificationsSettings.EnabledOption?,
        onSelectionChange: (NotificationsSettings.EnabledOption) -> Unit,
    ) {
        Row {
            NotificationsSettings.EnabledOption.entries.forEachIndexed { index, option ->
                if (index > 0) {
                    Spacer(modifier = Modifier.width(10.dp))
                }
                LabelledRadioButton(
                    label = option.toString(),
                    enabled = notificationsSettings.isEnabledOptionAvailable(option),
                    selected = selectedOption == option,
                    onClick = {
                        onSelectionChange.invoke(option)
                    }
                )
            }
        }
    }

    @Composable
    private fun StateNotificationsTableRow(
        title: String,
        enabled: Boolean,
        othersPullRequestsSelected: Boolean,
        onOthersPullRequestsSelectedClick: () -> Unit,
    ) {
        TableRow(
            title = title,
            enabled = enabled,
            column2 = {
                FilterChip(
                    label = { Text("Others PRs") },
                    enabled = enabled,
                    leadingIcon = { Icon(Tabler.Outline.Users, contentDescription = null) },
                    selected = othersPullRequestsSelected,
                    onClick = onOthersPullRequestsSelectedClick
                )
            },
            column3 = {

            },
        )
    }

    @Composable
    private fun TableRow(
        title: String,
        description: String? = null,
        enabled: Boolean = true,
        column2: @Composable () -> Unit,
        column3: @Composable () -> Unit,
    ) {
        TableRow(
            column1 = {
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.then(if (!enabled) Modifier.alpha(ContentAlpha.disabled) else Modifier)
                    )
                    Spacer(Modifier.size(5.dp))
                    if (description != null) {
                        Text(
                            text = description,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.then(if (!enabled) Modifier.alpha(ContentAlpha.disabled) else Modifier)
                        )
                    }
                }
            },
            column2 = column2,
            column3 = column3,
        )
    }

    @Composable
    private fun TableRow(
        column1: @Composable () -> Unit,
        column2: @Composable () -> Unit,
        column3: @Composable () -> Unit,
    ) {
        val column1Weight = .30f // 30%
        val column2Weight = .35f // 35%
        val column3Weight = .35f // 35%

        Row(Modifier.fillMaxWidth()) {
            TableCell(weight = column1Weight) {
                column1()
            }
            TableCell(weight = column2Weight) {
                column2()
            }
            TableCell(weight = column3Weight) {
                column3()
            }
        }
    }
}