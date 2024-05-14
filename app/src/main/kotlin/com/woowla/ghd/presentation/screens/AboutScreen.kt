package com.woowla.ghd.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.woowla.ghd.AppFolderFactory
import com.woowla.ghd.BuildConfig
import com.woowla.ghd.presentation.app.AppDimens
import com.woowla.ghd.presentation.app.i18n
import com.woowla.ghd.presentation.components.*
import java.nio.file.Path

object AboutScreen {
    @Composable
    fun Content(
        appDir: Path = AppFolderFactory.folder,
        onBackClick: (() -> Unit)? = null,
        onComponentsSampleScreenClick: (() -> Unit),
    ) {
        ScreenScrollable(
            topBar = {
                TopBar(
                    title = i18n.top_bar_title_about,
                    navOnClick = onBackClick
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .padding(AppDimens.contentPaddingAllDp)
                    .width(AppDimens.contentWidthDp)
            ) {
                SectionCategory("Regarding ghd") {
                    SectionItem(
                        title = "What is it?",
                        description = "This app will show your pull requests and GitHub releases"
                    )
                    SectionItem(
                        title = "Version",
                        description = "This app version is '${BuildConfig.APP_VERSION}'"
                    )
                    SelectionContainer {
                        SectionItem(
                            title = "Application directory",
                            description = "This application stores some data into '$appDir' directory"
                        )
                    }
                    SelectionContainer {
                        SectionItem(
                            title = "Icons",
                            description = "The icons used are Tabler (https://github.com/tabler/tabler-icons)"
                        )
                    }
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