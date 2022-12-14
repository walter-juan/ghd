package com.woowla.ghd.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import com.woowla.ghd.presentation.app.AppDimens
import com.woowla.ghd.presentation.app.i18n
import com.woowla.ghd.presentation.components.*
import com.woowla.ghd.presentation.viewmodels.ReleasesViewModel

class ReleasesScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel = rememberScreenModel { ReleasesViewModel() }
        val state = viewModel.state.collectAsState().value
        val statusBarText = when(state) {
            is ReleasesViewModel.State.Initializing -> i18n.status_bar_loading
            is ReleasesViewModel.State.Success -> i18n.status_bar_synchronized_at(state.synchronizedAt)
            is ReleasesViewModel.State.Error -> i18n.status_bar_error
        }

        ScreenScrollable(
            topBar = { TopBar(title = i18n.top_bar_title_releases) },
            statusBarText = statusBarText,
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .padding(AppDimens.contentPaddingAllDp.dp)
                    .width(AppDimens.contentWidthDp.dp)
            ) {
                when(state) {
                    ReleasesViewModel.State.Initializing -> { }
                    is ReleasesViewModel.State.Error -> {
                        Text(i18n.generic_error)
                    }
                    is ReleasesViewModel.State.Success -> {
                        state.groupedReleases.forEach { groupedReleases ->
                            if (groupedReleases.groupName != null) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.Start),
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.width(AppDimens.contentWidthDp.dp)
                                ) {
                                    Text(
                                        text = groupedReleases.groupName,
                                        style = MaterialTheme.typography.subtitle1,
                                    )
                                }
                            }
                            groupedReleases.releases.forEach { release ->
                                ReleaseCard(release)
                            }
                            Divider(modifier = Modifier.padding(vertical = 10.dp).width(AppDimens.contentWidthDp.dp))
                        }
                    }
                }
            }
        }
    }
}