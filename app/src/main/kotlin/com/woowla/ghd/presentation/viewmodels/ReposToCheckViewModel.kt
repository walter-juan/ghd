package com.woowla.ghd.presentation.viewmodels

import androidx.lifecycle.viewModelScope
import com.freeletics.flowredux.dsl.ChangedState
import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import com.freeletics.flowredux.dsl.State
import com.woowla.ghd.domain.entities.AppSettings
import com.woowla.ghd.domain.entities.RepoToCheck
import com.woowla.ghd.domain.services.AppSettingsService
import com.woowla.ghd.domain.services.RepoToCheckService
import com.woowla.ghd.eventbus.Event
import com.woowla.ghd.eventbus.EventBus
import com.woowla.ghd.utils.FlowReduxViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
class ReposToCheckViewModel(
    stateMachine: ReposToCheckStateMachine = ReposToCheckStateMachine()
): FlowReduxViewModel<ReposToCheckStateMachine.St, ReposToCheckStateMachine.Act>(stateMachine) {
    init {
        EventBus.subscribe(this, viewModelScope, Event.REPO_TO_CHECK_UPDATED) {
            dispatch(ReposToCheckStateMachine.Act.Reload)
        }
        EventBus.subscribe(this, viewModelScope, Event.SETTINGS_UPDATED) {
            dispatch(ReposToCheckStateMachine.Act.Reload)
        }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class ReposToCheckStateMachine(
    private val repoToCheckService: RepoToCheckService = RepoToCheckService(),
    private val appSettingsService: AppSettingsService = AppSettingsService(),
): FlowReduxStateMachine<ReposToCheckStateMachine.St, ReposToCheckStateMachine.Act>(initialState = St.Initializing) {

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
                on<Act.DeleteRepoToCheck> { action, state ->
                    deleteRepo(state, repoToCheck = action.repoToCheck)
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
        val appSettings = appSettingsService.get().getOrNull()

        return repoToCheckService.getAll()
            .fold(
                onSuccess = { reposToCheck ->
                    val groupNameFilters = reposToCheck.mapNotNull { it.groupName }.distinct().toSet()
                    val groupNameFilterSizes = groupNameFilters.associateWith { groupName ->
                        reposToCheck.count { it.groupName == groupName }
                    }
                    // clean up in case a group is no-available anymore
                    val groupNameFiltersSelectedRecalculated = groupNameFiltersSelected.filter { groupNameFilters.contains(it) }.toSet()

                    val releasesFiltered = reposToCheck.filter {
                        groupNameFiltersSelectedRecalculated.isEmpty() || groupNameFiltersSelectedRecalculated.contains(it.groupName)
                    }

                    state.override {
                        St.Success(
                            reposToCheck = reposToCheck,
                            reposToCheckFiltered = releasesFiltered,
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

    private suspend fun deleteRepo(state: State<St.Success>, repoToCheck: RepoToCheck): ChangedState<St> {
        repoToCheckService.delete(repoToCheck.id)
        return load(state, groupNameFiltersSelected = state.snapshot.groupNameFiltersSelected)
    }

    private fun filter(state: State<St.Success>, groupName: String, isSelected: Boolean): ChangedState<St> {
        val groupNameFiltersSelected = if (isSelected) {
            state.snapshot.groupNameFiltersSelected - groupName
        } else {
            state.snapshot.groupNameFiltersSelected + groupName
        }
        val reposToCheckFiltered = state.snapshot.reposToCheck.filter {
            groupNameFiltersSelected.isEmpty() || groupNameFiltersSelected.contains(it.groupName)
        }
        return state.mutate {
            this.copy(
                reposToCheckFiltered = reposToCheckFiltered,
                groupNameFiltersSelected = groupNameFiltersSelected,
            )
        }
    }

    sealed interface St {
        data object Initializing: St
        data class Success(
            val reposToCheck: List<RepoToCheck>,
            val reposToCheckFiltered: List<RepoToCheck>,
            val appSettings: AppSettings?,
            val groupNameFilters: Set<String>,
            val groupNameFilterSizes: Map<String, Int>,
            val groupNameFiltersSelected: Set<String>,
        ): St
        data class  Error(val throwable: Throwable): St
    }

    sealed interface Act {
        data object Reload: Act
        data class DeleteRepoToCheck(val repoToCheck: RepoToCheck): Act
        data class GroupNameFilterSelected(val isSelected: Boolean, val groupName: String): Act
    }
}
