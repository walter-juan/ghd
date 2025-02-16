package com.woowla.ghd.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.woowla.ghd.domain.entities.SyncResult
import com.woowla.ghd.domain.entities.SyncResultWithEntriesAndRepos
import com.woowla.ghd.i18n
import com.woowla.ghd.presentation.app.AppDimens
import com.woowla.ghd.presentation.components.EmptyComponent
import com.woowla.ghd.presentation.components.ErrorComponent
import com.woowla.ghd.presentation.components.LoadingComponent
import com.woowla.ghd.presentation.components.ScreenScrollable
import com.woowla.ghd.presentation.components.SynResultCard
import com.woowla.ghd.presentation.components.TopBar
import com.woowla.ghd.presentation.viewmodels.SyncResultsStateMachine
import com.woowla.ghd.presentation.viewmodels.SyncResultsViewModel

object SyncResultsScreen {
    @Composable
    fun Content(
        viewModel: SyncResultsViewModel,
        onBackClick: () -> Unit,
        onSyncResultEntriesClick: (syncResult: SyncResult) -> Unit,
    ) {
        val state = viewModel.state.collectAsState().value

        ScreenScrollable(
            topBar = {
                TopBar(title = i18n.top_bar_title_synchronization_results, navOnClick = onBackClick)
            }
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .padding(AppDimens.screenPadding)
                    .fillMaxWidth()
            ) {
                when (state) {
                    null, SyncResultsStateMachine.St.Initializing -> {
                        LoadingComponent()
                    }
                    is SyncResultsStateMachine.St.Error -> {
                        ErrorComponent()
                    }
                    is SyncResultsStateMachine.St.Success -> {
                        if (state.syncResultWithEntries.isEmpty()) {
                            EmptyComponent()
                        } else {
                            ListSyncResult(
                                syncResultWithEntries = state.syncResultWithEntries,
                                onSyncResultClick = onSyncResultEntriesClick
                            )
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    private fun ListSyncResult(
        syncResultWithEntries: List<SyncResultWithEntriesAndRepos>,
        onSyncResultClick: (SyncResult) -> Unit,
    ) {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(space = AppDimens.cardHorizontalSpaceBetween, alignment = Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(AppDimens.cardVerticalSpaceBetween),
            maxItemsInEachRow = 2
        ) {
            syncResultWithEntries.forEach { syncResultEntry ->
                SynResultCard(
                    syncResultWithEntries = syncResultEntry,
                    onClick = {
                        onSyncResultClick.invoke(syncResultEntry.syncResult)
                    },
                    modifier = Modifier.sizeIn(maxWidth = AppDimens.cardMaxWidth).fillMaxWidth(),
                )
            }
        }
    }
}