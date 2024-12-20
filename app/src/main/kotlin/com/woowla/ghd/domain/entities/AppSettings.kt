package com.woowla.ghd.domain.entities

import arrow.optics.optics

@optics data class AppSettings(
    val darkTheme: Boolean?,
    val notificationsSettings: NotificationsSettings
) {
    companion object {}
}
