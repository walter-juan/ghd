package com.woowla.ghd.core.notifications

import androidx.compose.ui.window.Notification
import androidx.compose.ui.window.TrayState
import io.kotest.core.spec.style.StringSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class NotificationClientUnitTest : StringSpec({
    "sendNotification dispatches a Compose notification through the injected tray state" {
        val trayState = mockk<TrayState>()
        every { trayState.sendNotification(any()) } returns Unit
        val client = NotificationClient(trayState)

        client.sendNotification("Title", "Message", NotificationType.INFO)

        verify { trayState.sendNotification(Notification("Title", "Message", Notification.Type.Info)) }
    }
})
