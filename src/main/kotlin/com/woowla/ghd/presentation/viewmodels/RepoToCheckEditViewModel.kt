package com.woowla.ghd.presentation.viewmodels

import com.woowla.ghd.domain.entities.RepoToCheck
import com.woowla.ghd.domain.mappers.DomainMappers
import com.woowla.ghd.domain.requests.UpsertRepoToCheckRequest
import com.woowla.ghd.domain.usecases.SaveRepoToCheckUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RepoToCheckEditViewModel(
    private val repoToCheck: RepoToCheck?,
    private val saveRepoToCheckUseCase: SaveRepoToCheckUseCase = SaveRepoToCheckUseCase(),
): ViewModel() {

    private val initialStateValue: UpsertRepoToCheckRequest = if (repoToCheck == null) {
        UpsertRepoToCheckRequest.newInstance()
    } else {
        DomainMappers.INSTANCE.repoToCheckToUpsertRequest(repoToCheck)
    }
    private val _updateRequest = MutableStateFlow(initialStateValue)
    val updateRequest: StateFlow<UpsertRepoToCheckRequest> = _updateRequest

    private val _events = MutableSharedFlow<Events>()
    val events: SharedFlow<Events> = _events

    fun ownerUpdated(value: String) {
        _updateRequest.value = _updateRequest.value.copy(owner = value)
    }

    fun nameUpdated(value: String) {
        _updateRequest.value = _updateRequest.value.copy(name = value)
    }

    fun pullNotificationsEnabledUpdated(value: Boolean) {
        _updateRequest.value = _updateRequest.value.copy(pullNotificationsEnabled = value)
    }

    fun releaseNotificationsEnabledUpdated(value: Boolean) {
        _updateRequest.value = _updateRequest.value.copy(releaseNotificationsEnabled = value)
    }

    fun groupUpdated(value: String) {
        _updateRequest.value = _updateRequest.value.copy(groupName = value)
    }

    fun branchRegexUpdated(value: String) {
        _updateRequest.value = _updateRequest.value.copy(pullBranchRegex = value)
    }

    fun saveRepo() {
        viewModelScope.launch {
            saveRepoToCheckUseCase
                .execute(_updateRequest.value)
                .onSuccess {
                    _events.emit(Events.Saved)
                }
        }
    }

    sealed class Events {
        object Saved: Events()
    }
}