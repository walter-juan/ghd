package com.woowla.ghd.presentation.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.useResource
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.produceLibraries
import com.woowla.ghd.presentation.i18nUi
import com.woowla.ghd.presentation.components.TopBar
import com.woowla.ghd.core.utils.openWebpage

object AboutLibraries {
    @Composable
    fun Content(
        jsonFileName: String,
        onBackClick: (() -> Unit)? = null,
    ) {
        Scaffold(
            topBar = {
                TopBar(
                    title = i18nUi.top_bar_title_about_libraries,
                    navOnClick = onBackClick
                )
            }
        ) { paddingValues ->
            val libraries by produceLibraries {
                useResource(jsonFileName) { res -> res.bufferedReader().readText() }
            }
            LibrariesContainer(
                libraries = libraries,
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                onLibraryClick = { library ->
                    (library.website ?: library.scm?.url)?.let(::openWebpage)
                },
            )
        }
    }
}