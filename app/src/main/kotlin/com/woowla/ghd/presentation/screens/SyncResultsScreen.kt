package com.woowla.ghd.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.woowla.ghd.domain.entities.SyncResult
import com.woowla.ghd.domain.entities.SyncResultWithEntitiesAndRepos
import com.woowla.ghd.presentation.app.AppDimens
import com.woowla.ghd.presentation.app.i18n
import com.woowla.ghd.presentation.components.CardListItem
import com.woowla.ghd.presentation.components.ScreenScrollable
import com.woowla.ghd.presentation.components.TopBar
import com.woowla.ghd.presentation.decorators.SyncResultDecorator
import com.woowla.ghd.presentation.viewmodels.SyncResultsViewModel

object SyncResultsScreen {
    @Composable
    fun Content(
        onBackClick: () -> Unit,
        onSyncResultEntriesClick: (syncResult: SyncResult) -> Unit,
    ) {
        val viewModel = viewModel { SyncResultsViewModel() }

        val state by viewModel.state.collectAsState()

        ScreenScrollable(
            topBar = { TopBar(title = i18n.top_bar_title_synchronization_results, navOnClick = onBackClick) }
        ) {
            Column(
                modifier = Modifier
                    .padding(AppDimens.contentPaddingAllDp)
                    .width(AppDimens.contentWidthDp)
            ) {
                val lockedState = state
                when(lockedState) {
                    SyncResultsViewModel.State.Initializing -> {
                        Text(i18n.generic_loading)
                    }
                    is SyncResultsViewModel.State.Error -> {
                        Text(i18n.generic_error)
                    }
                    is SyncResultsViewModel.State.Success -> {
                        lockedState.syncResultWithEntries.forEach { syncResultEntry ->
                            SynResult(syncResultEntry) {
                                onSyncResultEntriesClick.invoke(syncResultEntry.syncResult)
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun SynResult(syncResultWithEntries: SyncResultWithEntitiesAndRepos, onClick: () -> Unit) {
        SynResultCard(syncResultWithEntries, onClick)
        Spacer(modifier = Modifier.height(15.dp))
    }

    @Composable
    private fun SynResultCard(syncResultWithEntries: SyncResultWithEntitiesAndRepos, onClick: () -> Unit) {
        val decorator = SyncResultDecorator(syncResultWithEntries)
        val overlineText = if (syncResultWithEntries.syncResult.duration == null) {
            i18n.screen_sync_results_in_progress
        } else {
            i18n.screen_sync_results_took_seconds((syncResultWithEntries.syncResult.duration.inWholeMilliseconds / 1000.0))
        }
        val headlineText = i18n.screen_sync_results_start_at(syncResultWithEntries.syncResult.startAt)
        val supportingText = if (syncResultWithEntries.syncResult.endAt == null) {
            ""
        } else {
            i18n.screen_sync_results_end_at(decorator.emoji, syncResultWithEntries.errorPercentage, syncResultWithEntries.entriesSize)
        }
        CardListItem(
            overlineText = {
                Text(overlineText)
            },
            headlineText = {
                Text(headlineText)
            },
            supportingText = {
                Text(supportingText)
            },
            onClick = onClick,
        )
    }
}