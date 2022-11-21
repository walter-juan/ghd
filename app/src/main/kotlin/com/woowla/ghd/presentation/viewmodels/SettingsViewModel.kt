package com.woowla.ghd.presentation.viewmodels

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.coroutineScope
import com.woowla.ghd.domain.entities.AppSettings
import com.woowla.ghd.domain.entities.SyncSettings
import com.woowla.ghd.domain.usecases.GetAppSettingsUseCase
import com.woowla.ghd.domain.usecases.GetSyncSettingsUseCase
import com.woowla.ghd.domain.usecases.SaveAppSettingsUseCase
import com.woowla.ghd.domain.usecases.SaveSyncSettingsUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val getSyncSettingsUseCase: GetSyncSettingsUseCase = GetSyncSettingsUseCase(),
    private val saveSyncSettingsUseCase: SaveSyncSettingsUseCase = SaveSyncSettingsUseCase(),
    private val getAppSettingsUseCase: GetAppSettingsUseCase = GetAppSettingsUseCase(),
    private val saveAppSettingsUseCase: SaveAppSettingsUseCase = SaveAppSettingsUseCase(),
): ScreenModel {
    private val initialStateValue = State.Loading

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

    fun saveSettings() {
        _state.on<State.Success> {
            coroutineScope.launch {
                val syncSettingsResult = saveSyncSettingsUseCase.execute(it.syncSettings)
                val appSettingsResult = saveAppSettingsUseCase.execute(it.appSettings)
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
        _state.value = State.Loading
        coroutineScope.launch {
            try {
                val syncSettings = getSyncSettingsUseCase.execute().getOrThrow()
                val appSettings = getAppSettingsUseCase.execute().getOrThrow()
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
        object Loading: State()
        data class Success(val syncSettings: SyncSettings, val appSettings: AppSettings): State()
        data class Error(val throwable: Throwable): State()
    }
}