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
    stateMachine: PullRequestsStateMachine = PullRequestsStateMachine()
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
    private val synchronizer: Synchronizer = Synchronizer.INSTANCE,
    private val appSettingsService: AppSettingsService = AppSettingsService(),
    private val pullRequestService: PullRequestService = PullRequestService(),
): FlowReduxStateMachine<PullRequestsStateMachine.St, PullRequestsStateMachine.Act>(initialState = St.Initializing) {

    init {
        spec {
            inState<St.Initializing> {
                onEnter { state ->
                    load(state, stateExtendedFiltersSelected = emptySet())
                }
                on<Act.Reload> { _, state ->
                    load(state, stateExtendedFiltersSelected = emptySet())
                }
            }
            inState<St.Success> {
                on<Act.Reload> { _, state ->
                    load(state, stateExtendedFiltersSelected = state.snapshot.stateExtendedFiltersSelected)
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

    private suspend fun <T: St> load(state: State<T>, stateExtendedFiltersSelected: Set<PullRequestStateExtended>): ChangedState<St> {
        val syncResult = synchronizer.getLastSyncResult().getOrNull()
        val appSettings = appSettingsService.get().getOrNull()

        return pullRequestService.getAll()
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
    }

    private fun filter(
        state: State<St.Success>,
        stateExtended: PullRequestStateExtended,
        isSelected: Boolean,
    ): ChangedState<St> {
        val stateExtendedFiltersSelected = if (isSelected) {
            state.snapshot.stateExtendedFiltersSelected - stateExtended
        } else {
            state.snapshot.stateExtendedFiltersSelected + stateExtended
        }
        val pullRequestsFiltered = state.snapshot.pullRequests.filter {
            stateExtendedFiltersSelected.isEmpty() || stateExtendedFiltersSelected.contains(it.pullRequest.stateExtended)
        }
        return state.mutate {
            this.copy(
                pullRequestsFiltered = pullRequestsFiltered,
                stateExtendedFiltersSelected = stateExtendedFiltersSelected,
            )
        }
    }

    sealed interface St {
        data object Initializing: St
        data class Success(
            val pullRequests: List<PullRequestWithRepoAndReviews>,
            val pullRequestsFiltered: List<PullRequestWithRepoAndReviews>,
            val syncResultWithEntries: SyncResultWithEntriesAndRepos?,
            val appSettings: AppSettings?,
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