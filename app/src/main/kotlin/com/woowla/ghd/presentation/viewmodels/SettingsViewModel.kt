package com.woowla.ghd.presentation.viewmodels

import arrow.optics.optics
import com.freeletics.flowredux.dsl.ChangedState
import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import com.freeletics.flowredux.dsl.State
import com.woowla.ghd.domain.entities.AppSettings
import com.woowla.ghd.domain.entities.SyncSettings
import com.woowla.ghd.domain.entities.checkTimeout
import com.woowla.ghd.domain.entities.darkTheme
import com.woowla.ghd.domain.entities.githubPatToken
import com.woowla.ghd.domain.entities.pullRequestCleanUpTimeout
import com.woowla.ghd.domain.services.AppSettingsService
import com.woowla.ghd.domain.services.SyncSettingsService
import com.woowla.ghd.utils.FlowReduxViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModel(
    stateMachine: SettingsStateMachine,
) : FlowReduxViewModel<SettingsStateMachine.St, SettingsStateMachine.Act>(stateMachine)

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsStateMachine(
    private val syncSettingsService: SyncSettingsService,
    private val appSettingsService: AppSettingsService,
) : FlowReduxStateMachine<SettingsStateMachine.St, SettingsStateMachine.Act>(initialState = St.Initializing) {
    init {
        spec {
            inState<St.Initializing> {
                onEnter { state -> load(state) }
            }
            inState<St.Success> {
                on<Act.Save> { _, state ->
                    save(state)
                }
                on<Act.CleanUpSaveSuccessfully> { action, state ->
                    state.mutate { copy(savedSuccessfully = null) }
                }
                on<Act.UpdatePatToken> { action, state ->
                    state.mutate { St.Success.syncSettings.githubPatToken.modify(this) { action.gitHubPatToken } }
                }
                on<Act.UpdateCheckTimeout> { action, state ->
                    state.mutate { St.Success.syncSettings.checkTimeout.modify(this) { action.checkTimeout } }
                }
                on<Act.UpdatePullRequestCleanUpTimeout> { action, state ->
                    state.mutate { St.Success.syncSettings.pullRequestCleanUpTimeout.modify(this) { action.cleanUpTimeout } }
                }
                on<Act.UpdateAppTheme> { action, state ->
                    state.mutate { St.Success.appSettings.darkTheme.modify(this) { action.appDarkTheme } }
                }
            }
            inState<St.Error> {
                on<Act.Reload> { _, state ->
                    state.override { St.Initializing }
                }
            }
        }
    }

    private suspend fun load(state: State<St.Initializing>): ChangedState<St> {
        return try {
            val syncSettings = syncSettingsService.get().getOrThrow()
            val appSettings = appSettingsService.get().getOrThrow()
            state.override { St.Success(syncSettings, appSettings) }
        } catch (th: Throwable) {
            state.override { St.Error(th) }
        }
    }

    private suspend fun save(state: State<St.Success>): ChangedState<St> {
        return try {
            val syncSettingsResult = syncSettingsService.save(state.snapshot.syncSettings)
            val appSettingsResult = appSettingsService.save(state.snapshot.appSettings)
            if (syncSettingsResult.isSuccess && appSettingsResult.isSuccess) {
                state.mutate { copy(savedSuccessfully = true) }
            } else {
                state.mutate { copy(savedSuccessfully = false) }
            }
        } catch (th: Throwable) {
            state.override { St.Error(th) }
        }
    }

    sealed interface St {
        data object Initializing : St
        @optics data class Success(
            val syncSettings: SyncSettings,
            val appSettings: AppSettings,
            val savedSuccessfully: Boolean? = null
        ) : St {
            companion object
        }
        data class Error(val throwable: Throwable) : St
    }
    sealed interface Act {
        data object Save : Act
        data object Reload : Act
        data object CleanUpSaveSuccessfully : Act

        data class UpdatePatToken(val gitHubPatToken: String) : Act
        data class UpdateCheckTimeout(val checkTimeout: Long) : Act
        data class UpdatePullRequestCleanUpTimeout(val cleanUpTimeout: Long) : Act
        data class UpdateAppTheme(val appDarkTheme: Boolean?) : Act
    }
}