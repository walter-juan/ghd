package com.woowla.ghd.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woowla.ghd.domain.entities.AppSettings
import com.woowla.ghd.domain.entities.PullRequest
import com.woowla.ghd.domain.entities.PullRequestStateWithDraft
import com.woowla.ghd.domain.entities.SyncResult
import com.woowla.ghd.domain.services.AppSettingsService
import com.woowla.ghd.domain.services.PullRequestService
import com.woowla.ghd.domain.synchronization.Synchronizer
import com.woowla.ghd.eventbus.Event
import com.woowla.ghd.eventbus.EventBus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class PullRequestsViewModel(
    private val synchronizer: Synchronizer = Synchronizer.INSTANCE,
    private val appSettingsService: AppSettingsService = AppSettingsService(),
    private val pullRequestService: PullRequestService = PullRequestService(),
): ViewModel() {
    private val initialStateValue = State.Initializing

    private val _state = MutableStateFlow<State>(initialStateValue)
    val state: StateFlow<State> = _state

    init {
        loadPulls()
        EventBus.subscribe(this, viewModelScope, Event.SYNCHRONIZED) {
            reload()
        }
        EventBus.subscribe(this, viewModelScope, Event.SETTINGS_UPDATED) {
            reload()
        }
    }

    fun reload() {
        loadPulls()
    }

    fun markAsSeen(pullRequest: PullRequest) {
        viewModelScope.launch {
            val appSeenAt = if (pullRequest.appSeen) {
                null
            } else {
                Clock.System.now()
            }
            pullRequestService.markAsSeen(id = pullRequest.id, appSeenAt = appSeenAt)
            loadPulls()
        }
    }

    private fun loadPulls() {
        viewModelScope.launch {
            val syncResult = synchronizer.getLastSyncResult().getOrNull()
            val appSettings = appSettingsService.get().getOrNull()

            pullRequestService.getAll()
                .fold(
                    onSuccess = { pullRequests ->
                        val groupedPullRequests = pullRequests
                            .groupBy { it.stateWithDraft }
                            .map { GroupedPullRequests(pullRequestStateWithDraft = it.key, pullRequests = it.value) }
                        _state.value = State.Success(groupedPullRequests = groupedPullRequests, syncResult = syncResult, appSettings = appSettings)
                    },
                    onFailure = {
                        _state.value = State.Error(throwable = it)
                    }
                )
        }
    }

    sealed class State {
        object Initializing: State()
        data class Success(val groupedPullRequests: List<GroupedPullRequests>, val syncResult: SyncResult?, val appSettings: AppSettings?): State()
        data class  Error(val throwable: Throwable): State()
    }

    data class GroupedPullRequests(val pullRequestStateWithDraft: PullRequestStateWithDraft, val pullRequests: List<PullRequest>)
}