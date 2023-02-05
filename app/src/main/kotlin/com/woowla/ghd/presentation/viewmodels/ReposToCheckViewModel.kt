package com.woowla.ghd.presentation.viewmodels

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.coroutineScope
import com.woowla.ghd.domain.entities.RepoToCheck
import com.woowla.ghd.domain.services.RepoToCheckService
import com.woowla.ghd.eventbus.Event
import com.woowla.ghd.eventbus.EventBus
import java.io.File
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReposToCheckViewModel(
    private val repoToCheckService: RepoToCheckService = RepoToCheckService(),
): ScreenModel {
    private val initialStateValue = State.Initializing

    private val _state = MutableStateFlow<State>(initialStateValue)
    val state: StateFlow<State> = _state

    init {
        loadRepos()
        EventBus.subscribe(this, coroutineScope, Event.REPO_TO_CHECK_UPDATED) {
            reload()
        }
    }

    fun bulkImportRepo(file: File?) {
        coroutineScope.launch {
            if (file != null) {
                val content = file.readText()
                repoToCheckService.import(content)
            }
        }
    }

    fun bulkExportRepo(file: File?) {
        coroutineScope.launch {
            if (file != null) {
                repoToCheckService.export().onSuccess { content ->
                    file.writeText(content)
                }
            }
        }
    }

    fun deleteRepo(repoToCheck: RepoToCheck) {
        coroutineScope.launch {
            repoToCheckService.delete(repoToCheck.id)
            reload()
        }
    }

    fun reload() {
        loadRepos()
    }

    private fun loadRepos() {
        coroutineScope.launch {
            repoToCheckService.getAll().fold(
                onSuccess = {
                    _state.value = State.Success(reposToCheck = it)
                },
                onFailure = {
                    _state.value = State.Error(throwable = it)
                }
            )
        }
    }

    sealed class State {
        object Initializing: State()
        data class Success(val reposToCheck: List<RepoToCheck>): State()
        data class Error(val throwable: Throwable): State()
    }
}