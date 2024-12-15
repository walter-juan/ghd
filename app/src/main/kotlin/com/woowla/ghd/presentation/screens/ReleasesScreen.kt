package com.woowla.ghd.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.woowla.ghd.domain.entities.SyncResult
import com.woowla.ghd.presentation.app.AppDimens
import com.woowla.ghd.presentation.app.i18n
import com.woowla.ghd.presentation.components.*
import com.woowla.ghd.presentation.decorators.SyncResultDecorator
import com.woowla.ghd.presentation.viewmodels.ReleasesViewModel

object ReleasesScreen {
    @Composable
    fun Content(onSyncResultEntriesClick: (syncResult: SyncResult) -> Unit) {
        val viewModel = viewModel { ReleasesViewModel() }
        val state = viewModel.state.collectAsState().value
        val topBarSubtitle = when(state) {
            is ReleasesViewModel.State.Initializing -> i18n.status_bar_loading
            is ReleasesViewModel.State.Success -> state.syncResultWithEntries?.let { SyncResultDecorator(it) }?.title ?: i18n.status_bar_synchronized_at_unknown
            is ReleasesViewModel.State.Error -> i18n.status_bar_error
        }
        val topBarSubtitleOnClick: (() -> Unit)? = when(state) {
            is ReleasesViewModel.State.Initializing -> null
            is ReleasesViewModel.State.Success -> {
                state.syncResultWithEntries?.let { { onSyncResultEntriesClick.invoke(it.syncResult) } }
            }
            is ReleasesViewModel.State.Error -> null
        }

        ScreenScrollable(
            topBar = {
                TopBar(
                    title = i18n.top_bar_title_releases,
                    subtitle = topBarSubtitle,
                    subtitleOnClick = topBarSubtitleOnClick
                )
            },
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .padding(AppDimens.contentPaddingAllDp)
                    .width(AppDimens.contentWidthDp)
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
                                    modifier = Modifier.width(AppDimens.contentWidthDp)
                                ) {
                                    Text(
                                        text = groupedReleases.groupName,
                                        style = MaterialTheme.typography.titleMedium,
                                    )
                                }
                            }
                            groupedReleases.releases.forEach { release ->
                                ReleaseCard(releaseWithRepo = release)
                            }
                            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp).width(AppDimens.contentWidthDp))
                        }
                    }
                }
            }
        }
    }
}