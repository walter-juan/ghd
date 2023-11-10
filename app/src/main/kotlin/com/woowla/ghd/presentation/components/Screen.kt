package com.woowla.ghd.presentation.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.woowla.compose.remixicon.ArrowsArrowLeftLine
import com.woowla.compose.remixicon.RemixiconPainter

@Composable
fun ScreenScrollable(
    snackbarHost: @Composable () -> Unit = {},
    topBar: @Composable () -> Unit = {},
    scrollState: ScrollState = rememberScrollState(),
    content: @Composable () -> Unit,
) {
    ScreenScaffold(
        topBar = topBar,
        snackbarHost = snackbarHost,
    ) { scaffoldPaddingValues ->
        Box(
            contentAlignment = Alignment.TopCenter
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(scaffoldPaddingValues)
                    .verticalScroll(scrollState)
                    .fillMaxWidth()
            ) {
                content()
            }
            VerticalScrollbar(
                modifier = Modifier
                    .padding(scaffoldPaddingValues)
                    .fillMaxHeight()
                    .align(Alignment.CenterEnd),
                adapter = rememberScrollbarAdapter(
                    scrollState = scrollState
                )
            )
        }
    }
}

@Composable
fun ScreenScaffold(
    topBar: @Composable () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        topBar = topBar,
        snackbarHost = snackbarHost,
        content = { paddingValues ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                content(paddingValues)
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    title: String,
    subtitle: String? = null,
    subtitleOnClick: (() -> Unit)? = null,
    navImagePainter: Painter = RemixiconPainter.ArrowsArrowLeftLine,
    navContentDescription: String? = null,
    navOnClick: (() -> Unit)? = null,
    actions: @Composable @ExtensionFunctionType RowScope.() -> Unit = {},
) {
    TopAppBar(
        title = {
            Column {
                Text(text = title)
                Text(
                    text = subtitle ?: "",
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.clickable(enabled = subtitleOnClick != null, onClick = { subtitleOnClick?.invoke() })
                )
            }
        },
        navigationIcon = {
            if (navOnClick != null) {
                IconButton(onClick = navOnClick) {
                    Icon(navImagePainter, navContentDescription, Modifier.size(25.dp))
                }
            }
        },
        actions = actions,
    )
}