package com.woowla.ghd.presentation.screens

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.woowla.ghd.AppFolderFactory
import com.woowla.ghd.BuildConfig
import com.woowla.ghd.presentation.app.i18n
import com.woowla.ghd.presentation.components.Screen
import com.woowla.ghd.presentation.components.SectionCategory
import com.woowla.ghd.presentation.components.SectionItem
import com.woowla.ghd.presentation.components.TopBar
import java.nio.file.Path

class AboutScreen(
    private val appDir: Path = AppFolderFactory.folder,
    val onBackClick: (() -> Unit)? = null,
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val onComponentsSampleScreenClick = { navigator.push(ComponentsSampleScreen()) }

        Screen(
            topBar = {
                TopBar(
                    title = i18n.top_bar_title_about,
                    navOnClick = onBackClick
                )
            }
        ) {
            item {
                SectionCategory("Regarding ghd") {
                    SectionItem(
                        title = "What is it?",
                        description = "This app will show your pull requests and GitHub releases"
                    )
                    SectionItem(
                        title = "Version",
                        description = "This app version is '${BuildConfig.APP_VERSION}'"
                    )
                    SectionItem(
                        title = "Application directory",
                        description = "This application stores some data into '$appDir' directory"
                    )
                }
                if (BuildConfig.DEBUG) {
                    SectionCategory("Debug only sections") {
                        SectionItem(
                            title = "Components",
                            description = "Sample screen with this app theme with typographies, components, etc..."
                        ) {
                            Button(onClick = onComponentsSampleScreenClick) {
                                Text("open components screen sample")
                            }
                        }
                    }
                }
            }
        }
    }
}