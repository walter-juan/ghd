package com.woowla.ghd.presentation.viewmodels

import arrow.optics.optics
import com.freeletics.flowredux.dsl.ChangedState
import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import com.freeletics.flowredux.dsl.State
import com.woowla.ghd.domain.entities.RepoToCheck
import com.woowla.ghd.domain.services.RepoToCheckService
import com.woowla.ghd.core.utils.FlowReduxViewModel
import com.woowla.ghd.domain.entities.Repository
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
                on<Act.UpdateRepository> { action, state ->
                    state.mutate { St.Success.repoToCheck.modify(this) { it.copy(repository = Repository(owner = action.owner, name = action.name)) } }
                }
                on<Act.CleanUpSaveSuccessfully> { _, state ->
                    state.mutate { copy(savedSuccessfully = null) }
                }
            }
        }
    }

    private suspend fun load(state: State<St.Loading>): ChangedState<St> {
        return if (repoToCheckId == null) {
            state.override { St.Success(Mode.CREATE, RepoToCheck.newInstance()) }
        } else {
            repoToCheckService
                .get(repoToCheckId)
                .fold(
                    onSuccess = { repoToCheck ->
                        state.override { St.Success(Mode.UPDATE, repoToCheck) }
                    },
                    onFailure = {
                        state.override { St.Error(it) }
                    }
                )
        }
    }

    private suspend fun save(state: State<St.Success>, action: Act.Save): ChangedState<St> {
        val updateRequestResult = runCatching {
            val repository = Repository.fromUrl(action.repositoryUrl)
            state.snapshot.repoToCheck.copy(
                repository = repository,
                groupName = action.groupName,
                pullBranchRegex = action.branchRegex,
                arePullRequestsEnabled = action.arePullRequestsEnabled,
                arePullRequestsNotificationsEnabled = action.arePullRequestsNotificationsEnabled,
                areReleasesEnabled = action.areReleasesEnabled,
                areReleasesNotificationsEnabled = action.areReleasesNotificationsEnabled,
            )
        }
        return updateRequestResult
            .map { updateRequest ->
                repoToCheckService.save(updateRequest)
            }
            .fold(
                onSuccess = {
                    state.mutate { copy(savedSuccessfully = true) }
                },
                onFailure = { _ ->
                    state.mutate { copy(savedSuccessfully = false) }
                },
            )
    }

    enum class Mode {
        CREATE, UPDATE
    }

    sealed interface St {
        data object Loading : St
        @optics data class Success(val mode: Mode, val repoToCheck: RepoToCheck, val savedSuccessfully: Boolean? = null) : St {
            companion object
        }
        data class Error(val throwable: Throwable) : St
    }
    sealed interface Act {
        data object CleanUpSaveSuccessfully : Act
        data class UpdateRepository(val repository: String) : Act {
            val owner = repository.substringBefore("/", missingDelimiterValue = "")
            val name = repository.substringAfter("/", missingDelimiterValue = "")
        }
        data class Save(
            val repositoryUrl: String,
            val groupName: String,
            val branchRegex: String,
            val arePullRequestsEnabled: Boolean,
            val arePullRequestsNotificationsEnabled: Boolean,
            val areReleasesEnabled: Boolean,
            val areReleasesNotificationsEnabled: Boolean,
        ) : Act
    }
}