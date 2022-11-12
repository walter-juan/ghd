package com.woowla.ghd.presentation.app

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.NavigationRail
import androidx.compose.material.NavigationRailItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.woowla.ghd.domain.usecases.GetAppSettingsUseCase
import com.woowla.ghd.eventbus.Event
import com.woowla.ghd.eventbus.EventBus
import kotlinx.coroutines.launch

@Composable
fun App() {
    val systemDarkTheme = isSystemInDarkTheme()
    var darkTheme by remember { mutableStateOf(systemDarkTheme) }

    LaunchedEffect("app-theme") {
        GetAppSettingsUseCase().execute().onSuccess { darkTheme = it.appDarkTheme ?: systemDarkTheme }
        EventBus.subscribe("subscriber", this, Event.APP_SETTINGS_UPDATED) {
            launch {
                GetAppSettingsUseCase().execute().onSuccess { darkTheme = it.appDarkTheme ?: systemDarkTheme }
            }
        }
    }

    AppTheme(darkTheme = darkTheme) {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            TabNavigator(TabAboutScreen) {
                NavigationRail(
                    backgroundColor = AppColors.navRailBackground()
                ) {
                    TabNavigationRailItem(TabPullRequestsScreen)
                    TabNavigationRailItem(TabReleasesScreen)
                    TabNavigationRailItem(TabRepoToCheckScreen)
                    TabNavigationRailItem(TabAppSettings)
                    TabNavigationRailItem(TabAboutScreen)
                }

                CurrentTab()
            }
        }
    }
}

@Composable
private fun TabNavigationRailItem(tab: Tab) {
    val tabNavigator = LocalTabNavigator.current
    val enabled = true

    NavigationRailItem(
        icon = { Icon(painter = tab.options.icon!!, contentDescription = tab.options.title) },
        label = { Text(tab.options.title) },
        enabled = enabled,
        selected = tabNavigator.current.key == tab.key,
        onClick = { tabNavigator.current = tab },
        alwaysShowLabel = false,
        selectedContentColor = if (enabled) { MaterialTheme.colors.primary } else {
            AppColors.navRailBackground()
        },
        unselectedContentColor = if (enabled) {
            AppColors.navRailItemUnselectedContentColor()
        } else {
            AppColors.navRailBackground()
        },
    )
}
