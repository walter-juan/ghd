package com.woowla.ghd.presentation.viewmodels

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.coroutineScope
import com.woowla.ghd.domain.entities.AppSettings
import com.woowla.ghd.domain.entities.SyncSettings
import com.woowla.ghd.domain.services.AppSettingsService
import com.woowla.ghd.domain.services.SyncSettingsService
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val syncSettingsService: SyncSettingsService = SyncSettingsService(),
    private val appSettingsService: AppSettingsService = AppSettingsService(),
): ScreenModel {
    private val initialStateValue = State.Initializing

    private val _state = MutableStateFlow<State>(initialStateValue)
    val state: StateFlow<State> = _state

    private val _events = MutableSharedFlow<Events>()
    val events = _events.asSharedFlow()

    init {
        loadSettings()
    }

    fun patTokenUpdated(gitHubPatToken: String) {
        _state.on<State.Success> {
            _state.value = it.copy(syncSettings = it.syncSettings.copy(githubPatToken = gitHubPatToken))
        }
    }

    fun checkTimeoutUpdated(checkTimeout: Long) {
        _state.on<State.Success> {
            _state.value = it.copy(syncSettings = it.syncSettings.copy(checkTimeout = checkTimeout))
        }
    }

    fun pullRequestCleanUpTimeoutUpdated(cleanUpTimeout: Long) {
        _state.on<State.Success> {
            _state.value = it.copy(syncSettings = it.syncSettings.copy(pullRequestCleanUpTimeout = cleanUpTimeout))
        }
    }

    fun appThemeUpdated(appDarkTheme: Boolean?) {
        _state.on<State.Success> {
            _state.value = it.copy(appSettings = it.appSettings.copy(darkTheme = appDarkTheme))
        }
    }

    fun featurePreviewNewCardsUpdated(featurePreviewNewCards: Boolean?) {
        _state.on<State.Success> {
            _state.value = it.copy(appSettings = it.appSettings.copy(featurePreviewNewCards = featurePreviewNewCards))
        }
    }

    fun featurePreviewNewCardsBoldStyleUpdated(featurePreviewNewCardsBoldStyle: Boolean?) {
        _state.on<State.Success> {
            _state.value = it.copy(appSettings = it.appSettings.copy(featurePreviewNewCardsBoldStyle = featurePreviewNewCardsBoldStyle))
        }
    }

    fun saveSettings() {
        _state.on<State.Success> {
            coroutineScope.launch {
                val syncSettingsResult = syncSettingsService.save(it.syncSettings)
                val appSettingsResult = appSettingsService.save(it.appSettings)
                if (syncSettingsResult.isSuccess && appSettingsResult.isSuccess) {
                    _events.emit(Events.Saved)
                }
                reload()
            }
        }
    }

    fun reload() {
        loadSettings()
    }

    private fun loadSettings() {
        coroutineScope.launch {
            try {
                val syncSettings = syncSettingsService.get().getOrThrow()
                val appSettings = appSettingsService.get().getOrThrow()
                _state.value = State.Success(syncSettings = syncSettings, appSettings = appSettings)
            } catch (th: Throwable) {
                _state.value = State.Error(throwable = th)
            }
        }
    }

    private inline fun <reified T: State> MutableStateFlow<State>.on(block: (T) -> Unit) {
        (value as? T)?.let(block)
    }

    sealed class Events {
        object Saved: Events()
    }

    sealed class State {
        object Initializing: State()
        data class Success(val syncSettings: SyncSettings, val appSettings: AppSettings): State()
        data class Error(val throwable: Throwable): State()
    }
}