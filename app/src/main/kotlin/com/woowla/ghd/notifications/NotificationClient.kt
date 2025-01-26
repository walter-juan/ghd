package com.woowla.ghd.notifications

import androidx.compose.ui.window.Notification
import androidx.compose.ui.window.TrayState

class NotificationClient(private val trayState: TrayState) {
    fun sendNotification(title: String, message: String, type: NotificationType) {
        val notification = Notification(
            title = title,
            message = message,
            type = when(type) {
                NotificationType.NONE -> Notification.Type.None
                NotificationType.INFO -> Notification.Type.Info
                NotificationType.WARNING -> Notification.Type.Warning
                NotificationType.ERROR -> Notification.Type.Error
            }
        )
        trayState.sendNotification(notification)
    }
}