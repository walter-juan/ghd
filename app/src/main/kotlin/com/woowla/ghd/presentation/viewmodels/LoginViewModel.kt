package com.woowla.ghd.presentation.viewmodels

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.navigator.Navigator
import com.tinder.StateMachine
import com.woowla.ghd.data.local.db.DbSettings
import com.woowla.ghd.domain.entities.AppSettings
import com.woowla.ghd.domain.services.AppSettingsService
import com.woowla.ghd.domain.synchronization.Synchronizer
import com.woowla.ghd.eventbus.Event as EventBusEvent
import com.woowla.ghd.eventbus.EventBus
import com.woowla.ghd.presentation.screens.HomeScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TimeSource.Monotonic.markNow

class LoginViewModel(
    private val navigator: Navigator,
    private val dbSettings: DbSettings = DbSettings,
    private val appSettingsService: AppSettingsService = AppSettingsService(),
) : ScreenModel {
    companion object {
        private val MIN_LOADING_DURATION: Duration = 500.milliseconds
    }
    private val initialStateValue = State.Initializing

    private val _state = MutableStateFlow<State>(initialStateValue)
    val state: StateFlow<State> = _state

    private val stateMachine = StateMachine.create<State, Event, Unit> {
        initialState(initialStateValue)
        state<State.Initializing> {
            on<Event.Initialized> { event ->
                if (event.databaseExists) {
                    transitionTo(State.LockedDatabase(appSettings = event.appSettings))
                } else {
                    transitionTo(State.NonexistentDatabase(appSettings = event.appSettings))
                }
            }
        }
        state<State.NonexistentDatabase> {
            on<Event.CreateDatabase> { event ->
                createDatabase(encrypt = event.encryptDatabase, password = event.password, appSettings = this.appSettings)
                dontTransition()
            }
            on<Event.Error> { event ->
                transitionTo(this.copy(error = event.error))
            }
        }
        state<State.LockedDatabase> {
            on<Event.UnlockDatabase> { event ->
                val encrypted = this.appSettings.encryptedDatabase
                unlockDatabase(encrypted = encrypted, password = event.password)
                dontTransition()
            }
            on<Event.DeleteDatabase> {
                deleteDatabase()
                dontTransition()
            }
            on<Event.DatabaseDeleted> {
                transitionTo(State.NonexistentDatabase(appSettings = this.appSettings))
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
        screenModelScope.launch {
            val now = markNow()
            val appSettingsResult = appSettingsService.get()
            val databaseExists = dbSettings.dbExists()

            delay(MIN_LOADING_DURATION - now.elapsedNow())

            appSettingsResult.fold(
                onSuccess = { appSettings ->
                    stateMachine.transition(Event.Initialized(databaseExists = databaseExists, appSettings = appSettings))
                },
                onFailure = { error ->
                    stateMachine.transition(Event.Error(error = error))
                }
            )
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
        screenModelScope.launch {
            val now = markNow()
            dbSettings.deleteDb()
            delay(MIN_LOADING_DURATION - now.elapsedNow())

            stateMachine.transition(Event.DatabaseDeleted)
        }
    }

    private fun createDatabase(encrypt: Boolean, password: String?, appSettings: AppSettings) {
        screenModelScope.launch {
            val passwordToUse = if (encrypt) {
                password
            } else {
                null
            }

            // createIfNotExists is true because we want to create also the database
            val now = markNow()
            val testConnectionResult = dbSettings.testConnection(filePassword = passwordToUse, createIfNotExists = true)
            delay(MIN_LOADING_DURATION - now.elapsedNow())

            testConnectionResult
                .onSuccess {
                    appSettingsService.save(appSettings.copy(encryptedDatabase = encrypt))
                    navigateHomeScreen(passwordToUse)
                }
                .onFailure {
                    stateMachine.transition(Event.Error(error = it))
                }
        }
    }

    private fun unlockDatabase(encrypted: Boolean, password: String?) {
        screenModelScope.launch {
            val passwordToUse = if (encrypted) {
                password
            } else {
                null
            }

            val now = markNow()
            val testConnectionResult = dbSettings.testConnection(filePassword = passwordToUse)
            delay(MIN_LOADING_DURATION - now.elapsedNow())

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
        data class Initialized(val databaseExists: Boolean, val appSettings: AppSettings): Event()
        data class CreateDatabase(val password: String? = null, val encryptDatabase: Boolean): Event()
        data class UnlockDatabase(val password: String?): Event()
        object DeleteDatabase: Event()
        object DatabaseDeleted: Event()
        data class Error(val error: Throwable): Event()
    }

    sealed class State {
        object Initializing: State()
        data class NonexistentDatabase(val appSettings: AppSettings, val error: Throwable? = null): State()
        data class LockedDatabase(val appSettings: AppSettings, val error: Throwable? = null): State()
    }
}