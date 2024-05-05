package com.woowla.ghd.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.woowla.ghd.AppFolderFactory
import com.woowla.ghd.presentation.app.AppScreen
import kotlin.system.measureTimeMillis
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SplashViewModel(
    private val navController: NavController,
) : ViewModel() {
    companion object {
        private const val MIN_LOADING_TIME = 1200
    }

    private val initialStateValue = State.Initializing
    private val _state = MutableStateFlow<State>(initialStateValue)
    val state: StateFlow<State> = _state

    init {
        viewModelScope.launch {
            _state.value = State.Started
            val timeMillis = measureTimeMillis {
                AppFolderFactory.createFolder()
            }
            delay(MIN_LOADING_TIME - timeMillis)
            navigateToLogin()
        }
    }

    private fun navigateToLogin() {
        navController.navigate(AppScreen.Login.route) {
            popUpTo(AppScreen.Splash.route) { inclusive = true }
        }
    }

    sealed class State {
        object Initializing : State()
        object Started : State()
    }
}