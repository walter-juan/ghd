package com.woowla.ghd.data.local.prop.entities

data class PropAppSettings(
    val darkTheme: Boolean?,
    val encryptedDatabase: Boolean,
    val newPullRequestsNotificationsEnabled: Boolean,
    val updatedPullRequestsNotificationsEnabled: Boolean,
    val newReleaseNotificationsEnabled: Boolean,
    val updatedReleaseNotificationsEnabled: Boolean,
)
