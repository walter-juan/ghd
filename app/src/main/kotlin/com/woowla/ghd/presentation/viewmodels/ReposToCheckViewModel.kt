package com.woowla.ghd.presentation.viewmodels

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.woowla.ghd.domain.entities.AppSettings
import com.woowla.ghd.domain.entities.RepoToCheck
import com.woowla.ghd.domain.services.AppSettingsService
import com.woowla.ghd.domain.services.RepoToCheckService
import com.woowla.ghd.eventbus.Event
import com.woowla.ghd.eventbus.EventBus
import java.io.File
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReposToCheckViewModel(
    private val repoToCheckService: RepoToCheckService = RepoToCheckService(),
    private val appSettingsService: AppSettingsService = AppSettingsService(),
): ScreenModel {
    private val initialStateValue = State.Initializing

    private val _state = MutableStateFlow<State>(initialStateValue)
    val state: StateFlow<State> = _state

    init {
        loadRepos()
        EventBus.subscribe(this, screenModelScope, Event.REPO_TO_CHECK_UPDATED) {
            reload()
        }
        EventBus.subscribe(this, screenModelScope, Event.SETTINGS_UPDATED) {
            reload()
        }
    }

    fun bulkImportRepo(file: File?) {
        screenModelScope.launch {
            if (file != null) {
                val content = file.readText()
                repoToCheckService.import(content)
            }
        }
    }

    fun bulkExportRepo(file: File?) {
        screenModelScope.launch {
            if (file != null) {
                repoToCheckService.export().onSuccess { content ->
                    file.writeText(content)
                }
            }
        }
    }

    fun deleteRepo(repoToCheck: RepoToCheck) {
        screenModelScope.launch {
            repoToCheckService.delete(repoToCheck.id)
            reload()
        }
    }

    fun reload() {
        loadRepos()
    }

    private fun loadRepos() {
        screenModelScope.launch {
            val appSettings = appSettingsService.get().getOrNull()

            repoToCheckService.getAll().fold(
                onSuccess = {
                    _state.value = State.Success(reposToCheck = it, appSettings = appSettings)
                },
                onFailure = {
                    _state.value = State.Error(throwable = it)
                }
            )
        }
    }

    sealed class State {
        object Initializing: State()
        data class Success(val reposToCheck: List<RepoToCheck>, val appSettings: AppSettings?): State()
        data class Error(val throwable: Throwable): State()
    }
}