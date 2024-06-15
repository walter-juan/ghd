package com.woowla.ghd.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woowla.ghd.domain.entities.SyncResultWithEntitiesAndRepos
import com.woowla.ghd.domain.synchronization.Synchronizer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SyncResultEntriesViewModel(
    private val syncResultId: Long,
    private val synchronizer: Synchronizer = Synchronizer.INSTANCE,
): ViewModel() {
    private val initialStateValue = State.Initializing

    private val _state = MutableStateFlow<State>(initialStateValue)
    val state: StateFlow<State> = _state

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            val syncResult = synchronizer.getSyncResult(syncResultId)

           if (syncResult.isSuccess) {
               _state.value = State.Success(syncResultWithEntries = syncResult.getOrThrow())
           } else {
               val throwable = syncResult.exceptionOrNull()
               requireNotNull(throwable)
               _state.value = State.Error(throwable = throwable)
           }
        }
    }

    sealed class State {
        object Initializing: State()
        data class Success(val syncResultWithEntries: SyncResultWithEntitiesAndRepos): State()
        data class Error(val throwable: Throwable): State()
    }
}