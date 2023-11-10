package com.woowla.ghd.presentation.screens

import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.woowla.ghd.presentation.app.AppDimens
import com.woowla.ghd.presentation.app.i18n
import com.woowla.ghd.presentation.components.*

class RepoToCheckBulkSampleScreen: Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val onBackClick: (() -> Unit) = { navigator.pop() }

        val horizontalScrollState: ScrollState = rememberScrollState()

        ScreenScrollable(
            topBar = { TopBar(title = i18n.top_bar_title_repos_to_check_bulk_sample, navOnClick = onBackClick) }
        ) {
            Column(
                modifier = Modifier
                    .padding(AppDimens.contentPaddingAllDp)
                    .width(AppDimens.contentWidthDp)
            ) {
                Box(
                    modifier = Modifier.horizontalScroll(horizontalScrollState)
                ) {
                    Text(
                        text = i18n.screen_repos_to_check_bulk_sample_sample_file,
                        fontSize = 18.sp,
                        style = MaterialTheme.typography.overline
                    )
                }
                HorizontalScrollbar(
                    modifier = Modifier.fillMaxWidth(),
                    adapter = rememberScrollbarAdapter(
                        scrollState = horizontalScrollState
                    )
                )
            }
        }
    }
}
