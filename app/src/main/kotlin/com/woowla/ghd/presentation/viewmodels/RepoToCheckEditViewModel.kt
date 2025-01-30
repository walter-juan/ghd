package com.woowla.ghd.presentation.viewmodels

import com.freeletics.flowredux.dsl.ChangedState
import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import com.freeletics.flowredux.dsl.State
import com.woowla.ghd.domain.entities.RepoToCheck
import com.woowla.ghd.domain.services.RepoToCheckService
import com.woowla.ghd.utils.FlowReduxViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
class RepoToCheckEditViewModel(
    stateMachine: RepoToCheckEditStateMachine,
) : FlowReduxViewModel<RepoToCheckEditStateMachine.St, RepoToCheckEditStateMachine.Act>(stateMachine)

@OptIn(ExperimentalCoroutinesApi::class)
class RepoToCheckEditStateMachine(
    private val repoToCheckId: Long?,
    private val repoToCheckService: RepoToCheckService,
) : FlowReduxStateMachine<RepoToCheckEditStateMachine.St, RepoToCheckEditStateMachine.Act>(initialState = St.Loading) {
    init {
        spec {
            inState<St.Loading> {
                onEnter { state ->
                    load(state)
                }
            }
            inState<St.Success> {
                on<Act.Save> { action, state ->
                    save(state, action)
                }
            }
        }
    }

    private suspend fun load(state: State<St.Loading>): ChangedState<St> {
        return if (repoToCheckId == null) {
            state.override { St.Success(RepoToCheck.newInstance()) }
        } else {
            repoToCheckService
                .get(repoToCheckId)
                .fold(
                    onSuccess = { repoToCheck ->
                        state.override { St.Success(repoToCheck) }
                    },
                    onFailure = {
                        state.override { St.Error(it) }
                    }
                )
        }
    }

    private suspend fun save(state: State<St.Success>, action: Act.Save): ChangedState<St> {
        val updateRequest = state.snapshot.repoToCheck.copy(
            owner = action.owner,
            name = action.name,
            groupName = action.groupName,
            pullBranchRegex = action.branchRegex,
            arePullRequestsEnabled = action.arePullRequestsEnabled,
            arePullRequestsNotificationsEnabled = action.arePullRequestsNotificationsEnabled,
            areReleasesEnabled = action.areReleasesEnabled,
            areReleasesNotificationsEnabled = action.areReleasesNotificationsEnabled,
        )
        return repoToCheckService
            .save(updateRequest)
            .fold(
                onSuccess = {
                    state.mutate { copy(savedSuccessfully = true) }
                },
                onFailure = { _ ->
                    state.mutate { copy(savedSuccessfully = false) }
                },
            )
    }

    sealed interface St {
        data object Loading : St
        data class Success(val repoToCheck: RepoToCheck, val savedSuccessfully: Boolean? = null) : St
        data class Error(val throwable: Throwable) : St
    }
    sealed interface Act {
        data class Save(
            val owner: String,
            val name: String,
            val groupName: String,
            val branchRegex: String,
            val arePullRequestsEnabled: Boolean,
            val arePullRequestsNotificationsEnabled: Boolean,
            val areReleasesEnabled: Boolean,
            val areReleasesNotificationsEnabled: Boolean,
        ) : Act
    }
}