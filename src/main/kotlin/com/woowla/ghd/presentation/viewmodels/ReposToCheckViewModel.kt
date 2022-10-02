package com.woowla.ghd.presentation.viewmodels

import com.woowla.ghd.domain.entities.RepoToCheck
import com.woowla.ghd.domain.usecases.DeleteRepoToCheckUseCase
import com.woowla.ghd.domain.usecases.GetAllReposToCheckUseCaseUseCase
import com.woowla.ghd.domain.usecases.SaveRepoToCheckBulkUseCase
import com.woowla.ghd.eventbus.Event
import com.woowla.ghd.eventbus.EventBus
import java.io.File
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReposToCheckViewModel(
    private val getAllReposToCheckUseCaseUseCase: GetAllReposToCheckUseCaseUseCase = GetAllReposToCheckUseCaseUseCase(),
    private val saveRepoToCheckBulkUseCase: SaveRepoToCheckBulkUseCase = SaveRepoToCheckBulkUseCase(),
    private val deleteRepoToCheckUseCase: DeleteRepoToCheckUseCase = DeleteRepoToCheckUseCase(),
): ViewModel() {
    private val initialStateValue = State.Loading

    private val _state = MutableStateFlow<State>(initialStateValue)
    val state: StateFlow<State> = _state

    init {
        loadRepos()
        EventBus.subscribe(this, viewModelScope, Event.REPO_TO_CHECK_UPDATED) {
            reload()
        }
    }

    fun bulkImportRepo(file: File?) {
        _state.on<State.Success> {
            viewModelScope.launch {
                if (file != null) {
                    saveRepoToCheckBulkUseCase.execute(file)
                }
            }
        }
    }

    fun deleteRepo(repoToCheck: RepoToCheck) {
        _state.on<State.Success> {
            viewModelScope.launch {
                deleteRepoToCheckUseCase.execute(repoToCheck.id)
                reload()
            }
        }
    }

    fun reload() {
        loadRepos()
    }

    private fun loadRepos() {
        _state.value = State.Loading
        viewModelScope.launch {
            getAllReposToCheckUseCaseUseCase.execute().fold(
                onSuccess = {
                    _state.value = State.Success(reposToCheck = it)
                },
                onFailure = {
                    _state.value = State.Error(throwable = it)
                }
            )
        }
    }

    private inline fun <reified T: State> MutableStateFlow<State>.on(block: (T) -> Unit) {
        (value as? T)?.let(block)
    }

    sealed class State {
        object Loading: State()
        data class Success(val reposToCheck: List<RepoToCheck>): State()
        data class Error(val throwable: Throwable): State()
    }
}