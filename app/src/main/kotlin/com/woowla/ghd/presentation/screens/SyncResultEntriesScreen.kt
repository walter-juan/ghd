package com.woowla.ghd.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.woowla.ghd.domain.entities.SyncResult
import com.woowla.ghd.domain.entities.SyncResultEntry
import com.woowla.ghd.presentation.app.AppDimens
import com.woowla.ghd.presentation.app.i18n
import com.woowla.ghd.presentation.components.ScreenScrollable
import com.woowla.ghd.presentation.components.TopBar
import com.woowla.ghd.presentation.decorators.RepoToCheckDecorator
import com.woowla.ghd.presentation.decorators.SyncResultDecorator
import com.woowla.ghd.presentation.decorators.SyncResultEntryDecorator
import com.woowla.ghd.presentation.viewmodels.SyncResultEntriesViewModel

class SyncResultEntriesScreen(
    private val syncResult: SyncResult,
) : Screen {
    // to solve a crash, see https://github.com/adrielcafe/voyager/issues/197
    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val viewModel = rememberScreenModel { SyncResultEntriesViewModel(syncResult) }
        val navigator = LocalNavigator.currentOrThrow
        val onBackClick: (() -> Unit) = { navigator.pop() }

        val state = viewModel.state.collectAsState().value
        val topBarSubtitle = when(state) {
            is SyncResultEntriesViewModel.State.Initializing -> i18n.status_bar_loading
            is SyncResultEntriesViewModel.State.Error -> i18n.status_bar_error
            is SyncResultEntriesViewModel.State.Success -> {
                val decorator = SyncResultDecorator(state.syncResult)
                i18n.top_bar_subtitle_synchronization_result_entries(decorator.emoji, state.syncResult.errorPercentage, state.syncResult.entriesSize)
            }
        }

        ScreenScrollable(
            topBar = {
                TopBar(
                    title = i18n.top_bar_title_synchronization_result_entries,
                    subtitle = topBarSubtitle,
                    navOnClick = onBackClick
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .padding(AppDimens.contentPaddingAllDp)
                    .width(AppDimens.contentWidthDp)
            ) {
                val lockedState = state
                when(lockedState) {
                    SyncResultEntriesViewModel.State.Initializing -> {
                        Text(i18n.generic_loading)
                    }
                    is SyncResultEntriesViewModel.State.Error -> {
                        Text(i18n.generic_error)
                    }
                    is SyncResultEntriesViewModel.State.Success -> {
                        lockedState.syncResultEntries.forEach { syncResultEntry ->
                            SynResultEntry(syncResultEntry)
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun SynResultEntry(syncResultEntry: SyncResultEntry) {
        val decorator = SyncResultEntryDecorator(syncResultEntry)

        Row(Modifier.fillMaxWidth()) {
            Text(
                text = decorator.emoji,
                Modifier.weight(0.05F).padding(horizontal = 8.dp)
            )
            Text(
                text = syncResultEntry.origin.toString(),
                Modifier.weight(0.15F).padding(horizontal = 8.dp)
            )
            Text(
                text = syncResultEntry.repoToCheck?.let { RepoToCheckDecorator(it) }?.fullRepo ?: "",
                Modifier.weight(0.7F).padding(horizontal = 8.dp)
            )
        }
        Row(Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.weight(0.05F))
            Text(
                text = i18n.screen_sync_result_entries_took_seconds((syncResultEntry.duration.inWholeMilliseconds / 1000.0)),
                Modifier.weight(0.95F).padding(horizontal = 8.dp),
                style = MaterialTheme.typography.bodySmall,
            )
        }
        if (syncResultEntry is SyncResultEntry.Error) {
            Row(Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.weight(0.05F))
                Text(
                    text = syncResultEntry.errorMessage ?: "",
                    Modifier.weight(0.95F).padding(horizontal = 8.dp),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
        Spacer(modifier = Modifier.height(15.dp))
    }
}