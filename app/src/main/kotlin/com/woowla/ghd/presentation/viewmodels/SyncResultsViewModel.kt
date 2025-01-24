package com.woowla.ghd.presentation.viewmodels

import androidx.lifecycle.viewModelScope
import com.freeletics.flowredux.dsl.ChangedState
import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import com.freeletics.flowredux.dsl.State
import com.woowla.ghd.domain.entities.SyncResultWithEntriesAndRepos
import com.woowla.ghd.domain.synchronization.Synchronizer
import com.woowla.ghd.eventbus.Event
import com.woowla.ghd.eventbus.EventBus
import com.woowla.ghd.utils.FlowReduxViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
class SyncResultsViewModel(
    stateMachine: SyncResultsStateMachine
): FlowReduxViewModel<SyncResultsStateMachine.St, SyncResultsStateMachine.Act>(stateMachine) {
    init {
        dispatch(SyncResultsStateMachine.Act.Load)
        EventBus.subscribe(this, viewModelScope, Event.SYNCHRONIZED) {
            dispatch(SyncResultsStateMachine.Act.Load)
        }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class SyncResultsStateMachine(
    private val synchronizer: Synchronizer,
): FlowReduxStateMachine<SyncResultsStateMachine.St, SyncResultsStateMachine.Act>(initialState = St.Initializing) {

    init {
        spec {
            inState<St.Initializing> {
                onEnter { state ->
                    load(state)
                }
                on<Act.Load> { _, state ->
                    load(state)
                }
            }
            inState<St.Success> {
                on<Act.Load> { _, state ->
                    load(state)
                }
            }
            inState<St.Error> {
                on<Act.Load> { _, state ->
                    load(state)
                }
            }
        }
    }

    private suspend fun <T: St> load(state: State<T>): ChangedState<St> {
        return synchronizer.getAllSyncResults().fold(
            onSuccess = { syncResults ->
                state.override { St.Success(syncResultWithEntries = syncResults) }
            },
            onFailure = { error ->
                state.override { St.Error(throwable = error) }
            }
        )
    }

    sealed interface St {
        data object Initializing: St
        data class Success(val syncResultWithEntries: List<SyncResultWithEntriesAndRepos>): St
        data class Error(val throwable: Throwable): St
    }

    sealed interface Act {
        data object Load: Act
    }
}