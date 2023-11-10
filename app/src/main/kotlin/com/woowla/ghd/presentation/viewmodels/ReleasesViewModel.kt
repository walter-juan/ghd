package com.woowla.ghd.presentation.viewmodels

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.woowla.ghd.domain.entities.AppSettings
import com.woowla.ghd.domain.entities.Release
import com.woowla.ghd.domain.entities.SyncResult
import com.woowla.ghd.domain.services.AppSettingsService
import com.woowla.ghd.domain.services.ReleaseService
import com.woowla.ghd.domain.synchronization.Synchronizer
import com.woowla.ghd.eventbus.Event
import com.woowla.ghd.eventbus.EventBus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReleasesViewModel(
    private val synchronizer: Synchronizer = Synchronizer.INSTANCE,
    private val appSettingsService: AppSettingsService = AppSettingsService(),
    private val releaseService: ReleaseService = ReleaseService()
): ScreenModel {
    private val initialStateValue = State.Initializing

    private val _state = MutableStateFlow<State>(initialStateValue)
    val state: StateFlow<State> = _state

    init {
        loadReleases()
        EventBus.subscribe(this, screenModelScope, Event.SYNCHRONIZED) {
            reload()
        }
        EventBus.subscribe(this, screenModelScope, Event.SETTINGS_UPDATED) {
            reload()
        }
    }

    fun reload() {
        loadReleases()
    }

    private fun loadReleases() {
        screenModelScope.launch {
            val syncResult = synchronizer.getLastSyncResult().getOrNull()
            val appSettings = appSettingsService.get().getOrNull()

            releaseService.getAll()
                .fold(
                    onSuccess = { releases ->
                        val groupedReleases = releases
                            .groupBy { it.repoToCheck.groupName }
                            .map { GroupedReleases(groupName = it.key, releases = it.value) }
                            .sortedBy { it.groupName }
                        _state.value = State.Success(groupedReleases = groupedReleases, syncResult = syncResult, appSettings = appSettings)
                    },
                    onFailure = {
                        _state.value = State.Error(throwable = it)
                    }
                )
        }
    }

    sealed class State {
        object Initializing: State()
        data class Success(val groupedReleases: List<GroupedReleases>, val syncResult: SyncResult?, val appSettings: AppSettings?): State()
        data class  Error(val throwable: Throwable): State()
    }

    data class GroupedReleases(val groupName: String?, val releases: List<Release>)
}