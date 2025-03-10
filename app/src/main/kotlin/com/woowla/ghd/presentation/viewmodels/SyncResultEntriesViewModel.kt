package com.woowla.ghd.presentation.viewmodels

import com.freeletics.flowredux.dsl.ChangedState
import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import com.freeletics.flowredux.dsl.State
import com.woowla.ghd.domain.entities.SyncResultWithEntriesAndRepos
import com.woowla.ghd.domain.synchronization.Synchronizer
import com.woowla.ghd.core.utils.FlowReduxViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
class SyncResultEntriesViewModel(
    stateMachine: SyncResultEntriesStateMachine,
) : FlowReduxViewModel<SyncResultEntriesStateMachine.St, SyncResultEntriesStateMachine.Act>(stateMachine) {
    init {
        dispatch(SyncResultEntriesStateMachine.Act.Load)
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class SyncResultEntriesStateMachine(
    private val syncResultId: Long,
    private val synchronizer: Synchronizer,
) : FlowReduxStateMachine<SyncResultEntriesStateMachine.St, SyncResultEntriesStateMachine.Act>(
    initialState = St.Initializing
) {

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

    private suspend fun <T : St> load(state: State<T>): ChangedState<St> {
        return synchronizer.getSyncResult(syncResultId).fold(
            onSuccess = { syncResult ->
                state.override { St.Success(syncResultWithEntries = syncResult) }
            },
            onFailure = { error ->
                state.override { St.Error(throwable = error) }
            }
        )
    }

    sealed interface St {
        data object Initializing : St
        data class Success(val syncResultWithEntries: SyncResultWithEntriesAndRepos) : St
        data class Error(val throwable: Throwable) : St
    }

    sealed interface Act {
        data object Load : Act
    }
}