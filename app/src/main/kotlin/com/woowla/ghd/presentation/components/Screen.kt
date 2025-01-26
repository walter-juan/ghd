package com.woowla.ghd.presentation.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.woowla.compose.icon.collections.tabler.Tabler
import com.woowla.compose.icon.collections.tabler.tabler.Outline
import com.woowla.compose.icon.collections.tabler.tabler.outline.ChevronLeft
import com.woowla.ghd.presentation.app.AppDimens

@Composable
fun ScreenScrollable(
    snackbarHost: @Composable () -> Unit = {},
    topBar: @Composable () -> Unit = {},
    scrollState: ScrollState = rememberScrollState(),
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Scaffold(
        topBar = topBar,
        snackbarHost = snackbarHost,
        modifier = modifier,
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
                Column(
                    modifier = Modifier.widthIn(max = AppDimens.screenMaxWidth)
                ) {
                    content()
                }
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
fun TopBar(
    title: String,
    subtitle: String? = null,
    onSubtitleClick: (() -> Unit)? = null,
    navImage: ImageVector = Tabler.Outline.ChevronLeft,
    navContentDescription: String? = null,
    navOnClick: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
) {
    TopBar(
        title = title,
        subtitle = {
            Text(
                text = subtitle ?: "",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.clickable(enabled = onSubtitleClick != null, onClick = { onSubtitleClick?.invoke() })
            )
        },
        navImage = navImage,
        navContentDescription = navContentDescription,
        navOnClick = navOnClick,
        actions = actions
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    title: String,
    subtitle: (@Composable () -> Unit)? = null,
    navImage: ImageVector = Tabler.Outline.ChevronLeft,
    navContentDescription: String? = null,
    navOnClick: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
) {
    CenterAlignedTopAppBar(
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(2.dp))
                CompositionLocalProvider(
                    LocalTextStyle provides MaterialTheme.typography.bodySmall,
                    content = if (subtitle == null) {
                        { Text("") }
                    } else {
                        subtitle
                    }
                )
            }
        },
        navigationIcon = {
            if (navOnClick != null) {
                IconButton(onClick = navOnClick) {
                    Icon(navImage, navContentDescription, Modifier.size(25.dp))
                }
            } else {
                IconButton(onClick = {}, enabled = false) {
                    Box(modifier = Modifier.size(25.dp)) {}
                }
            }
        },
        actions = actions,
    )
}