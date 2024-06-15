package com.woowla.ghd.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woowla.ghd.domain.entities.RepoToCheck
import com.woowla.ghd.domain.services.RepoToCheckService
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RepoToCheckEditViewModel(
    private val repoToCheckId: Long?,
    private val repoToCheckService: RepoToCheckService = RepoToCheckService(),
): ViewModel() {
    private val initialStateValue = State.Initializing

    private val _state = MutableStateFlow<State>(initialStateValue)
    val state: StateFlow<State> = _state

    private val _events = MutableSharedFlow<Events>()
    val events: SharedFlow<Events> = _events

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            if (repoToCheckId == null) {
                _state.value = State.Success(RepoToCheck.newInstance())
            } else {
                repoToCheckService
                    .get(repoToCheckId)
                    .fold(
                        onSuccess = { repoToCheck ->
                            _state.value = State.Success(repoToCheck)
                        },
                        onFailure = {
                            _state.value = State.Error(it)
                        }
                    )

            }
        }
    }

    fun saveRepo(
        owner: String,
        name: String,
        groupName: String,
        branchRegex: String,
        arePullRequestsEnabled: Boolean,
        areReleasesEnabled: Boolean,
    ) {
        _state.on<State.Success> {
            val updateRequest = it.repoToCheck.copy(
                owner = owner,
                name = name,
                groupName = groupName,
                pullBranchRegex = branchRegex,
                arePullRequestsEnabled = arePullRequestsEnabled,
                areReleasesEnabled = areReleasesEnabled,
            )
            viewModelScope.launch {
                repoToCheckService.save(updateRequest)
                    .onSuccess {
                        _events.emit(Events.Saved)
                    }
            }
        }
    }

    private inline fun <reified T: State> MutableStateFlow<State>.on(block: (T) -> Unit) {
        (value as? T)?.let(block)
    }

    sealed class State {
        object Initializing: State()
        data class Success(val repoToCheck: RepoToCheck): State()
        data class Error(val throwable: Throwable): State()
    }

    sealed class Events {
        object Saved: Events()
    }
}