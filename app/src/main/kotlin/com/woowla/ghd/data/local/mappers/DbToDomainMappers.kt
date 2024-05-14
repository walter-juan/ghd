package com.woowla.ghd.data.local.mappers

import com.woowla.ghd.data.local.prop.AppProperties
import com.woowla.ghd.domain.entities.AppSettings

fun AppProperties.toAppSettings(): AppSettings {
    return AppSettings(
        darkTheme = darkTheme,
        newPullRequestsNotificationsEnabled = newPullRequestsNotificationsEnabled,
        updatedPullRequestsNotificationsEnabled = updatedPullRequestsNotificationsEnabled,
        newReleaseNotificationsEnabled = newReleaseNotificationsEnabled,
        updatedReleaseNotificationsEnabled = updatedReleaseNotificationsEnabled
    )
}
