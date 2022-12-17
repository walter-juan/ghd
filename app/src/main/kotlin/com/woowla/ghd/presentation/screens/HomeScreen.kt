package com.woowla.ghd.presentation.screens

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.NavigationRail
import androidx.compose.material.NavigationRailItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.painterResource
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import cafe.adriel.voyager.navigator.tab.TabOptions
import cafe.adriel.voyager.transitions.SlideTransition
import com.woowla.ghd.presentation.app.AppColors
import com.woowla.ghd.presentation.app.AppIcons
import com.woowla.ghd.presentation.app.i18n

class HomeScreen : Screen {
    @Composable
    override fun Content() {
        TabNavigator(TabPullRequestsScreen) {
            NavigationRail(
                backgroundColor = AppColors.navRailBackground()
            ) {
                TabNavigationRailItem(TabPullRequestsScreen)
                TabNavigationRailItem(TabReleasesScreen)
                TabNavigationRailItem(TabRepoToCheckScreen)
                TabNavigationRailItem(TabSettings)
                TabNavigationRailItem(TabAboutScreen)
            }
            CurrentTab()
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

    private object TabPullRequestsScreen : Tab {
        override val options: TabOptions
            @Composable
            get() {
                val title = i18n.tab_title_pull_requests
                val icon = painterResource(AppIcons.gitPullRequest)
                return remember { TabOptions(index = 1u, title = title, icon = icon) }
            }

        @OptIn(ExperimentalAnimationApi::class)
        @Composable
        override fun Content() {
            Navigator(PullRequestsScreen()) { navigator -> SlideTransition(navigator) }
        }
    }

    private object TabReleasesScreen : Tab {
        override val options: TabOptions
            @Composable
            get() {
                val title = i18n.tab_title_releases
                val icon = painterResource(AppIcons.packages)
                return remember { TabOptions(index = 2u, title = title, icon = icon) }
            }

        @OptIn(ExperimentalAnimationApi::class)
        @Composable
        override fun Content() {
            Navigator(ReleasesScreen()) { navigator -> SlideTransition(navigator) }
        }
    }

    private object TabRepoToCheckScreen : Tab {
        override val options: TabOptions
            @Composable
            get() {
                val title = i18n.tab_title_repos_to_check
                val icon = painterResource(AppIcons.repository)
                return remember { TabOptions(index = 3u, title = title, icon = icon) }
            }

        @OptIn(ExperimentalAnimationApi::class)
        @Composable
        override fun Content() {
            Navigator(RepoToCheckScreen()) { navigator -> SlideTransition(navigator) }
        }
    }

    private object TabSettings : Tab {
        override val options: TabOptions
            @Composable
            get() {
                val title = i18n.tab_title_settings
                val icon = painterResource(AppIcons.settings)
                return remember { TabOptions(index = 4u, title = title, icon = icon) }
            }

        @OptIn(ExperimentalAnimationApi::class)
        @Composable
        override fun Content() {
            Navigator(SettingsScreen()) { navigator -> SlideTransition(navigator) }
        }
    }

    private object TabAboutScreen : Tab {
        override val options: TabOptions
            @Composable
            get() {
                val title = i18n.tab_title_about
                val icon = painterResource(AppIcons.infoEmpty)
                return remember { TabOptions(index = 5u, title = title, icon = icon) }
            }

        @OptIn(ExperimentalAnimationApi::class)
        @Composable
        override fun Content() {
            Navigator(AboutScreen()) { navigator -> SlideTransition(navigator) }
        }
    }
}