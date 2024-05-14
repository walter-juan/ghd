package com.woowla.ghd.domain.synchronization

import com.woowla.ghd.AppLogger
import com.woowla.ghd.data.local.LocalDataSource
import com.woowla.ghd.domain.entities.*
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
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

class Synchronizer private constructor(
    private val repoToCheckService: RepoToCheckService = RepoToCheckService(),
    private val syncSettingsService: SyncSettingsService = SyncSettingsService(),
    private val synchronizableServiceList: List<SynchronizableService> = listOf(PullRequestService(), ReleaseService()),
    private val localDataSource: LocalDataSource = LocalDataSource(),
) {
    companion object {
        val INSTANCE = Synchronizer()
        val MAX_SYNC_RESULTS = 1_000
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

    suspend fun getAllSyncResults(): Result<List<SyncResultWithEntitiesAndRepos>> {
        return localDataSource.getAllSyncResults()
            .mapCatching { syncResults ->
                syncResults.sortedByDescending { it.syncResult.startAt }
            }
    }

    suspend fun getLastSyncResult(): Result<SyncResultWithEntitiesAndRepos?> {
        return localDataSource.getLastSyncResult()
    }

    suspend fun getSyncResult(id: Long): Result<SyncResultWithEntitiesAndRepos> {
        return localDataSource.getSyncResult(id)
    }

    suspend fun getSyncResultEntries(syncResultId: Long): Result<List<SyncResultEntryWithRepo>> {
        return localDataSource.getSyncResultEntries(syncResultId)
            .mapCatching { syncResultEntries ->
                syncResultEntries.sorted()
            }
    }

    suspend fun cleanUpSyncResult() {
        getAllSyncResults()
            .mapCatching { syncResults ->
                syncResults.sortedByDescending { it.syncResult.startAt }
            }
            .mapCatching { syncResults ->
                syncResults.drop(MAX_SYNC_RESULTS).map { it.syncResult.id }
            }
            .mapCatching {
                localDataSource.removeSyncResults(it)
            }
    }

    fun sync() {
        if (!isInitialized.get()) {
            return
        }

        // don't sync if it's still running
        if (syncJob?.isCompleted ?: true) {
            AppLogger.d("Synchronizer :: sync :: start")
            syncJob = scope.launch {
                unsubscribe()
                executeAllSynchronizables()
                subscribe()
            }
        } else {
            AppLogger.d("Synchronizer :: sync :: don't sync, still in progress")
        }
    }

    private suspend fun syncResultStart(): SyncResult {
        return localDataSource.upsertSyncResult(
            SyncResult(
                startAt = Clock.System.now(),
                endAt = null
            )
        ).getOrThrow()
    }

    private suspend fun executeAllSynchronizables() {
        var syncResultFinish = syncResultStart()

        val syncSettings = syncSettingsService.get().getOrNull()
        if (syncSettings == null) {
            syncResultFinishWithError(syncResult = syncResultFinish, error = "Unknown error", message = "Synchronization settings are null")
            AppLogger.d("Synchronizer :: sync :: finished because the synchronization settings are null")
            EventBus.publish(Event.SYNCHRONIZED)
            return
        }

        val githubPatToken = syncSettings.githubPatToken
        if (githubPatToken.isNullOrBlank()) {
            syncResultFinishWithError(syncResult = syncResultFinish, error = "Invalid data", message = "GitHub token is not set")
            AppLogger.d("Synchronizer :: sync :: finished because the github token is null or blank")
            EventBus.publish(Event.SYNCHRONIZED)
            return
        }

        val allReposToCheck = repoToCheckService.getAll().getOrDefault(listOf())

        val upsertSyncResultEntries = coroutineScope {
            synchronizableServiceList
                .map {
                    async { it.synchronize(syncResultFinish.id, syncSettings, allReposToCheck) }
                }
                .awaitAll()
                .flatten()
        }

        syncResultFinish = syncResultFinish(syncResultFinish, upsertSyncResultEntries)
        cleanUpSyncResult()

        val syncResult = getSyncResult(syncResultFinish.id).getOrThrow()
        AppLogger.d("Synchronizer :: sync :: finished, from ${syncResult.syncResult.startAt} to ${syncResult.syncResult.endAt} for ${allReposToCheck.count()} repos to check it took ${syncResult.syncResult.duration?.inWholeMilliseconds} millis to download the pull requests and repositories with ${syncResult.errorPercentage}% of errors meaning a ${syncResult.status} status")

        // add some small delay because sometimes some kind of flickering is shown (it shows large amount of PRs and later on they disappear)
        delay(150)
        EventBus.publish(Event.SYNCHRONIZED)
    }

    private suspend fun syncResultFinish(syncResult: SyncResult, syncResultEntryList: List<SyncResultEntry>): SyncResult {
        val syncResultUpdated = localDataSource.upsertSyncResult(syncResult.copy(endAt = Clock.System.now())).getOrThrow()
        localDataSource.upsertSyncResultEntries(syncResultEntryList)
        return syncResultUpdated
    }

    private suspend fun syncResultFinishWithError(syncResult: SyncResult, error: String, message: String): SyncResult {
        val syncResultEntry = SyncResultEntry(
            syncResultId = syncResult.id,
            repoToCheckId = null,
            isSuccess = false,
            startAt = Clock.System.now(),
            endAt = Clock.System.now(),
            origin = SyncResultEntry.Origin.OTHER,
            error = error,
            errorMessage = message
        )
        return syncResultFinish(syncResult, listOf(syncResultEntry))
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