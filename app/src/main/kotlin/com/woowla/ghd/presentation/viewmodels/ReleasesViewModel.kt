package com.woowla.ghd.presentation.viewmodels

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.coroutineScope
import com.woowla.ghd.domain.entities.Release
import com.woowla.ghd.domain.usecases.GetAllReleasesUseCase
import com.woowla.ghd.domain.usecases.GetSyncSettingsUseCase
import com.woowla.ghd.eventbus.Event
import com.woowla.ghd.eventbus.EventBus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant

class ReleasesViewModel(
    private val getSyncSettingsUseCase: GetSyncSettingsUseCase = GetSyncSettingsUseCase(),
    private val getAllReleasesUseCase: GetAllReleasesUseCase = GetAllReleasesUseCase()
): ScreenModel {
    private val initialStateValue = State.Loading(mapOf())

    private val _state = MutableStateFlow<State>(initialStateValue)
    val state: StateFlow<State> = _state

    init {
        loadReleases()
        EventBus.subscribe(this, coroutineScope, Event.SYNCHRONIZED) {
            reload()
        }
    }

    fun reload() {
        loadReleases()
    }

    private fun loadReleases() {
        _state.value = State.Loading(_state.getReleasesOrEmptyList())

        coroutineScope.launch {
            val synchronizedAt = getSyncSettingsUseCase.execute().getOrNull()?.synchronizedAt

            getAllReleasesUseCase.execute()
                .fold(
                    onSuccess = { releases ->
                        _state.value = State.Success(releases = releases.groupBy { it.repoToCheck.groupName }, synchronizedAt = synchronizedAt)
                    },
                    onFailure = {
                        _state.value = State.Error(throwable = it)
                    }
                )
        }
    }

    private fun StateFlow<State>.getReleasesOrEmptyList(): Map<String?, List<Release>> {
        val lockedValue = value
        return when(lockedValue) {
            is State.Loading -> lockedValue.releases
            is State.Success -> lockedValue.releases
            is State.Error -> mapOf()
        }
    }

    sealed class State {
        data class Loading(val releases: Map<String?, List<Release>>): State()
        data class Success(val releases: Map<String?, List<Release>>, val synchronizedAt: Instant?): State()
        data class  Error(val throwable: Throwable): State()
    }
}