package com.woowla.ghd.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woowla.ghd.domain.entities.AppSettings
import com.woowla.ghd.domain.entities.ReleaseWithRepo
import com.woowla.ghd.domain.entities.SyncResultWithEntitiesAndRepos
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
): ViewModel() {
    private val initialStateValue = State.Initializing

    private val _state = MutableStateFlow<State>(initialStateValue)
    val state: StateFlow<State> = _state

    init {
        loadReleases()
        EventBus.subscribe(this, viewModelScope, Event.SYNCHRONIZED) {
            reload()
        }
        EventBus.subscribe(this, viewModelScope, Event.SETTINGS_UPDATED) {
            reload()
        }
    }

    fun reload() {
        loadReleases()
    }

    private fun loadReleases() {
        viewModelScope.launch {
            val syncResult = synchronizer.getLastSyncResult().getOrNull()
            val appSettings = appSettingsService.get().getOrNull()

            releaseService.getAll()
                .fold(
                    onSuccess = { releases ->
                        val groupedReleases = releases
                            .groupBy { it.repoToCheck.groupName }
                            .map { GroupedReleases(groupName = it.key, releases = it.value) }
                            .sortedBy { it.groupName }
                        _state.value = State.Success(groupedReleases = groupedReleases, syncResultWithEntities = syncResult, appSettings = appSettings)
                    },
                    onFailure = {
                        _state.value = State.Error(throwable = it)
                    }
                )
        }
    }

    sealed class State {
        object Initializing: State()
        data class Success(val groupedReleases: List<GroupedReleases>, val syncResultWithEntities: SyncResultWithEntitiesAndRepos?, val appSettings: AppSettings?): State()
        data class Error(val throwable: Throwable): State()
    }

    data class GroupedReleases(val groupName: String?, val releases: List<ReleaseWithRepo>)
}