package com.woowla.ghd.presentation.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.woowla.ghd.BuildConfig

object AppIconsPainter
object AppIconsRes

public val AppIconsPainter.Launcher: Painter
    @Composable
    get() = painterResource(AppIconsRes.Launcher)

public val AppIconsPainter.TrayIcon: Painter
    @Composable
    get() = painterResource(AppIconsRes.TrayIcon)

public val AppIconsPainter.Placeholder: Painter
    @Composable
    get() = painterResource(AppIconsRes.Placeholder)

public val AppIconsRes.Launcher: String
    get() = if (BuildConfig.DEBUG) {
        "icons/ic_launcher_debug.svg"
    } else {
        "icons/ic_launcher.svg"
    }

public val AppIconsRes.TrayIcon: String
    get() = if (BuildConfig.DEBUG) {
        "icons/ic_tray_debug.svg"
    } else {
        "icons/ic_tray.svg"
    }

public val AppIconsRes.Placeholder: String
    get() = "icons/placeholder.png"
