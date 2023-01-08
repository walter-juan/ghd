package com.woowla.ghd.presentation.viewmodels

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.coroutineScope
import com.woowla.ghd.domain.entities.Release
import com.woowla.ghd.domain.services.ReleaseService
import com.woowla.ghd.domain.services.SyncSettingsService
import com.woowla.ghd.eventbus.Event
import com.woowla.ghd.eventbus.EventBus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant

class ReleasesViewModel(
    private val syncSettingsService: SyncSettingsService = SyncSettingsService(),
    private val releaseService: ReleaseService = ReleaseService()
): ScreenModel {
    private val initialStateValue = State.Initializing

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
        coroutineScope.launch {
            val synchronizedAt = syncSettingsService.get().getOrNull()?.synchronizedAt

            releaseService.getAll()
                .fold(
                    onSuccess = { releases ->
                        val groupedReleases = releases
                            .groupBy { it.repoToCheck.groupName }
                            .map { GroupedReleases(groupName = it.key, releases = it.value) }
                            .sortedBy { it.groupName }
                        _state.value = State.Success(groupedReleases = groupedReleases, synchronizedAt = synchronizedAt)
                    },
                    onFailure = {
                        _state.value = State.Error(throwable = it)
                    }
                )
        }
    }

    sealed class State {
        object Initializing: State()
        data class Success(val groupedReleases: List<GroupedReleases>, val synchronizedAt: Instant?): State()
        data class  Error(val throwable: Throwable): State()
    }

    data class GroupedReleases(val groupName: String?, val releases: List<Release>)
}