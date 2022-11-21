package com.woowla.ghd.presentation.app

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import com.woowla.ghd.domain.usecases.GetAppSettingsUseCase
import com.woowla.ghd.eventbus.Event
import com.woowla.ghd.eventbus.EventBus
import com.woowla.ghd.presentation.screens.SplashScreen
import kotlinx.coroutines.launch

@Composable
fun App() {
    val systemDarkTheme = isSystemInDarkTheme()
    var darkTheme by remember { mutableStateOf(systemDarkTheme) }

    LaunchedEffect("app-theme") {
        GetAppSettingsUseCase().execute().onSuccess { darkTheme = it.darkTheme ?: systemDarkTheme }
        EventBus.subscribe("app-subscriber", this, Event.SETTINGS_UPDATED) {
            launch {
                GetAppSettingsUseCase().execute().onSuccess { darkTheme = it.darkTheme ?: systemDarkTheme }
            }
        }
    }

    AppTheme(darkTheme = darkTheme) {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            Navigator(SplashScreen())
        }
    }
}
