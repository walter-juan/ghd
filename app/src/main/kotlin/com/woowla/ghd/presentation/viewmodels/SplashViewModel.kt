package com.woowla.ghd.presentation.viewmodels

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.coroutineScope
import cafe.adriel.voyager.navigator.Navigator
import com.woowla.ghd.AppFolderFactory
import com.woowla.ghd.data.local.prop.AppProperties
import com.woowla.ghd.presentation.screens.LoginScreen
import kotlin.system.measureTimeMillis
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SplashViewModel(
    private val navigator: Navigator,
) : ScreenModel {
    companion object {
        private const val MIN_LOADING_TIME = 1200
    }

    private val initialStateValue = State.Initializing
    private val _state = MutableStateFlow<State>(initialStateValue)
    val state: StateFlow<State> = _state

    init {
        coroutineScope.launch {
            _state.value = State.Started
            val timeMillis = measureTimeMillis {
                AppFolderFactory.createFolder()
            }
            delay(MIN_LOADING_TIME - timeMillis)
            navigateToLogin()
        }
    }

    private fun navigateToLogin() {
        navigator.replaceAll(LoginScreen())
    }

    sealed class State {
        object Initializing : State()
        object Started : State()
    }
}