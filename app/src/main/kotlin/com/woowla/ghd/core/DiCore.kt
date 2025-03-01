package com.woowla.ghd.core

import androidx.compose.ui.window.TrayState
import com.woowla.ghd.core.eventbus.EventBus
import com.woowla.ghd.core.notifications.NotificationClient
import org.koin.core.module.Module
import org.slf4j.Logger

object DiCore {
    fun module(
        isDebug: Boolean,
        appFolder: String,
        logger: Logger,
    ): Module = org.koin.dsl.module {
        single { EventBus(get()) }
        single { AppLogger(logger = logger) }
        single<AppFolderFactory> { AppFolderFactory(isDebug, appFolder) }
        single<NotificationClient> { NotificationClient(get()) }
        single<TrayState> { TrayState() }
    }
}