package com.woowla.ghd.presentation.viewmodels

import androidx.lifecycle.viewModelScope
import com.freeletics.flowredux.dsl.ChangedState
import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import com.freeletics.flowredux.dsl.State
import com.woowla.ghd.domain.entities.AppSettings
import com.woowla.ghd.domain.entities.SyncResultWithEntriesAndRepos
import com.woowla.ghd.domain.services.AppSettingsService
import com.woowla.ghd.domain.synchronization.Synchronizer
import com.woowla.ghd.domain.entities.Event
import com.woowla.ghd.core.eventbus.EventBus
import com.woowla.ghd.core.utils.FlowReduxViewModel
import com.woowla.ghd.domain.entities.DeploymentWithRepo
import com.woowla.ghd.domain.services.DeploymentService
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
class DeploymentsViewModel(
    stateMachine: DeploymentsStateMachine,
    private val eventBus: EventBus,
) : FlowReduxViewModel<DeploymentsStateMachine.St, DeploymentsStateMachine.Act>(stateMachine) {
    init {
        eventBus.subscribe(this, viewModelScope, Event.SYNCHRONIZED) {
            dispatch(DeploymentsStateMachine.Act.Reload)
        }
        eventBus.subscribe(this, viewModelScope, Event.SETTINGS_UPDATED) {
            dispatch(DeploymentsStateMachine.Act.Reload)
        }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class DeploymentsStateMachine(
    private val synchronizer: Synchronizer,
    private val appSettingsService: AppSettingsService,
    private val deploymentService: DeploymentService,
) : FlowReduxStateMachine<DeploymentsStateMachine.St, DeploymentsStateMachine.Act>(initialState = St.Initializing) {

    init {
        spec {
            inState<St.Initializing> {
                onEnter { state ->
                    load(state)
                }
                on<Act.Reload> { _, state ->
                    load(state)
                }
            }
            inState<St.Success> {
                on<Act.Reload> { _, state ->
                    load(state)
                }
            }
            inState<St.Error> {
                on<Act.Reload> { _, state ->
                    state.override { St.Initializing }
                }
            }
        }
    }

    private suspend fun <T : St> load(state: State<T>): ChangedState<St> {
        return try {
            val syncResult = synchronizer.getLastSyncResult().getOrNull()
            val appSettings = appSettingsService.get().getOrThrow()

            deploymentService.getAll()
                .fold(
                    onSuccess = { deployments ->
                        state.override {
                            St.Success(
                                deployments = deployments,
                                syncResultWithEntries = syncResult,
                                appSettings = appSettings,
                            )
                        }
                    },
                    onFailure = {
                        state.override { St.Error(it) }
                    },
                )
        } catch (e: Throwable) {
            state.override { St.Error(e) }
        }
    }

    sealed interface St {
        data object Initializing : St
        data class Success(
            val deployments: List<DeploymentWithRepo>,
            val syncResultWithEntries: SyncResultWithEntriesAndRepos?,
            val appSettings: AppSettings,
        ) : St
        data class Error(val throwable: Throwable) : St
    }

    sealed interface Act {
        data object Reload : Act
    }
}