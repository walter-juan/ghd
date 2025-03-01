package com.woowla.ghd.presentation.viewmodels

import arrow.optics.optics
import com.freeletics.flowredux.dsl.ChangedState
import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import com.freeletics.flowredux.dsl.State
import com.woowla.ghd.domain.entities.AppSettings
import com.woowla.ghd.domain.entities.NotificationsSettings
import com.woowla.ghd.domain.entities.activityChecksFromYourPullRequestsEnabled
import com.woowla.ghd.domain.entities.activityEnabledOption
import com.woowla.ghd.domain.entities.activityMergeableFromYourPullRequestsEnabled
import com.woowla.ghd.domain.entities.activityReviewsFromYouDismissedEnabled
import com.woowla.ghd.domain.entities.activityReviewsFromYourPullRequestsEnabled
import com.woowla.ghd.domain.entities.filterUsername
import com.woowla.ghd.domain.entities.newReleaseEnabled
import com.woowla.ghd.domain.entities.notificationsSettings
import com.woowla.ghd.domain.entities.stateClosedFromOthersPullRequestsEnabled
import com.woowla.ghd.domain.entities.stateDraftFromOthersPullRequestsEnabled
import com.woowla.ghd.domain.entities.stateEnabledOption
import com.woowla.ghd.domain.entities.stateMergedFromOthersPullRequestsEnabled
import com.woowla.ghd.domain.entities.stateOpenFromOthersPullRequestsEnabled
import com.woowla.ghd.domain.services.AppSettingsService
import com.woowla.ghd.core.utils.FlowReduxViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
class NotificationsViewModel(
    stateMachine: NotificationsStateMachine,
) : FlowReduxViewModel<NotificationsStateMachine.St, NotificationsStateMachine.Act>(stateMachine)

@OptIn(ExperimentalCoroutinesApi::class)
class NotificationsStateMachine(
    private val appSettingsService: AppSettingsService,
) : FlowReduxStateMachine<NotificationsStateMachine.St, NotificationsStateMachine.Act>(initialState = St.Loading) {
    init {
        spec {
            inState<St.Loading> {
                onEnter { state ->
                    load(state)
                }
            }
            inState<St.Success> {
                on<Act.Save> { action, state ->
                    save(state)
                }
                on<Act.CleanUpSaveSuccessfully> { action, state ->
                    state.mutate { copy(savedSuccessfully = null) }
                }
                on<Act.UpdateFilterUsername> { action, state ->
                    state.mutate { St.Success.appSettings.notificationsSettings.filterUsername.modify(this) { action.filterUsername } }
                }
                on<Act.UpdateStateEnabledOption> { action, state ->
                    state.mutate { St.Success.appSettings.notificationsSettings.stateEnabledOption.modify(this) { action.enabledOption } }
                }
                on<Act.UpdateStateOpenFromOthersPullRequestsEnabled> { action, state ->
                    state.mutate { St.Success.appSettings.notificationsSettings.stateOpenFromOthersPullRequestsEnabled.modify(this) { action.enabled } }
                }
                on<Act.UpdateStateClosedFromOthersPullRequestsEnabled> { action, state ->
                    state.mutate { St.Success.appSettings.notificationsSettings.stateClosedFromOthersPullRequestsEnabled.modify(this) { action.enabled } }
                }
                on<Act.UpdateStateMergedFromOthersPullRequestsEnabled> { action, state ->
                    state.mutate { St.Success.appSettings.notificationsSettings.stateMergedFromOthersPullRequestsEnabled.modify(this) { action.enabled } }
                }
                on<Act.UpdateStateDraftFromOthersPullRequestsEnabled> { action, state ->
                    state.mutate { St.Success.appSettings.notificationsSettings.stateDraftFromOthersPullRequestsEnabled.modify(this) { action.enabled } }
                }
                on<Act.UpdateActivityEnabledOption> { action, state ->
                    state.mutate { St.Success.appSettings.notificationsSettings.activityEnabledOption.modify(this) { action.enabledOption } }
                }
                on<Act.UpdateActivityReviewsFromYourPullRequestsEnabled> { action, state ->
                    state.mutate { St.Success.appSettings.notificationsSettings.activityReviewsFromYourPullRequestsEnabled.modify(this) { action.enabled } }
                }
                on<Act.UpdateActivityReviewsFromYouDismissedEnabled> { action, state ->
                    state.mutate { St.Success.appSettings.notificationsSettings.activityReviewsFromYouDismissedEnabled.modify(this) { action.enabled } }
                }
                on<Act.UpdateActivityChecksFromYourPullRequestsEnabled> { action, state ->
                    state.mutate { St.Success.appSettings.notificationsSettings.activityChecksFromYourPullRequestsEnabled.modify(this) { action.enabled } }
                }
                on<Act.UpdateActivityMergeableFromYourPullRequestsEnabled> { action, state ->
                    state.mutate { St.Success.appSettings.notificationsSettings.activityMergeableFromYourPullRequestsEnabled.modify(this) { action.enabled } }
                }
                on<Act.UpdateNewReleaseEnabled> { action, state ->
                    state.mutate { St.Success.appSettings.notificationsSettings.newReleaseEnabled.modify(this) { action.enabled } }
                }
            }
            inState<St.Error> {
                on<Act.Reload> { action, state ->
                    state.override { St.Loading }
                }
            }
        }
    }

    private suspend fun load(state: State<St.Loading>): ChangedState<St> {
        return appSettingsService
            .get()
            .fold(
                onSuccess = { appSettings ->
                    state.override { St.Success(appSettings) }
                },
                onFailure = { error ->
                    state.override { St.Error(error) }
                },
            )
    }

    private suspend fun save(state: State<St.Success>): ChangedState<St> {
        return appSettingsService
            .save(state.snapshot.appSettings)
            .fold(
                onSuccess = {
                    state.mutate { copy(savedSuccessfully = true) }
                },
                onFailure = { error ->
                    state.mutate { copy(savedSuccessfully = false) }
                },
            )
    }

    sealed interface St {
        data object Loading : St
        @optics data class Success(val appSettings: AppSettings, val savedSuccessfully: Boolean? = null) : St {
            companion object
            val notificationsSettings: NotificationsSettings = appSettings.notificationsSettings
        }
        data class Error(val error: Throwable) : St
    }
    sealed interface Act {
        data object Reload : Act
        data object Save : Act
        data object CleanUpSaveSuccessfully : Act

        data class UpdateFilterUsername(val filterUsername: String) : Act

        data class UpdateStateEnabledOption(val enabledOption: NotificationsSettings.EnabledOption) : Act
        data class UpdateStateOpenFromOthersPullRequestsEnabled(val enabled: Boolean) : Act
        data class UpdateStateClosedFromOthersPullRequestsEnabled(val enabled: Boolean) : Act
        data class UpdateStateMergedFromOthersPullRequestsEnabled(val enabled: Boolean) : Act
        data class UpdateStateDraftFromOthersPullRequestsEnabled(val enabled: Boolean) : Act

        data class UpdateActivityEnabledOption(val enabledOption: NotificationsSettings.EnabledOption) : Act
        data class UpdateActivityReviewsFromYourPullRequestsEnabled(val enabled: Boolean) : Act

        data class UpdateActivityReviewsFromYouDismissedEnabled(val enabled: Boolean) : Act
        data class UpdateActivityChecksFromYourPullRequestsEnabled(val enabled: Boolean) : Act
        data class UpdateActivityMergeableFromYourPullRequestsEnabled(val enabled: Boolean) : Act

        data class UpdateNewReleaseEnabled(val enabled: Boolean) : Act
    }
}
