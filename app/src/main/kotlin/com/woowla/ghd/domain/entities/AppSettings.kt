package com.woowla.ghd.domain.entities

data class AppSettings(
    val darkTheme: Boolean?,
    val pullRequestNotificationsFilterOptions: PullRequestNotificationsFilterOptions,
    val pullRequestStateNotificationsEnabled: Boolean,
    val pullRequestActivityNotificationsEnabled: Boolean,
    val newReleaseNotificationsEnabled: Boolean,
    val updatedReleaseNotificationsEnabled: Boolean
) {
}

data class PullRequestNotificationsFilterOptions(
    val open: Boolean,
    val closed: Boolean,
    val merged: Boolean,
    val draft: Boolean
)
