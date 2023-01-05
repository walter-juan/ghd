package com.woowla.ghd.presentation.viewmodels

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.coroutineScope
import com.woowla.ghd.domain.entities.PullRequest
import com.woowla.ghd.domain.entities.PullRequestState
import com.woowla.ghd.domain.usecases.GetAllPullRequestsUseCase
import com.woowla.ghd.domain.usecases.GetSyncSettingsUseCase
import com.woowla.ghd.domain.usecases.SetPullRequestSeenAt
import com.woowla.ghd.eventbus.Event
import com.woowla.ghd.eventbus.EventBus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class PullRequestsViewModel(
    private val getSyncSettingsUseCase: GetSyncSettingsUseCase = GetSyncSettingsUseCase(),
    private val getAllPullRequestsUseCase: GetAllPullRequestsUseCase = GetAllPullRequestsUseCase(),
    private val setPullRequestSeenAt: SetPullRequestSeenAt = SetPullRequestSeenAt(),
): ScreenModel {
    private val initialStateValue = State.Initializing

    private val _state = MutableStateFlow<State>(initialStateValue)
    val state: StateFlow<State> = _state

    init {
        loadPulls()
        EventBus.subscribe(this, coroutineScope, Event.SYNCHRONIZED) {
            reload()
        }
    }

    fun reload() {
        loadPulls()
    }

    fun markAsSeen(pullRequest: PullRequest) {
        _state.on<State.Success> {
            coroutineScope.launch {
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
        coroutineScope.launch {
            val synchronizedAt = getSyncSettingsUseCase.execute().getOrNull()?.synchronizedAt
            getAllPullRequestsUseCase.execute()
                .fold(
                    onSuccess = { pullRequests ->
                        val groupedPullRequests = pullRequests
                            .groupBy { it.state }
                            .map { GroupedPullRequests(pullRequestState = it.key, pullRequests = it.value) }
                        _state.value = State.Success(groupedPullRequests = groupedPullRequests, synchronizedAt = synchronizedAt)
                    },
                    onFailure = {
                        _state.value = State.Error(throwable = it)
                    }
                )
        }
    }

    sealed class State {
        object Initializing: State()
        data class Success(val groupedPullRequests: List<GroupedPullRequests>, val synchronizedAt: Instant?): State()
        data class  Error(val throwable: Throwable): State()
    }

    data class GroupedPullRequests(val pullRequestState: PullRequestState, val pullRequests: List<PullRequest>)
}