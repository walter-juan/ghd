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
import androidx.compose.ui.res.painterResource
import com.woowla.ghd.domain.usecases.GetAppSettingsUseCase
import com.woowla.ghd.eventbus.Event
import com.woowla.ghd.eventbus.EventBus
import com.woowla.ghd.presentation.screens.ReleasesScreen
import com.woowla.ghd.presentation.screens.ComponentsSampleScreen
import com.woowla.ghd.presentation.screens.AboutScreen
import com.woowla.ghd.presentation.screens.PullRequestsScreen
import com.woowla.ghd.presentation.screens.RepoToCheckEditScreen
import com.woowla.ghd.presentation.screens.RepoToCheckScreen
import com.woowla.ghd.presentation.screens.SettingsScreen
import com.woowla.ghd.presentation.viewmodels.RepoToCheckEditViewModel
import kotlinx.coroutines.launch

@Composable
fun App() {
    var selectedAppScreen: AppScreen by remember { mutableStateOf(AppTabScreen.Repos) }
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
            NavigationRail(
                backgroundColor = AppColors.navRailBackground()
            ) {
                val enabled = selectedAppScreen is AppTabScreen
                AppTabScreen.values().forEach { appScreen ->
                    NavigationRailItem(
                        icon = { Icon(painter = painterResource(appScreen.tabIcon), contentDescription = appScreen.tabTitle) },
                        label = { Text(appScreen.tabTitle) },
                        enabled = enabled,
                        selected = selectedAppScreen == appScreen,
                        onClick = { selectedAppScreen = appScreen },
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
            }

            val selectedAppScreenLocked = selectedAppScreen
            when (selectedAppScreenLocked) {
                AppTabScreen.Pulls -> {
                    PullRequestsScreen()
                }
                AppTabScreen.Releases -> {
                    ReleasesScreen()
                }
                AppTabScreen.Repos -> {
                    RepoToCheckScreen(
                        onEditRepoClick = { selectedAppScreen = AppFullScreen.RepoEdit(repoToCheck = it) },
                        onAddNewRepoClick = { selectedAppScreen = AppFullScreen.RepoEdit(repoToCheck = null) }
                    )
                }
                AppTabScreen.Settings -> {
                    SettingsScreen()
                }
                AppTabScreen.About -> {
                    AboutScreen(
                        onComponentsSampleScreenClick = { selectedAppScreen = AppFullScreen.ComponentsSample },
                    )
                }
                AppFullScreen.ComponentsSample -> {
                    ComponentsSampleScreen(
                        onBackClick = { selectedAppScreen = AppTabScreen.About }
                    )
                }
                is AppFullScreen.RepoEdit -> {
                    RepoToCheckEditScreen(
                        viewModel = RepoToCheckEditViewModel(repoToCheck = selectedAppScreenLocked.repoToCheck),
                        onBackClick = { selectedAppScreen = AppTabScreen.Repos }
                    )
                }
            }
        }
    }
}