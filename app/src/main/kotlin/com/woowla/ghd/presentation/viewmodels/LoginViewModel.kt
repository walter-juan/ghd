package com.woowla.ghd.presentation.viewmodels

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.coroutineScope
import cafe.adriel.voyager.navigator.Navigator
import com.woowla.ghd.data.local.db.DbSettings
import com.woowla.ghd.data.local.db.exceptions.DbDatabaseNotFoundException
import com.woowla.ghd.domain.Synchronizer
import com.woowla.ghd.eventbus.Event
import com.woowla.ghd.eventbus.EventBus
import com.woowla.ghd.presentation.screens.HomeScreen
import kotlin.system.measureTimeMillis
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalTime::class)
class LoginViewModel(
    private val navigator: Navigator,
    private val dbSettings: DbSettings = DbSettings
) : ScreenModel {
    companion object {
        private const val MIN_LOADING_TIME = 500
    }
    private val initialStateValue = State.Loading

    private val _state = MutableStateFlow<State>(initialStateValue)
    val state: StateFlow<State> = _state

    init {
        _state.value = State.Loading

        coroutineScope.launch {
            val (testConnectionResult,  time) = measureTimedValue {
                dbSettings.testConnection()
            }
            delay(MIN_LOADING_TIME - time.inWholeMilliseconds)

            testConnectionResult
                .onSuccess {
                    _state.value = State.Success.SuccessWithDatabase()
                }
                .onFailure { th ->
                    _state.value = when(th) {
                        is DbDatabaseNotFoundException -> {
                            State.Success.SuccessWithoutDatabase()
                        }
                        else -> {
                            State.Success.SuccessWithDatabase()
                        }
                    }
                }
        }
    }

    fun createDatabase(pwd: String) {
        _state.value = State.Loading

        coroutineScope.launch {
            // ifExists is false because we want to create also the database
            val (testConnectionResult,  time) = measureTimedValue {
                dbSettings.testConnection(filePassword = pwd, ifExists = false)
            }
            delay(MIN_LOADING_TIME - time.inWholeMilliseconds)

            testConnectionResult
                .onSuccess {
                    navigateToAfterUnlockDatabaseScreen(pwd)
                }
                .onFailure {
                    _state.value = State.Success.SuccessWithoutDatabase(error = it)
                }
        }
    }

    fun unlockDatabase(pwd: String) {
        _state.value = State.Loading
        coroutineScope.launch {
            val (testConnectionResult,  time) = measureTimedValue {
                dbSettings.testConnection(filePassword = pwd)
            }
            delay(MIN_LOADING_TIME - time.inWholeMilliseconds)

            testConnectionResult
                .onSuccess {
                    navigateToAfterUnlockDatabaseScreen(pwd)
                }
                .onFailure {
                    _state.value = State.Success.SuccessWithDatabase(error = it)
                }
        }
    }

    fun deleteDatabase() {
        _state.value = State.Loading
        coroutineScope.launch {
            val time = measureTimeMillis {
                dbSettings.deleteDb()
            }
            delay(MIN_LOADING_TIME - time)

            _state.value = State.Success.SuccessWithoutDatabase()
        }
    }

    private suspend fun navigateToAfterUnlockDatabaseScreen(pwd: String) {
        dbSettings.initDb(filePassword = pwd)
        Synchronizer.INSTANCE.initialize()
        EventBus.publish(Event.APP_UNLOCKED)
        navigator.replaceAll(HomeScreen())
    }

    sealed class State {
        object Loading: State()
        sealed class Success: State() {
            data class SuccessWithDatabase(val error: Throwable? = null): Success()
            data class SuccessWithoutDatabase(val error: Throwable? = null): Success()
        }
        data class  Error(val throwable: Throwable): State()
    }
}