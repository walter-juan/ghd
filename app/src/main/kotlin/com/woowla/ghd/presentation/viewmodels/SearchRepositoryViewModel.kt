package com.woowla.ghd.presentation.viewmodels

import arrow.optics.optics
import com.freeletics.flowredux.dsl.ChangedState
import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import com.freeletics.flowredux.dsl.State
import com.woowla.ghd.core.AppLogger
import com.woowla.ghd.domain.entities.Repository
import com.woowla.ghd.domain.services.RepositoryService
import com.woowla.ghd.core.utils.FlowReduxViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
class SearchRepositoryViewModel(
    stateMachine: SearchRepositoryStateMachine,
) : FlowReduxViewModel<SearchRepositoryStateMachine.St, SearchRepositoryStateMachine.Act>(stateMachine)

@OptIn(ExperimentalCoroutinesApi::class)
class SearchRepositoryStateMachine(
    private val repositoryService: RepositoryService,
    private val appLogger: AppLogger,
) : FlowReduxStateMachine<SearchRepositoryStateMachine.St, SearchRepositoryStateMachine.Act>(
    initialState = St.Success("", ""),
) {
    init {
        spec {
            inState<St.Success> {
                on<Act.Search> { action, state ->
                    state.override { St.Loading(text = state.snapshot.text, owner = state.snapshot.owner) }
                }
                on<Act.UpdateText> { action, state ->
                    state.mutate { St.Success.text.modify(this) { action.text } }
                }
                on<Act.UpdateOwner> { action, state ->
                    state.mutate { St.Success.owner.modify(this) { action.owner } }
                }
            }
            inState<St.Loading> {
                onEnter { state ->
                    search(state)
                }
            }
            inState<St.Error> {
                on<Act.Search> { action, state ->
                    state.override { St.Loading(text = state.snapshot.text, owner = state.snapshot.owner) }
                }
                on<Act.UpdateText> { action, state ->
                    state.mutate { St.Error.text.modify(this) { action.text } }
                }
                on<Act.UpdateOwner> { action, state ->
                    state.mutate { St.Error.owner.modify(this) { action.owner } }
                }
            }
        }
    }

    private suspend fun search(state: State<St.Loading>): ChangedState<St> {
        return try {
            val result = repositoryService.search(
                text = state.snapshot.text,
                user = state.snapshot.owner,
            )
            result.fold(
                onSuccess = { repositories ->
                    state.override { St.Success(
                        text = state.snapshot.text,
                        owner = state.snapshot.owner,
                        repositories = repositories,
                    ) }
                },
                onFailure = { throwable ->
                    state.override { St.Error(
                        text = state.snapshot.text,
                        owner = state.snapshot.owner,
                        throwable = throwable,
                    ) }
                },
            )
        } catch (e: Throwable) {
            state.override { St.Error(
                text = state.snapshot.text,
                owner = state.snapshot.owner,
                throwable = e,
            ) }
        }
    }

    sealed class St(
        open val text: String,
        open val owner: String,
    ) {
        @optics data class Success(
            override val text: String,
            override val owner: String,
            val repositories: List<Repository> = listOf()
        ) : St(text, owner) {
            companion object
        }

        data class Loading(
            override val text: String,
            override val owner: String,
        ) : St(text, owner)

        @optics data class Error(
            override val text: String,
            override val owner: String,
            val throwable: Throwable
        ) : St(text, owner) {
            companion object
        }
    }

    sealed interface Act {
        data class UpdateText(val text: String) : Act
        data class UpdateOwner(val owner: String) : Act
        data object Search : Act
    }
}