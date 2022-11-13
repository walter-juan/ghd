package com.woowla.ghd.presentation.app

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.painterResource
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import cafe.adriel.voyager.transitions.SlideTransition
import com.woowla.ghd.presentation.screens.AppSettingsScreen

object TabAppSettings : Tab {
    override val options: TabOptions
        @Composable
        get() {
            val title = i18n.tab_title_settings
            val icon = painterResource(AppIcons.settings)

            return remember {
                TabOptions(
                    index = 3u,
                    title = title,
                    icon = icon
                )
            }
        }

    @OptIn(ExperimentalAnimationApi::class)
    @Composable
    override fun Content() {
        Navigator(AppSettingsScreen()) { navigator ->
            SlideTransition(navigator)
        }
    }
}