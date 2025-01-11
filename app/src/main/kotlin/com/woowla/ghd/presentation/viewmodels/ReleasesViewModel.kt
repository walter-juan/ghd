package com.woowla.ghd.presentation.viewmodels

import androidx.lifecycle.viewModelScope
import com.freeletics.flowredux.dsl.ChangedState
import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import com.freeletics.flowredux.dsl.State
import com.woowla.ghd.domain.entities.AppSettings
import com.woowla.ghd.domain.entities.ReleaseWithRepo
import com.woowla.ghd.domain.entities.SyncResultWithEntriesAndRepos
import com.woowla.ghd.domain.services.AppSettingsService
import com.woowla.ghd.domain.services.ReleaseService
import com.woowla.ghd.domain.synchronization.Synchronizer
import com.woowla.ghd.eventbus.Event
import com.woowla.ghd.eventbus.EventBus
import com.woowla.ghd.utils.FlowReduxViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
class ReleasesViewModel(
    stateMachine: ReleasesStateMachine = ReleasesStateMachine()
): FlowReduxViewModel<ReleasesStateMachine.St, ReleasesStateMachine.Act>(stateMachine) {
    init {
        EventBus.subscribe(this, viewModelScope, Event.SYNCHRONIZED) {
            dispatch(ReleasesStateMachine.Act.Reload)
        }
        EventBus.subscribe(this, viewModelScope, Event.SETTINGS_UPDATED) {
            dispatch(ReleasesStateMachine.Act.Reload)
        }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class ReleasesStateMachine(
    private val synchronizer: Synchronizer = Synchronizer.INSTANCE,
    private val appSettingsService: AppSettingsService = AppSettingsService(),
    private val releaseService: ReleaseService = ReleaseService(),
): FlowReduxStateMachine<ReleasesStateMachine.St, ReleasesStateMachine.Act>(initialState = St.Initializing) {

    init {
        spec {
            inState<St.Initializing> {
                onEnter { state ->
                    load(state, groupNameFiltersSelected = emptySet())
                }
                on<Act.Reload> { _, state ->
                    load(state, groupNameFiltersSelected = emptySet())
                }
            }
            inState<St.Success> {
                on<Act.Reload> { _, state ->
                    load(state, groupNameFiltersSelected = state.snapshot.groupNameFiltersSelected)
                }
                on<Act.GroupNameFilterSelected> { action, state ->
                    filter(state, groupName = action.groupName, isSelected = action.isSelected)
                }
            }
            inState<St.Error> {
                on<Act.Reload> { _, state ->
                    state.override { St.Initializing }
                }
            }
        }
    }

    private suspend fun <T: St> load(state: State<T>, groupNameFiltersSelected: Set<String>): ChangedState<St> {
        val syncResult = synchronizer.getLastSyncResult().getOrNull()
        val appSettings = appSettingsService.get().getOrNull()

        return releaseService.getAll()
            .fold(
                onSuccess = { releases ->
                    val groupNameFilters = releases.mapNotNull { it.repoToCheck.groupName }.distinct().toSet()
                    val groupNameFilterSizes = groupNameFilters.associateWith { groupName ->
                        releases.count { it.repoToCheck.groupName == groupName }
                    }
                    // clean up in case a group is no-available anymore
                    val groupNameFiltersSelectedRecalculated = groupNameFiltersSelected.filter { groupNameFilters.contains(it) }.toSet()

                    val releasesFiltered = releases.filter {
                        groupNameFiltersSelectedRecalculated.isEmpty() || groupNameFiltersSelectedRecalculated.contains(it.repoToCheck.groupName)
                    }
                    state.override {
                        St.Success(
                            releases = releases,
                            releasesFiltered = releasesFiltered,
                            syncResultWithEntries = syncResult,
                            appSettings = appSettings,
                            groupNameFilters = groupNameFilters,
                            groupNameFilterSizes = groupNameFilterSizes,
                            groupNameFiltersSelected = groupNameFiltersSelectedRecalculated
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
        groupName: String,
        isSelected: Boolean,
    ): ChangedState<St> {
        val groupNameFiltersSelected = if (isSelected) {
            state.snapshot.groupNameFiltersSelected - groupName
        } else {
            state.snapshot.groupNameFiltersSelected + groupName
        }
        val releasesFiltered = state.snapshot.releases.filter {
            groupNameFiltersSelected.isEmpty() || groupNameFiltersSelected.contains(it.repoToCheck.groupName)
        }
        return state.mutate {
            this.copy(
                releasesFiltered = releasesFiltered,
                groupNameFiltersSelected = groupNameFiltersSelected,
            )
        }
    }

    sealed interface St {
        data object Initializing: St
        data class Success(
            val releases: List<ReleaseWithRepo>,
            val releasesFiltered: List<ReleaseWithRepo>,
            val syncResultWithEntries: SyncResultWithEntriesAndRepos?,
            val appSettings: AppSettings?,
            val groupNameFilters: Set<String>,
            val groupNameFilterSizes: Map<String, Int>,
            val groupNameFiltersSelected: Set<String>,
        ): St
        data class  Error(val throwable: Throwable): St
    }

    sealed interface Act {
        data object Reload: Act
        data class GroupNameFilterSelected(val isSelected: Boolean, val groupName: String): Act
    }
}