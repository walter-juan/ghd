package com.woowla.ghd.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
open class FlowReduxViewModel<S: Any, A: Any>(
    protected val stateMachine: FlowReduxStateMachine<S, A>
): ViewModel() {
    private val _state = MutableStateFlow<S?>(null)
    val state: StateFlow<S?> = _state

    init {
        viewModelScope.launch {
            // automatically canceled once ViewModel lifecycle reached destroyed.
            stateMachine.state.collect { newState ->
                _state.value = newState
            }
        }
    }

    fun dispatch(action: A) {
        viewModelScope.launch {
            stateMachine.dispatch(action)
        }
    }
}