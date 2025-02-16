package com.woowla.ghd

import androidx.compose.ui.window.TrayState
import com.woowla.ghd.eventbus.EventBus
import com.woowla.ghd.notifications.NotificationClient
import org.koin.core.module.Module
import org.koin.dsl.module
import org.slf4j.Logger

object DiCore {
    fun module(
        isDebug: Boolean,
        appFolder: String,
        logger: Logger,
    ): Module = module {
        single { EventBus(get()) }
        single { AppLogger(logger = logger) }
        single<AppFolderFactory> { AppFolderFactory(isDebug, appFolder) }
        single<NotificationClient> { NotificationClient(get()) }
        single<TrayState> { TrayState() }
    }
}
