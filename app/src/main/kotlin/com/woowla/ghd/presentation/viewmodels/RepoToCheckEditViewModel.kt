package com.woowla.ghd.presentation.viewmodels

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.coroutineScope
import com.woowla.ghd.domain.entities.RepoToCheck
import com.woowla.ghd.domain.mappers.toUpsertRepoToCheckRequest
import com.woowla.ghd.domain.requests.UpsertRepoToCheckRequest
import com.woowla.ghd.domain.services.RepoToCheckService
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RepoToCheckEditViewModel(
    private val repoToCheck: RepoToCheck?,
    private val repoToCheckService: RepoToCheckService = RepoToCheckService(),
): ScreenModel {

    private val initialStateValue: UpsertRepoToCheckRequest = if (repoToCheck == null) {
        UpsertRepoToCheckRequest.newInstance()
    } else {
        repoToCheck.toUpsertRepoToCheckRequest()
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

    fun groupUpdated(value: String) {
        _updateRequest.value = _updateRequest.value.copy(groupName = value)
    }

    fun branchRegexUpdated(value: String) {
        _updateRequest.value = _updateRequest.value.copy(pullBranchRegex = value)
    }

    fun saveRepo() {
        coroutineScope.launch {
            repoToCheckService.save(_updateRequest.value)
                .onSuccess {
                    _events.emit(Events.Saved)
                }
        }
    }

    sealed class Events {
        object Saved: Events()
    }
}