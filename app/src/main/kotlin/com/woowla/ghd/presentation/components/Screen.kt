package com.woowla.ghd.presentation.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.FabPosition
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.woowla.ghd.presentation.app.AppColors

@Composable
fun ScreenScrollable(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    statusBarText: String? = null,
    topBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    isFloatingActionButtonDocked: Boolean = false,
    scrollState: ScrollState = rememberScrollState(),
    content: @Composable () -> Unit,
) {
    ScreenScaffold(
        scaffoldState = scaffoldState,
        statusBarText = statusBarText,
        topBar = topBar,
        floatingActionButton = floatingActionButton,
        floatingActionButtonPosition = floatingActionButtonPosition,
        isFloatingActionButtonDocked = isFloatingActionButtonDocked,
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
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    statusBarText: String? = null,
    topBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    isFloatingActionButtonDocked: Boolean = false,
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = topBar,
        floatingActionButton = floatingActionButton,
        floatingActionButtonPosition = floatingActionButtonPosition,
        isFloatingActionButtonDocked = isFloatingActionButtonDocked,
        content = content,
        bottomBar = {
            if (statusBarText != null) {
                StatusBar(text = statusBarText)
            }
        }
    )
}

@Composable
fun TopBar(
    title: String,
    navImageVector: ImageVector = Icons.Filled.ArrowBack,
    navContentDescription: String? = null,
    navOnClick: (() -> Unit)? = null,
    actions: @Composable @ExtensionFunctionType RowScope.() -> Unit = {},
) {
    TopAppBar(
        title = { Text(
            text = title,
            style = MaterialTheme.typography.h4,
        ) },
        navigationIcon = {
            if (navOnClick != null) {
                IconButton(onClick = navOnClick) {
                    Icon(navImageVector, navContentDescription)
                }
            }
        },
        backgroundColor = AppColors.topBarBackground(),
        contentColor = AppColors.topBarContent(),
        actions = actions,
        elevation = 0.dp
    )
}

@Composable
fun StatusBar(text: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppColors.statusBarBackground())
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.caption,
        )
    }
}
