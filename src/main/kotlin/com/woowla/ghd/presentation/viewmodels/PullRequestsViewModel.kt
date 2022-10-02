package com.woowla.ghd.presentation.viewmodels

import com.woowla.ghd.domain.entities.PullRequest
import com.woowla.ghd.domain.entities.PullRequestState
import com.woowla.ghd.domain.usecases.GetAllPullRequestsUseCase
import com.woowla.ghd.domain.usecases.GetAppSettingsUseCase
import com.woowla.ghd.domain.usecases.SetPullRequestSeenAt
import com.woowla.ghd.eventbus.Event
import com.woowla.ghd.eventbus.EventBus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class PullRequestsViewModel(
    private val getAppSettingsUseCase: GetAppSettingsUseCase = GetAppSettingsUseCase(),
    private val getAllPullRequestsUseCase: GetAllPullRequestsUseCase = GetAllPullRequestsUseCase(),
    private val setPullRequestSeenAt: SetPullRequestSeenAt = SetPullRequestSeenAt(),
): ViewModel() {
    private val initialStateValue = State.Loading(mapOf())

    private val _state = MutableStateFlow<State>(initialStateValue)
    val state: StateFlow<State> = _state

    init {
        loadPulls()
        EventBus.subscribe(this, viewModelScope, Event.SYNCHRONIZED) {
            reload()
        }
    }

    fun reload() {
        loadPulls()
    }

    fun markAsSeen(pullRequest: PullRequest) {
        _state.on<State.Success> {
            viewModelScope.launch {
                val appSeenAt = if (pullRequest.appSeen) {
                    null
                } else {
                    Clock.System.now()
                }
                setPullRequestSeenAt.execute(SetPullRequestSeenAt.Params(id = pullRequest.id, appSeenAt = appSeenAt))
                loadPulls()
            }
        }
    }

    private inline fun <reified T: State> MutableStateFlow<State>.on(block: (T) -> Unit) {
        (value as? T)?.let(block)
    }

    private fun loadPulls() {
        _state.value = State.Loading(_state.getPullsOrEmptyList())

        viewModelScope.launch {
            val synchronizedAt = getAppSettingsUseCase.execute().getOrNull()?.synchronizedAt
            getAllPullRequestsUseCase.execute()
                .fold(
                    onSuccess = { pullRequests ->
                        _state.value = State.Success(pulls = pullRequests.groupBy { it.state }, synchronizedAt = synchronizedAt)
                    },
                    onFailure = {
                        _state.value = State.Error(throwable = it)
                    }
                )
        }
    }

    private fun StateFlow<State>.getPullsOrEmptyList(): Map<PullRequestState, List<PullRequest>> {
        val lockedValue = value
        return when(lockedValue) {
            is State.Loading -> lockedValue.pulls
            is State.Success -> lockedValue.pulls
            is State.Error -> mapOf()
        }
    }

    sealed class State {
        data class Loading(val pulls: Map<PullRequestState, List<PullRequest>>): State()
        data class Success(val pulls: Map<PullRequestState, List<PullRequest>>, val synchronizedAt: Instant?): State()
        data class  Error(val throwable: Throwable): State()
    }
}