package com.woowla.ghd.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woowla.ghd.AppLogger
import com.woowla.ghd.domain.entities.SyncResult
import com.woowla.ghd.domain.synchronization.Synchronizer
import com.woowla.ghd.eventbus.Event
import com.woowla.ghd.eventbus.EventBus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SyncResultsViewModel(
    private val synchronizer: Synchronizer = Synchronizer.INSTANCE,
): ViewModel() {
    private val initialStateValue = State.Initializing

    private val _state = MutableStateFlow<State>(initialStateValue)
    val state: StateFlow<State> = _state

    init {
        load()
        EventBus.subscribe(this, viewModelScope, Event.SYNCHRONIZED) {
            reload()
        }
    }

    fun reload() {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            synchronizer.getAllSyncResults().fold(
                onSuccess = {
                    _state.value = State.Success(syncResult = it)
                },
                onFailure = {
                    AppLogger.e("sync error", it)
                    _state.value = State.Error(throwable = it)
                }
            )
        }
    }

    sealed class State {
        object Initializing: State()
        data class Success(val syncResult: List<SyncResult>): State()
        data class Error(val throwable: Throwable): State()
    }
}