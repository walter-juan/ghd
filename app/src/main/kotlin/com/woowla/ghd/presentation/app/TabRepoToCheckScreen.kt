package com.woowla.ghd.presentation.app

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.painterResource
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import cafe.adriel.voyager.transitions.SlideTransition
import com.woowla.ghd.presentation.screens.RepoToCheckScreen

object TabRepoToCheckScreen : Tab {
    override val options: TabOptions
        @Composable
        get() {
            val title = i18n.tab_title_repos_to_check
            val icon = painterResource(AppIcons.repository)

            return remember {
                TabOptions(
                    index = 2u,
                    title = title,
                    icon = icon
                )
            }
        }

    @OptIn(ExperimentalAnimationApi::class)
    @Composable
    override fun Content() {
        Navigator(RepoToCheckScreen()) { navigator ->
            SlideTransition(navigator)
        }
    }
}