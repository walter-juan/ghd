package com.woowla.ghd.presentation.screens

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.woowla.compose.icon.collections.tabler.Tabler
import com.woowla.compose.icon.collections.tabler.tabler.Outline
import com.woowla.compose.icon.collections.tabler.tabler.outline.Refresh
import com.woowla.compose.icon.collections.tabler.tabler.outline.User
import com.woowla.compose.icon.collections.tabler.tabler.outline.Users
import com.woowla.ghd.presentation.app.AppDimens
import com.woowla.ghd.presentation.app.i18n
import com.woowla.ghd.presentation.components.LabelledCheckBox
import com.woowla.ghd.presentation.components.LabelledRadioButton
import com.woowla.ghd.presentation.components.ScreenScrollable
import com.woowla.ghd.presentation.components.SectionCategory
import com.woowla.ghd.presentation.components.SectionItem
import com.woowla.ghd.presentation.components.TableCell
import com.woowla.ghd.presentation.components.TopBar

object NotificationsScreen {
    @Composable
    fun Content(
        onBackClick: (() -> Unit)? = null,
    ) {
        ScreenScrollable(
            topBar = {
                TopBar(
                    title = i18n.top_bar_title_notifications,
                    navOnClick = onBackClick
                )
            }
        ) {
            var filterUsername by remember { mutableStateOf("") }
            val filterUsernameNotBlank by remember { derivedStateOf { filterUsername.isNotBlank() } }

            val notificationsStateOptions = listOf("Specific", "All", "None")
            var notificationsStateSelectedOption by remember { mutableStateOf(notificationsStateOptions.last()) }
            val notificationsStateSpecificOptionEnabled by remember { derivedStateOf {
                notificationsStateSelectedOption == "Specific" && filterUsernameNotBlank
            } }

            val notificationsActivityOptions = listOf("Specific", "All", "None")
            var notificationsActivitySelectedOption by remember { mutableStateOf(notificationsActivityOptions.last()) }
            val activityEnabled = notificationsActivitySelectedOption == "Specific"

            var notificationsOpenFromYourPullRequests by remember { mutableStateOf(false) }
            var notificationsClosedFromYourPullRequests by remember { mutableStateOf(false) }
            var notificationsMergedFromYourPullRequests by remember { mutableStateOf(false) }
            var notificationsDraftFromYourPullRequests by remember { mutableStateOf(false) }

            var notificationsOpenFromOthersPullRequests by remember { mutableStateOf(false) }
            var notificationsClosedFromOthersPullRequests by remember { mutableStateOf(false) }
            var notificationsMergedFromOthersPullRequests by remember { mutableStateOf(false) }
            var notificationsDraftFromOthersPullRequests by remember { mutableStateOf(false) }

            var notificationsReviewsFromYourPullRequests by remember { mutableStateOf(false) }
            var notificationsReviewsReRequest by remember { mutableStateOf(false) }
            var notificationsChecksFromYourPullRequests by remember { mutableStateOf(false) }
            var notificationsMergeableFromYourPullRequests by remember { mutableStateOf(false) }

            var newReleaseNotificationsEnabled by remember { mutableStateOf(false) }

            Column(
                modifier = Modifier
                    .padding(AppDimens.contentPaddingAllDp)
                    .width(AppDimens.contentWidthDp)
            ) {
                SectionCategory("Filters") {
                    SectionItem(
                        title = "Username",
                        description = "Add your login username in order to have a better notifications. Adding your username will enable the specific notifications.",
                        content = {
                            OutlinedTextField(
                                value = filterUsername,
                                onValueChange = { filterUsername = it },
                                label = { Text("Username") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    )
                }
                SectionCategory(i18n.screen_app_settings_pull_requests_notifications_section) {
                    SectionItem(
                        title = "State notifications",
                        description = "Notify me when a pull request has been created in an specific state or changed for example from Draft to Open",
                        content = {
                            SelectedOptions(
                                options = notificationsStateOptions,
                                selectedOption = notificationsStateSelectedOption,
                                onSelectionChange = {
                                    notificationsStateSelectedOption = it
                                }
                            )

                            StateNotificationsTableRow(
                                title = i18n.pull_request_state_draft,
                                enabled = notificationsStateSpecificOptionEnabled,
                                yourPullRequestsSelected = notificationsDraftFromYourPullRequests,
                                othersPullRequestsSelected = notificationsDraftFromOthersPullRequests,
                                onYourPullRequestsSelectedClick = {
                                    notificationsDraftFromYourPullRequests = !notificationsDraftFromYourPullRequests
                                },
                                onOthersPullRequestsSelectedClick = {
                                    notificationsDraftFromOthersPullRequests = !notificationsDraftFromOthersPullRequests
                                },
                            )

                            StateNotificationsTableRow(
                                title = i18n.pull_request_state_open,
                                enabled = notificationsStateSpecificOptionEnabled,
                                yourPullRequestsSelected = notificationsOpenFromYourPullRequests,
                                othersPullRequestsSelected = notificationsOpenFromOthersPullRequests,
                                onYourPullRequestsSelectedClick = {
                                    notificationsOpenFromYourPullRequests = !notificationsOpenFromYourPullRequests
                                },
                                onOthersPullRequestsSelectedClick = {
                                    notificationsOpenFromOthersPullRequests = !notificationsOpenFromOthersPullRequests
                                },
                            )

                            StateNotificationsTableRow(
                                title = i18n.pull_request_state_closed,
                                enabled = notificationsStateSpecificOptionEnabled,
                                yourPullRequestsSelected = notificationsClosedFromYourPullRequests,
                                othersPullRequestsSelected = notificationsClosedFromOthersPullRequests,
                                onYourPullRequestsSelectedClick = {
                                    notificationsClosedFromYourPullRequests = !notificationsClosedFromYourPullRequests
                                },
                                onOthersPullRequestsSelectedClick = {
                                    notificationsClosedFromOthersPullRequests = !notificationsClosedFromOthersPullRequests
                                },
                            )

                            StateNotificationsTableRow(
                                title = i18n.pull_request_state_merged,
                                enabled = notificationsStateSpecificOptionEnabled,
                                yourPullRequestsSelected = notificationsMergedFromYourPullRequests,
                                othersPullRequestsSelected = notificationsMergedFromOthersPullRequests,
                                onYourPullRequestsSelectedClick = {
                                    notificationsMergedFromYourPullRequests = !notificationsMergedFromYourPullRequests
                                },
                                onOthersPullRequestsSelectedClick = {
                                    notificationsMergedFromOthersPullRequests = !notificationsMergedFromOthersPullRequests
                                },
                            )
                        }
                    )
                    SectionItem(
                        title = i18n.screen_app_settings_notifications_pr_activity_title,
                        description = i18n.screen_app_settings_notifications_pr_activity_description,
                        content = {
                            SelectedOptions(
                                options = notificationsActivityOptions,
                                selectedOption = notificationsActivitySelectedOption,
                                onSelectionChange = {
                                    notificationsActivitySelectedOption = it
                                }
                            )
                            TableRow(
                                title = "Reviews",
                                description = "You will be notified when a new review has been proposed to your pull requests and/or a some requested a new review from you.",
                                enabled = activityEnabled,
                                column2 = {
                                    FilterChip(
                                        label = { Text("From your PRs") },
                                        enabled = activityEnabled,
                                        leadingIcon = { Icon(Tabler.Outline.User, contentDescription = null) },
                                        selected = notificationsReviewsFromYourPullRequests,
                                        onClick = { notificationsReviewsFromYourPullRequests = !notificationsReviewsFromYourPullRequests }
                                    )
                                },
                                column3 = {
                                    FilterChip(
                                        label = { Text("Your reviews re-requests") },
                                        enabled = activityEnabled,
                                        leadingIcon = { Icon(Tabler.Outline.Refresh, contentDescription = null) },
                                        selected = notificationsReviewsReRequest,
                                        onClick = { notificationsReviewsReRequest = !notificationsReviewsReRequest }
                                    )
                                },
                            )

                            TableRow(
                                title = "Checks",
                                description = "You will be notified when the statuses checks has been changed in your pull requests",
                                enabled = activityEnabled,
                                column2 = {
                                    FilterChip(
                                        label = { Text("From your PRs") },
                                        enabled = activityEnabled,
                                        leadingIcon = { Icon(Tabler.Outline.User, contentDescription = null) },
                                        selected = notificationsChecksFromYourPullRequests,
                                        onClick = { notificationsChecksFromYourPullRequests = !notificationsChecksFromYourPullRequests }
                                    )
                                },
                                column3 = { },
                            )

                            TableRow(
                                title = "Mergeable",
                                description = "You will be notified when the a pull request can be merged",
                                enabled = activityEnabled,
                                column2 = {
                                    FilterChip(
                                        label = { Text("From your PRs") },
                                        enabled = activityEnabled,
                                        leadingIcon = { Icon(Tabler.Outline.User, contentDescription = null) },
                                        selected = notificationsMergeableFromYourPullRequests,
                                        onClick = { notificationsMergeableFromYourPullRequests = !notificationsMergeableFromYourPullRequests }
                                    )
                                },
                                column3 = { },
                            )
                        }
                    )
                }

                SectionCategory(i18n.screen_app_settings_releases_notifications_section) {
                    LabelledCheckBox(
                        label = i18n.screen_app_settings_notifications_new_release_title,
                        description = i18n.screen_app_settings_notifications_new_release_description,
                        checked = newReleaseNotificationsEnabled,
                        onCheckedChange = {
                            newReleaseNotificationsEnabled = it
                        }
                    )
                }
            }
        }
    }

    @Composable
    private fun SelectedOptions(
        options: List<String>,
        selectedOption: String?,
        onSelectionChange: (String) -> Unit,
    ) {
        Row {
            options.forEachIndexed { index, option ->
                if (index > 0) {
                    Spacer(modifier = Modifier.width(10.dp))
                }
                LabelledRadioButton(
                    label = option,
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
        yourPullRequestsSelected: Boolean,
        othersPullRequestsSelected: Boolean,
        onYourPullRequestsSelectedClick: () -> Unit,
        onOthersPullRequestsSelectedClick: () -> Unit,
    ) {
        TableRow(
            title = title,
            enabled = enabled,
            column2 = {
                FilterChip(
                    label = { Text("Your PRs") },
                    enabled = enabled,
                    leadingIcon = { Icon(Tabler.Outline.User, contentDescription = null) },
                    selected = yourPullRequestsSelected,
                    onClick = onYourPullRequestsSelectedClick
                )
            },
            column3 = {
                FilterChip(
                    label = { Text("Others PRs") },
                    enabled = enabled,
                    leadingIcon = { Icon(Tabler.Outline.Users, contentDescription = null) },
                    selected = othersPullRequestsSelected,
                    onClick = onOthersPullRequestsSelectedClick
                )
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