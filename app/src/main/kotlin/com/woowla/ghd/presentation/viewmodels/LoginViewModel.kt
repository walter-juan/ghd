package com.woowla.ghd.presentation.viewmodels

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.coroutineScope
import cafe.adriel.voyager.navigator.Navigator
import com.tinder.StateMachine
import com.woowla.ghd.data.local.db.DbSettings
import com.woowla.ghd.data.local.db.exceptions.DbDatabaseNotFoundException
import com.woowla.ghd.domain.synchronization.Synchronizer
import com.woowla.ghd.eventbus.Event as EventBusEvent
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
    private val initialStateValue = State.Initializing

    private val _state = MutableStateFlow<State>(initialStateValue)
    val state: StateFlow<State> = _state

    private val stateMachine = StateMachine.create<State, Event, Unit> {
        initialState(initialStateValue)
        state<State.Initializing> {
            on<Event.Initialized> { event ->
                if (event.databaseExists) {
                    transitionTo(State.LockedDatabase(isDbEncrypted = event.isDbEncrypted))
                } else {
                    transitionTo(State.NonexistentDatabase())
                }
            }
        }
        state<State.NonexistentDatabase> {
            on<Event.CreateDatabase> { event ->
                createDatabase(encrypt = event.encryptDatabase, password = event.password)
                dontTransition()
            }
            on<Event.Error> { event ->
                transitionTo(this.copy(error = event.error))
            }
        }
        state<State.LockedDatabase> {
            on<Event.UnlockDatabase> { event ->
                unlockDatabase(encrypted = this.isDbEncrypted, password = event.password)
                dontTransition()
            }
            on<Event.DeleteDatabase> {
                deleteDatabase()
                dontTransition()
            }
            on<Event.DatabaseDeleted> {
                transitionTo(State.NonexistentDatabase())
            }
            on<Event.Error> { event ->
                transitionTo(this.copy(error = event.error))
            }
        }
        onTransition {
            val validTransition = it as? StateMachine.Transition.Valid ?: return@onTransition
            _state.value = validTransition.toState
        }
    }

    init {
        coroutineScope.launch {
            val (testConnectionResult,  time) = measureTimedValue {
                dbSettings.testConnection(filePassword = null)
            }
            delay(MIN_LOADING_TIME - time.inWholeMilliseconds)

            testConnectionResult
                .onSuccess {
                    stateMachine.transition(Event.Initialized(databaseExists = true, isDbEncrypted = false))
                }
                .onFailure { th ->
                    when(th) {
                        is DbDatabaseNotFoundException -> {
                            stateMachine.transition(Event.Initialized(databaseExists = false, isDbEncrypted = false))
                        }
                        else -> {
                            stateMachine.transition(Event.Initialized(databaseExists = true, isDbEncrypted = true))
                        }
                    }
                }
        }
    }

    fun onCreateDatabase(encrypt: Boolean, password: String?) {
        stateMachine.transition(Event.CreateDatabase(password = password, encryptDatabase = encrypt))
    }

    fun onUnlockDatabase(password: String?) {
        stateMachine.transition(Event.UnlockDatabase(password = password))
    }

    fun onDeleteDatabase() {
        stateMachine.transition(Event.DeleteDatabase)
    }

    private fun deleteDatabase() {
        coroutineScope.launch {
            val time = measureTimeMillis {
                dbSettings.deleteDb()
            }
            delay(MIN_LOADING_TIME - time)

            stateMachine.transition(Event.DatabaseDeleted)
        }
    }

    private fun createDatabase(encrypt: Boolean, password: String?) {
        coroutineScope.launch {
            val passwordToUse = if (encrypt) {
                password
            } else {
                null
            }

            // createIfNotExists is true because we want to create also the database
            val (testConnectionResult, time) = measureTimedValue {
                dbSettings.testConnection(filePassword = passwordToUse, createIfNotExists = true)
            }
            delay(MIN_LOADING_TIME - time.inWholeMilliseconds)

            testConnectionResult
                .onSuccess {
                    navigateHomeScreen(passwordToUse)
                }
                .onFailure {
                    stateMachine.transition(Event.Error(error = it))
                }
        }
    }

    private fun unlockDatabase(encrypted: Boolean, password: String?) {
        coroutineScope.launch {
            val passwordToUse = if (encrypted) {
                password
            } else {
                null
            }

            val (testConnectionResult, time) = measureTimedValue {
                dbSettings.testConnection(filePassword = passwordToUse)
            }
            delay(MIN_LOADING_TIME - time.inWholeMilliseconds)

            testConnectionResult
                .onSuccess {
                    navigateHomeScreen(passwordToUse)
                }
                .onFailure {
                    stateMachine.transition(Event.Error(error = it))
                }
        }
    }

    private suspend fun navigateHomeScreen(pwd: String?) {
        dbSettings.initDb(filePassword = pwd)
        Synchronizer.INSTANCE.initialize()
        EventBus.publish(EventBusEvent.APP_UNLOCKED)
        navigator.replaceAll(HomeScreen())
    }

    sealed class Event {
        data class Initialized(val databaseExists: Boolean, val isDbEncrypted: Boolean): Event()
        data class CreateDatabase(val password: String? = null, val encryptDatabase: Boolean): Event()
        data class UnlockDatabase(val password: String?): Event()
        object DeleteDatabase: Event()
        object DatabaseDeleted: Event()
        data class Error(val error: Throwable): Event()
    }

    sealed class State {
        object Initializing: State()
        data class NonexistentDatabase(val error: Throwable? = null): State()
        data class LockedDatabase(val isDbEncrypted: Boolean, val error: Throwable? = null): State()
    }
}