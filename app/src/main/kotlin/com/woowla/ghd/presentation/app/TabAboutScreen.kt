package com.woowla.ghd.presentation.app

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.painterResource
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import cafe.adriel.voyager.transitions.FadeTransition
import cafe.adriel.voyager.transitions.ScaleTransition
import cafe.adriel.voyager.transitions.SlideTransition
import com.woowla.ghd.presentation.screens.AboutScreen

object TabAboutScreen : Tab {
    override val options: TabOptions
        @Composable
        get() {
            val title = i18n.tab_title_about
            val icon = painterResource(AppIcons.infoEmpty)

            return remember {
                TabOptions(
                    index = 4u,
                    title = title,
                    icon = icon
                )
            }
        }

    @OptIn(ExperimentalAnimationApi::class)
    @Composable
    override fun Content() {
        Navigator(AboutScreen()) { navigator ->
            SlideTransition(navigator)
        }
    }
}