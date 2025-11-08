package com.woowla.ghd.presentation.viewmodels

import androidx.lifecycle.viewModelScope
import com.freeletics.flowredux.dsl.ChangedState
import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import com.freeletics.flowredux.dsl.State
import com.woowla.ghd.domain.entities.AppSettings
import com.woowla.ghd.domain.entities.RepoToCheck
import com.woowla.ghd.domain.services.AppSettingsService
import com.woowla.ghd.domain.services.RepoToCheckService
import com.woowla.ghd.domain.entities.Event
import com.woowla.ghd.core.eventbus.EventBus
import com.woowla.ghd.core.utils.FlowReduxViewModel
import com.woowla.ghd.domain.entities.RepoToCheckFilters
import com.woowla.ghd.domain.entities.filtersRepoToCheck
import com.woowla.ghd.domain.entities.groupNames
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
class ReposToCheckViewModel(
    stateMachine: ReposToCheckStateMachine,
    private val eventBus: EventBus,
) : FlowReduxViewModel<ReposToCheckStateMachine.St, ReposToCheckStateMachine.Act>(stateMachine) {
    init {
        eventBus.subscribe(this, viewModelScope, Event.REPO_TO_CHECK_UPDATED) {
            dispatch(ReposToCheckStateMachine.Act.Reload)
        }
        eventBus.subscribe(this, viewModelScope, Event.SETTINGS_UPDATED) {
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
                on<Act.SearchQueryChanged> { action, state ->
                    filter(state, searchQuery = action.searchQuery)
                }
                on<Act.GroupNameFilterChanged> { action, state ->
                    filter(state, groupName = action.groupName, isSelected = action.isSelected)
                }
                on<Act.FiltersChanged> { action, state ->
                    filter(state, filters = action.filters)
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

            repoToCheckService.getAll()
                .fold(
                    onSuccess = { reposToCheck ->
                        val allGroupNames = reposToCheck.mapNotNull { it.groupName }.distinct().filter { it.isNotBlank() }.toSet()
                        val allGroupNamesSizes = allGroupNames.associateWith { groupName ->
                            reposToCheck.count { it.groupName == groupName }
                        }

                        val releasesFiltered = reposToCheck.filter(
                            searchQuery = searchQuery,
                            filters = appSettings.filtersRepoToCheck,
                        )

                        state.override {
                            St.Success(
                                reposToCheck = reposToCheck,
                                reposToCheckFiltered = releasesFiltered,
                                appSettings = appSettings,
                                allGroupNames = allGroupNames,
                                allGroupNamesSizes = allGroupNamesSizes,
                                searchQuery = searchQuery,
                                filtersRepoToCheck = appSettings.filtersRepoToCheck,
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
            filters = state.snapshot.filtersRepoToCheck,
        )
        return state.mutate {
            this.copy(
                reposToCheckFiltered = reposToCheckFiltered,
                searchQuery = searchQuery,
                filtersRepoToCheck = filtersRepoToCheck,
            )
        }
    }

    private suspend fun filter(state: State<St.Success>, groupName: String, isSelected: Boolean): ChangedState<St> {
        val groupNameFiltersSelected = if (isSelected) {
            state.snapshot.filtersRepoToCheck.groupNames - groupName
        } else {
            state.snapshot.filtersRepoToCheck.groupNames + groupName
        }

        // clean up in case a group is no-available anymore
        val allGroupNames = state.snapshot.allGroupNames
        val groupNameFiltersSelectedRecalculated = groupNameFiltersSelected.filter { allGroupNames.contains(it) }.toSet()

        val appSettings = AppSettings.filtersRepoToCheck.groupNames.modify(state.snapshot.appSettings) { groupNameFiltersSelectedRecalculated }

        appSettingsService.save(appSettings)

        // no change because the SETTINGS_UPDATED will be triggered automatically
        return state.noChange()
    }

    private suspend fun filter(state: State<St.Success>, filters: RepoToCheckFilters): ChangedState<St> {
        val appSettings = AppSettings.filtersRepoToCheck.modify(state.snapshot.appSettings) { filters }

        appSettingsService.save(appSettings)

        // no change because the SETTINGS_UPDATED will be triggered automatically
        return state.noChange()
    }

    private fun List<RepoToCheck>.filter(
        searchQuery: String,
        filters: RepoToCheckFilters,
    ): List<RepoToCheck> {
        val searchQueryIsBlank = searchQuery.isBlank()
        val searchQuernContainsName = { repoToCheck: RepoToCheck -> repoToCheck.repository?.name?.contains(searchQuery, ignoreCase = true) ?: false  }
        val searchQueryContainsGroup = { repoToCheck: RepoToCheck -> repoToCheck.groupName?.contains(searchQuery, ignoreCase = true) ?: false }
        val searchQueryFilter = { repoToCheck: RepoToCheck ->
            searchQueryIsBlank || searchQuernContainsName(repoToCheck) || searchQueryContainsGroup(repoToCheck)
        }

        val groupNames = filters.groupNames
        val groupNamesAreEmpty = groupNames.isEmpty()
        val groupNamesContainsGroup = { repoToCheck: RepoToCheck -> groupNames.contains(repoToCheck.groupName) }
        val groupNameFilter = { repoToCheck: RepoToCheck ->
            groupNamesAreEmpty || groupNamesContainsGroup(repoToCheck)
        }

        val otherFiltersFilter = otherFiltersFilter@{ repoToCheck: RepoToCheck ->
            // no filter active
            if (filters.anyFilterActive().not()) {
                return@otherFiltersFilter true
            }
            // at least one filter active
            var matches = false
            if (filters.pullRequestSyncEnabled) {
                matches = matches || repoToCheck.arePullRequestsEnabled
            }
            if (filters.pullRequestNotificationsEnabled) {
                matches = matches || repoToCheck.arePullRequestsNotificationsEnabled
            }
            if (filters.pullRequestBranchFilterActive) {
                matches = matches || !repoToCheck.pullBranchRegex.isNullOrBlank()
            }
            if (filters.releasesSyncEnabled) {
                matches = matches || repoToCheck.areReleasesEnabled
            }
            if (filters.releasesNotificationsEnabled) {
                matches = matches || repoToCheck.areReleasesNotificationsEnabled
            }
            matches
        }

        return this.filter { repoToCheck ->
            searchQueryFilter(repoToCheck) && groupNameFilter(repoToCheck) && otherFiltersFilter(repoToCheck)
        }
    }

    sealed interface St {
        data object Initializing : St
        data class Success(
            val reposToCheck: List<RepoToCheck>,
            val reposToCheckFiltered: List<RepoToCheck>,
            val appSettings: AppSettings,
            val searchQuery: String,
            val allGroupNames: Set<String>,
            val allGroupNamesSizes: Map<String, Int>,
            val filtersRepoToCheck: RepoToCheckFilters,
        ) : St
        data class Error(val throwable: Throwable) : St
    }

    sealed interface Act {
        data object Reload : Act
        data class DeleteRepoToCheck(val repoToCheck: RepoToCheck) : Act
        data class SearchQueryChanged(val searchQuery: String) : Act
        data class GroupNameFilterChanged(val isSelected: Boolean, val groupName: String) : Act
        data class FiltersChanged(val filters: RepoToCheckFilters) : Act
    }
}
