package com.woowla.ghd.presentation.viewmodels

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.woowla.ghd.domain.entities.SyncResult
import com.woowla.ghd.domain.entities.SyncResultEntry
import com.woowla.ghd.domain.synchronization.Synchronizer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SyncResultEntriesViewModel(
    private val syncResult: SyncResult,
    private val synchronizer: Synchronizer = Synchronizer.INSTANCE,
): ScreenModel {
    private val initialStateValue = State.Initializing

    private val _state = MutableStateFlow<State>(initialStateValue)
    val state: StateFlow<State> = _state

    init {
        load()
    }

    private fun load() {
        screenModelScope.launch {
            synchronizer.getSyncResultEntries(syncResult.id).fold(
                onSuccess = {
                    _state.value = State.Success(syncResult = syncResult, syncResultEntries = it)
                },
                onFailure = {
                    _state.value = State.Error(throwable = it)
                }
            )
        }
    }

    sealed class State {
        object Initializing: State()
        data class Success(val syncResult: SyncResult, val syncResultEntries: List<SyncResultEntry>): State()
        data class Error(val throwable: Throwable): State()
    }
}