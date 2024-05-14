package com.woowla.ghd.domain.entities

data class AppSettings(
    val darkTheme: Boolean?,
    val newPullRequestsNotificationsEnabled: Boolean,
    val updatedPullRequestsNotificationsEnabled: Boolean,
    val newReleaseNotificationsEnabled: Boolean,
    val updatedReleaseNotificationsEnabled: Boolean
)
