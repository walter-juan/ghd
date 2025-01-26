package com.woowla.ghd.presentation.viewmodels

import androidx.lifecycle.viewModelScope
import com.freeletics.flowredux.dsl.ChangedState
import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import com.freeletics.flowredux.dsl.State
import com.woowla.ghd.domain.entities.AppSettings
import com.woowla.ghd.domain.entities.PullRequestStateExtended
import com.woowla.ghd.domain.entities.PullRequestWithRepoAndReviews
import com.woowla.ghd.domain.entities.SyncResultWithEntriesAndRepos
import com.woowla.ghd.domain.services.AppSettingsService
import com.woowla.ghd.domain.services.PullRequestService
import com.woowla.ghd.domain.synchronization.Synchronizer
import com.woowla.ghd.eventbus.Event
import com.woowla.ghd.eventbus.EventBus
import com.woowla.ghd.utils.FlowReduxViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
class PullRequestsViewModel(
    stateMachine: PullRequestsStateMachine,
): FlowReduxViewModel<PullRequestsStateMachine.St, PullRequestsStateMachine.Act>(stateMachine) {
    init {
        EventBus.subscribe(this, viewModelScope, Event.SYNCHRONIZED) {
            dispatch(PullRequestsStateMachine.Act.Reload)
        }
        EventBus.subscribe(this, viewModelScope, Event.SETTINGS_UPDATED) {
            dispatch(PullRequestsStateMachine.Act.Reload)
        }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class PullRequestsStateMachine(
    private val synchronizer: Synchronizer,
    private val appSettingsService: AppSettingsService,
    private val pullRequestService: PullRequestService,
): FlowReduxStateMachine<PullRequestsStateMachine.St, PullRequestsStateMachine.Act>(initialState = St.Initializing) {

    init {
        spec {
            inState<St.Initializing> {
                onEnter { state ->
                    load(state)
                }
                on<Act.Reload> { _, state ->
                    load(state)
                }
            }
            inState<St.Success> {
                on<Act.Reload> { _, state ->
                    load(state)
                }
                on<Act.StateExtendedFilterSelected> { action, state ->
                    filter(state, stateExtended = action.pullRequestStateExtended, isSelected = action.isSelected)
                }
            }
            inState<St.Error> {
                on<Act.Reload> { _, state ->
                    state.override { St.Initializing }
                }
            }
        }
    }

    private suspend fun <T: St> load(state: State<T>): ChangedState<St> {
        return try {
            val syncResult = synchronizer.getLastSyncResult().getOrNull()
            val appSettings = appSettingsService.get().getOrThrow()
            val stateExtendedFiltersSelected = appSettings.filtersPullRequestState

            pullRequestService.getAll()
                .fold(
                    onSuccess = { pullRequests ->
                        val stateFilters = PullRequestStateExtended.entries.toSet()
                        val stateFilterSizes = stateFilters.associateWith { stateExtended ->
                            pullRequests.count { it.pullRequest.stateExtended == stateExtended }
                        }
                        // clean up in case a group is no-available anymore
                        val stateExtendedFiltersSelectedRecalculated = stateExtendedFiltersSelected.filter { stateFilters.contains(it) }.toSet()

                        val pullRequestsFiltered = pullRequests.filter {
                            stateExtendedFiltersSelectedRecalculated.isEmpty() || stateExtendedFiltersSelectedRecalculated.contains(it.pullRequest.stateExtended)
                        }
                        state.override {
                            St.Success(
                                pullRequests = pullRequests,
                                pullRequestsFiltered = pullRequestsFiltered,
                                syncResultWithEntries = syncResult,
                                appSettings = appSettings,
                                stateExtendedFilters = stateFilters,
                                stateExtendedFilterSizes = stateFilterSizes,
                                stateExtendedFiltersSelected = stateExtendedFiltersSelectedRecalculated
                            )
                        }
                    },
                    onFailure = {
                        state.override { St.Error(it) }
                    }
                )
        } catch (ex: Exception) {
            state.override { St.Error(ex) }
        }
    }

    private suspend fun filter(
        state: State<St.Success>,
        stateExtended: PullRequestStateExtended,
        isSelected: Boolean,
    ): ChangedState<St> {
        val stateExtendedFiltersSelected = if (isSelected) {
            state.snapshot.stateExtendedFiltersSelected - stateExtended
        } else {
            state.snapshot.stateExtendedFiltersSelected + stateExtended
        }

        val appSettings = state.snapshot.appSettings.copy(filtersPullRequestState = stateExtendedFiltersSelected)
        appSettingsService.save(appSettings)

        // no change because the SETTINGS_UPDATED will be triggered automatically
        return state.noChange()
    }

    sealed interface St {
        data object Initializing: St
        data class Success(
            val pullRequests: List<PullRequestWithRepoAndReviews>,
            val pullRequestsFiltered: List<PullRequestWithRepoAndReviews>,
            val syncResultWithEntries: SyncResultWithEntriesAndRepos?,
            val appSettings: AppSettings,
            val stateExtendedFilters: Set<PullRequestStateExtended>,
            val stateExtendedFilterSizes: Map<PullRequestStateExtended, Int>,
            val stateExtendedFiltersSelected: Set<PullRequestStateExtended>,
        ): St
        data class  Error(val throwable: Throwable): St
    }

    sealed interface Act {
        data object Reload: Act
        data class StateExtendedFilterSelected(val isSelected: Boolean, val pullRequestStateExtended: PullRequestStateExtended): Act
    }
}