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
    stateMachine: ReposToCheckStateMachine,
) : FlowReduxViewModel<ReposToCheckStateMachine.St, ReposToCheckStateMachine.Act>(stateMachine) {
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
    private val repoToCheckService: RepoToCheckService,
    private val appSettingsService: AppSettingsService,
) : FlowReduxStateMachine<ReposToCheckStateMachine.St, ReposToCheckStateMachine.Act>(initialState = St.Initializing) {

    init {
        spec {
            inState<St.Initializing> {
                onEnter { state ->
                    load(state, searchQuery = "")
                }
                on<Act.Reload> { _, state ->
                    load(state, searchQuery = "")
                }
            }
            inState<St.Success> {
                on<Act.Reload> { _, state ->
                    load(state, searchQuery = state.snapshot.searchQuery)
                }
                on<Act.GroupNameFilterSelected> { action, state ->
                    filter(state, groupName = action.groupName, isSelected = action.isSelected)
                }
                on<Act.SearchQueryChanged> { action, state ->
                    filter(state, searchQuery = action.searchQuery)
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

    private suspend fun <T : St> load(state: State<T>, searchQuery: String): ChangedState<St> {
        return try {
            val appSettings = appSettingsService.get().getOrThrow()
            val groupNameFiltersSelected = appSettings.filtersRepoToCheckGroupName

            repoToCheckService.getAll()
                .fold(
                    onSuccess = { reposToCheck ->
                        val groupNameFilters = reposToCheck.mapNotNull { it.groupName }.distinct().filter { it.isNotBlank() }.toSet()
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
                                groupNameFiltersSelected = groupNameFiltersSelectedRecalculated,
                                searchQuery = searchQuery,
                            )
                        }
                    },
                    onFailure = {
                        state.override { St.Error(it) }
                    }
                )
        } catch (throwable: Throwable) {
            state.override { St.Error(throwable) }
        }
    }

    private suspend fun deleteRepo(state: State<St.Success>, repoToCheck: RepoToCheck): ChangedState<St> {
        repoToCheckService.delete(repoToCheck.id)
        return load(state, searchQuery = state.snapshot.searchQuery)
    }

    private fun filter(state: State<St.Success>, searchQuery: String): ChangedState<St> {
        val reposToCheckFiltered = state.snapshot.reposToCheck.filter(
            searchQuery = searchQuery,
            groupNames = state.snapshot.groupNameFiltersSelected,
        )
        return state.mutate {
            this.copy(
                reposToCheckFiltered = reposToCheckFiltered,
                searchQuery = searchQuery,
            )
        }
    }

    private suspend fun filter(state: State<St.Success>, groupName: String, isSelected: Boolean): ChangedState<St> {
        val groupNameFiltersSelected = if (isSelected) {
            state.snapshot.groupNameFiltersSelected - groupName
        } else {
            state.snapshot.groupNameFiltersSelected + groupName
        }

        val appSettings = state.snapshot.appSettings.copy(filtersRepoToCheckGroupName = groupNameFiltersSelected)
        appSettingsService.save(appSettings)

        // no change because the SETTINGS_UPDATED will be triggered automatically
        return state.noChange()
    }

    private fun List<RepoToCheck>.filter(searchQuery: String, groupNames: Set<String>): List<RepoToCheck> {
        val searchQueryIsBlank = searchQuery.isBlank()
        val searchQuernContainsName = { repoToCheck: RepoToCheck -> repoToCheck.name.contains(searchQuery, ignoreCase = true) }
        val searchQueryContainsGroup = { repoToCheck: RepoToCheck -> repoToCheck.groupName?.contains(searchQuery, ignoreCase = true) ?: false }
        val searchQueryFilter = { repoToCheck: RepoToCheck ->
            searchQueryIsBlank || searchQuernContainsName(repoToCheck) || searchQueryContainsGroup(repoToCheck)
        }

        val groupNamesAreEmpty = groupNames.isEmpty()
        val groupNamesContainsGroup = { repoToCheck: RepoToCheck -> groupNames.contains(repoToCheck.groupName) }
        val groupNameFilter = { repoToCheck: RepoToCheck ->
            groupNamesAreEmpty || groupNamesContainsGroup(repoToCheck)
        }

        return this.filter { repoToCheck ->
            searchQueryFilter(repoToCheck) && groupNameFilter(repoToCheck)
        }
    }

    sealed interface St {
        data object Initializing : St
        data class Success(
            val reposToCheck: List<RepoToCheck>,
            val reposToCheckFiltered: List<RepoToCheck>,
            val appSettings: AppSettings,
            val searchQuery: String,
            val groupNameFilters: Set<String>,
            val groupNameFilterSizes: Map<String, Int>,
            val groupNameFiltersSelected: Set<String>,
        ) : St
        data class Error(val throwable: Throwable) : St
    }

    sealed interface Act {
        data object Reload : Act
        data class DeleteRepoToCheck(val repoToCheck: RepoToCheck) : Act
        data class GroupNameFilterSelected(val isSelected: Boolean, val groupName: String) : Act
        data class SearchQueryChanged(val searchQuery: String) : Act
    }
}
