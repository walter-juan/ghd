package com.woowla.ghd.core.notifications

import androidx.compose.ui.window.Notification
import androidx.compose.ui.window.TrayState
import com.woowla.ghd.core.AppLogger

class NotificationClient(
    private val trayState: TrayState,
    private val appLogger: AppLogger,
) {
    fun sendNotification(title: String, message: String, type: NotificationType) {
        val notification = Notification(
            title = title,
            message = message,
            type = when (type) {
                NotificationType.NONE -> Notification.Type.None
                NotificationType.INFO -> Notification.Type.Info
                NotificationType.WARNING -> Notification.Type.Warning
                NotificationType.ERROR -> Notification.Type.Error
            }
        )
        trayState.sendNotification(notification)
        appLogger.d("Notification :: dispatch :: type=$type :: result=accepted-by-compose-tray")
    }
}