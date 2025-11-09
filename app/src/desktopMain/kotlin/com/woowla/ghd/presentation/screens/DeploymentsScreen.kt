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
import com.woowla.ghd.domain.entities.DeploymentWithRepo
import com.woowla.ghd.domain.entities.SyncResult
import com.woowla.ghd.presentation.i18nUi
import com.woowla.ghd.presentation.app.AppDimens
import com.woowla.ghd.presentation.components.DeploymentCard
import com.woowla.ghd.presentation.components.EmptyComponent
import com.woowla.ghd.presentation.components.ErrorComponent
import com.woowla.ghd.presentation.components.ScreenScrollable
import com.woowla.ghd.presentation.components.TopBar
import com.woowla.ghd.presentation.decorators.SyncResultDecorator
import com.woowla.ghd.presentation.viewmodels.DeploymentsStateMachine
import com.woowla.ghd.presentation.viewmodels.DeploymentsViewModel

object DeploymentsScreen {
    @Composable
    fun Content(
        viewModel: DeploymentsViewModel,
        onSyncResultEntriesClick: (SyncResult) -> Unit
    ) {
        val state = viewModel.state.collectAsState().value

        ScreenScrollable(
            topBar = {
                TopBar(
                    title = i18nUi.top_bar_title_deployments,
                    subtitle = {
                        TopBarSubtitle(
                            state = state,
                            onSyncResultEntriesClick = onSyncResultEntriesClick
                        )
                    },
                )
            },
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .padding(AppDimens.screenPadding)
                    .fillMaxWidth()
            ) {
                when (state) {
                    null, DeploymentsStateMachine.St.Initializing -> {
                        // do not show a loading because is shown only some milliseconds
                    }
                    is DeploymentsStateMachine.St.Error -> {
                        ErrorComponent()
                    }
                    is DeploymentsStateMachine.St.Success -> {
                        if (state.deployments.isEmpty()) {
                            EmptyComponent()
                        } else {
                            ListDeployments(state.deployments)
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun TopBarSubtitle(state: DeploymentsStateMachine.St?, onSyncResultEntriesClick: (SyncResult) -> Unit) {
        if (state is DeploymentsStateMachine.St.Success && state.syncResultWithEntries != null) {
            SyncResultDecorator(state.syncResultWithEntries).TitleWithDate(
                onClick = { onSyncResultEntriesClick.invoke(state.syncResultWithEntries.syncResult) }
            )
        }
    }

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    private fun ListDeployments(deployments: List<DeploymentWithRepo>) {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(
                space = AppDimens.cardHorizontalSpaceBetween,
                alignment = Alignment.CenterHorizontally
            ),
            verticalArrangement = Arrangement.spacedBy(AppDimens.cardVerticalSpaceBetween),
            maxItemsInEachRow = 2
        ) {
            deployments.forEach { deployment ->
                DeploymentCard(
                    deploymentWithRepo = deployment,
                    modifier = Modifier.sizeIn(maxWidth = AppDimens.cardMaxWidth).fillMaxWidth(),
                )
            }
        }
    }
}
