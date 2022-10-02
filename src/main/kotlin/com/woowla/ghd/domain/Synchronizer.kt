package com.woowla.ghd.domain

import com.woowla.ghd.KermitLogger
import com.woowla.ghd.domain.entities.AppSettings
import com.woowla.ghd.domain.usecases.GetAppSettingsUseCase
import com.woowla.ghd.domain.usecases.SynchronizationUseCase
import com.woowla.ghd.eventbus.Event
import com.woowla.ghd.eventbus.EventBus
import com.woowla.ghd.extensions.timer
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class Synchronizer private constructor(
    private val synchronizationUseCase: SynchronizationUseCase = SynchronizationUseCase(),
    private val getAppSettingsUseCase: GetAppSettingsUseCase = GetAppSettingsUseCase()
) {
    companion object {
        val INSTANCE = Synchronizer()
    }

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    private var checkTimeout = AppSettings.defaultCheckTimeout
    private var timerJob: Job? = null

    private var syncJob: Job? = null

    init {
        reloadCheckTimeout(forceReload = true, startWithDelay = false)
        subscribe()
    }

    fun sync() {
        // don't sync if it's still running
        if (syncJob?.isCompleted ?: true) {
            KermitLogger.d("Synchronizer :: sync")
            syncJob = scope.launch {
                unsubscribe()
                synchronizationUseCase.execute()
                subscribe()
            }
        } else {
            KermitLogger.d("Synchronizer :: don't sync, still in progress")
        }
    }

    private fun reloadCheckTimeout(forceReload: Boolean = false, startWithDelay: Boolean = true) {
        scope.launch {
            val settingsCheckTimeout = getAppSettingsUseCase.execute().getOrNull()?.checkTimeout
            if (forceReload || (settingsCheckTimeout != null && settingsCheckTimeout != checkTimeout)) {
                checkTimeout = AppSettings.getValidCheckTimeout(settingsCheckTimeout)
                setTimer(startWithDelay = startWithDelay)
            }
        }
    }

    private fun setTimer(startWithDelay: Boolean = true) {
        timerJob?.cancel()
        val startDelay = if (startWithDelay) {
            checkTimeout.minutes
        } else {
            Duration.ZERO
        }
        timerJob = scope.timer(interval = checkTimeout.minutes, startDelay = startDelay) {
            sync()
        }
    }

    private fun subscribe() {
        EventBus.subscribe(this, scope, Event.APP_SETTINGS_UPDATED) {
            reloadCheckTimeout()
        }
    }

    private fun unsubscribe() {
        EventBus.unsubscribe(this)
    }
}