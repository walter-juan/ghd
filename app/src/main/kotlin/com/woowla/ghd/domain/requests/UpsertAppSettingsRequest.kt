package com.woowla.ghd.domain.requests

data class UpsertAppSettingsRequest(
    val darkTheme: Boolean?,
    val encryptedDatabase: Boolean,
    val newPullRequestsNotificationsEnabled: Boolean,
    val updatedPullRequestsNotificationsEnabled: Boolean,
    val newReleaseNotificationsEnabled: Boolean,
    val updatedReleaseNotificationsEnabled: Boolean
)