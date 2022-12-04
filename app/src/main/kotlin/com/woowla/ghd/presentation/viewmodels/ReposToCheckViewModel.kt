package com.woowla.ghd.presentation.viewmodels

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.coroutineScope
import com.woowla.ghd.domain.entities.RepoToCheck
import com.woowla.ghd.domain.usecases.DeleteRepoToCheckUseCase
import com.woowla.ghd.domain.usecases.ExportRepoToCheckUseCase
import com.woowla.ghd.domain.usecases.GetAllReposToCheckUseCase
import com.woowla.ghd.domain.usecases.ImportRepoToCheckUseCase
import com.woowla.ghd.eventbus.Event
import com.woowla.ghd.eventbus.EventBus
import java.io.File
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReposToCheckViewModel(
    private val getAllReposToCheckUseCase: GetAllReposToCheckUseCase = GetAllReposToCheckUseCase(),
    private val importRepoToCheckUseCase: ImportRepoToCheckUseCase = ImportRepoToCheckUseCase(),
    private val exportRepoToCheckUseCase: ExportRepoToCheckUseCase = ExportRepoToCheckUseCase(),
    private val deleteRepoToCheckUseCase: DeleteRepoToCheckUseCase = DeleteRepoToCheckUseCase(),
): ScreenModel {
    private val initialStateValue = State.Loading

    private val _state = MutableStateFlow<State>(initialStateValue)
    val state: StateFlow<State> = _state

    init {
        loadRepos()
        EventBus.subscribe(this, coroutineScope, Event.REPO_TO_CHECK_UPDATED) {
            reload()
        }
    }

    fun bulkImportRepo(file: File?) {
        _state.on<State.Success> {
            coroutineScope.launch {
                if (file != null) {
                    val content = file.readText()
                    importRepoToCheckUseCase.execute(content)
                }
            }
        }
    }

    fun bulkExportRepo(file: File?) {
        _state.on<State.Success> {
            coroutineScope.launch {
                if (file != null) {
                    exportRepoToCheckUseCase.execute().onSuccess { content ->
                        file.writeText(content)
                    }
                }
            }
        }
    }

    fun deleteRepo(repoToCheck: RepoToCheck) {
        _state.on<State.Success> {
            coroutineScope.launch {
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
        coroutineScope.launch {
            getAllReposToCheckUseCase.execute().fold(
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