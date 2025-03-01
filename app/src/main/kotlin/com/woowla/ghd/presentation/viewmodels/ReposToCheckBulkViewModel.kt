package com.woowla.ghd.presentation.viewmodels

import androidx.lifecycle.viewModelScope
import com.freeletics.flowredux.dsl.ChangedState
import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import com.freeletics.flowredux.dsl.State
import com.woowla.ghd.domain.services.RepoToCheckService
import com.woowla.ghd.domain.entities.Event
import com.woowla.ghd.core.eventbus.EventBus
import com.woowla.ghd.core.utils.FlowReduxViewModel
import java.io.File
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
class ReposToCheckBulkViewModel(
    stateMachine: ReposToCheckBulkStateMachine,
    private val eventBus: EventBus,
) : FlowReduxViewModel<ReposToCheckBulkStateMachine.St, ReposToCheckBulkStateMachine.Act>(stateMachine) {
    init {
        eventBus.subscribe(this, viewModelScope, Event.REPO_TO_CHECK_UPDATED) {
            dispatch(ReposToCheckBulkStateMachine.Act.Reload)
        }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class ReposToCheckBulkStateMachine(
    private val repoToCheckService: RepoToCheckService,
) : FlowReduxStateMachine<ReposToCheckBulkStateMachine.St, ReposToCheckBulkStateMachine.Act>(
    initialState = St.Success
) {

    init {
        spec {
            inState<St.Success> {
                on<Act.ImportRepos> { action, state ->
                    importRepos(state, action.file)
                }
                on<Act.ExportRepos> { action, state ->
                    exportRepos(state, action.file)
                }
                on<Act.Reload> { _, state ->
                    state.override { St.Success }
                }
            }
        }
    }

    private suspend fun importRepos(state: State<St.Success>, file: File): ChangedState<St> {
        return try {
            val content = file.readText()
            repoToCheckService.import(content)
            state.override { St.Success }
        } catch (e: Exception) {
            state.override { St.Error(e) }
        }
    }

    private suspend fun exportRepos(state: State<St.Success>, file: File): ChangedState<St> {
        return try {
            repoToCheckService.export().fold(
                onSuccess = { content ->
                    file.writeText(content)
                    state.override { St.Success }
                },
                onFailure = { error ->
                    state.override { St.Error(error) }
                }
            )
        } catch (e: Exception) {
            state.override { St.Error(e) }
        }
    }

    sealed interface St {
        data object Success : St
        data class Error(val throwable: Throwable) : St
    }

    sealed interface Act {
        data object Reload : Act
        data class ImportRepos(val file: File) : Act
        data class ExportRepos(val file: File) : Act
    }
}