package com.woowla.ghd.domain.synchronization

import com.woowla.ghd.AppLogger
import com.woowla.ghd.domain.entities.SyncSettings
import com.woowla.ghd.domain.services.PullRequestService
import com.woowla.ghd.domain.services.ReleaseService
import com.woowla.ghd.domain.services.RepoToCheckService
import com.woowla.ghd.domain.services.SyncSettingsService
import com.woowla.ghd.eventbus.Event
import com.woowla.ghd.eventbus.EventBus
import com.woowla.ghd.extensions.timer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.system.measureTimeMillis
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

class Synchronizer private constructor(
    private val repoToCheckService: RepoToCheckService = RepoToCheckService(),
    private val syncSettingsService: SyncSettingsService = SyncSettingsService(),
    private val synchronizableServiceList: List<SynchronizableService> = listOf(PullRequestService(), ReleaseService())
) {
    companion object {
        val INSTANCE = Synchronizer()
    }

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    private val isInitialized = AtomicBoolean(false)
    private var checkTimeout = SyncSettings.defaultCheckTimeout
    private var timerJob: Job? = null

    private var syncJob: Job? = null

    fun initialize() {
        isInitialized.set(true)
        reloadCheckTimeout(forceReload = true, startWithDelay = false)
        subscribe()
    }

    fun sync() {
        if (!isInitialized.get()) {
            return
        }

        // don't sync if it's still running
        if (syncJob?.isCompleted ?: true) {
            AppLogger.d("Synchronizer :: sync")
            syncJob = scope.launch {
                unsubscribe()
                executeAllSynchronizables()
                subscribe()
            }
        } else {
            AppLogger.d("Synchronizer :: don't sync, still in progress")
        }
    }

    private suspend fun executeAllSynchronizables() {
        val syncSettings = syncSettingsService.get().getOrNull()
        AppLogger.d("SynchronizationUseCase :: are sync settings null? ${syncSettings == null}")
        if (syncSettings == null) {
            return
        }

        val githubPatToken = syncSettings.githubPatToken
        AppLogger.d("SynchronizationUseCase :: is github token null or blank? ${githubPatToken.isNullOrBlank()}")
        if (githubPatToken.isNullOrBlank()) {
            return
        }

        val allReposToCheck = repoToCheckService.getAll().getOrDefault(listOf())


        val measuredTime = measureTimeMillis {
            coroutineScope {
                synchronizableServiceList
                    .map {
                        async { it.synchronize(syncSettings, allReposToCheck) }
                    }
                    .awaitAll()
            }
        }

        val synchronizedAt = Clock.System.now()
        syncSettingsService.save(syncSettings.copy(synchronizedAt = synchronizedAt))

        AppLogger.d("SynchronizationUseCase :: sync at $synchronizedAt and it took $measuredTime millis to download the pull requests and repositories")

        // add some small delay because sometimes some kind of flickering is shown (it shows large amount of PRs and later on they disappear)
        delay(150)
        EventBus.publish(Event.SYNCHRONIZED)
    }

    private fun reloadCheckTimeout(forceReload: Boolean = false, startWithDelay: Boolean = true) {
        scope.launch {
            val settingsCheckTimeout = syncSettingsService.get().getOrNull()?.checkTimeout
            if (forceReload || (settingsCheckTimeout != null && settingsCheckTimeout != checkTimeout)) {
                checkTimeout = SyncSettings.getValidCheckTimeout(settingsCheckTimeout)
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
        EventBus.subscribe(this, scope, Event.SETTINGS_UPDATED) {
            reloadCheckTimeout()
        }
    }

    private fun unsubscribe() {
        EventBus.unsubscribe(this)
    }
}