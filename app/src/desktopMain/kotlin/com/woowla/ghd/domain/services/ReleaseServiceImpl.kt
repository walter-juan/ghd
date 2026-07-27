package com.woowla.ghd.domain.services

import com.woowla.ghd.core.AppLogger
import com.woowla.ghd.domain.data.LocalDataSource
import com.woowla.ghd.domain.data.RemoteDataSource
import com.woowla.ghd.domain.entities.AppSettings
import com.woowla.ghd.domain.entities.ReleaseWithRepo
import com.woowla.ghd.domain.entities.RepoToCheck
import com.woowla.ghd.domain.entities.SyncResultEntry
import com.woowla.ghd.domain.entities.SyncSettings
import com.woowla.ghd.domain.entities.filterNotSyncValid
import com.woowla.ghd.domain.mappers.toSyncResultEntry
import com.woowla.ghd.domain.notifications.NotificationsSender
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlin.time.Clock

class ReleaseServiceImpl(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource,
    private val notificationsSender: NotificationsSender,
    private val appSettingsService: AppSettingsService,
    private val appLogger: AppLogger,
) : ReleaseService {
    override suspend fun getAll(): Result<List<ReleaseWithRepo>> {
        return localDataSource.getAllReleases()
            .mapCatching { releases ->
                releases.sorted()
            }
    }

    override suspend fun synchronize(syncResultId: Long, syncSettings: SyncSettings, repoToCheckList: List<RepoToCheck>): List<SyncResultEntry> {
        appLogger.d("Synchronizer :: sync :: releases :: start")
        val releasesSyncStartAt = Clock.System.now()
        val releasesBefore = getAll().getOrDefault(listOf())
        val enabledRepoToCheckList = repoToCheckList.filter { it.areReleasesEnabled }

        val syncApiResults = coroutineScope {
            val results = enabledRepoToCheckList
                .map { dbRepoToCheck ->
                    async { fetchLastReleases(syncResultId, dbRepoToCheck) }
                }
                .awaitAll()

            cleanUp()

            results
        }
        appLogger.d("Synchronizer :: sync :: releases :: fetch remote took ${(Clock.System.now() - releasesSyncStartAt).inWholeMilliseconds} ms")

        val releasesAfter = getAll().getOrDefault(listOf())
        appSettingsService.get().onSuccess { appSettings ->
            sendNotifications(appSettings = appSettings, oldReleases = releasesBefore, newReleases = releasesAfter)
        }

        appLogger.d("Synchronizer :: sync :: releases :: finish took ${(Clock.System.now() - releasesSyncStartAt).inWholeMilliseconds} ms")
        return syncApiResults
    }

    override suspend fun cleanUp() {
        getAll()
            .mapCatching { releases ->
                releases.filterNotSyncValid()
            }
            .mapCatching { releases ->
                releases.map { it.release.id }
            }
            .onSuccess { releasesIds ->
                localDataSource.removeReleases(releasesIds)
            }
    }

    override suspend fun sendNotifications(appSettings: AppSettings, oldReleases: List<ReleaseWithRepo>, newReleases: List<ReleaseWithRepo>): Result<Unit> {
        newReleases.forEach { newRelease ->
            val outcome = when {
                !appSettings.notificationsSettings.newReleaseEnabled -> "suppressed:global-disabled"
                !newRelease.repoToCheck.areReleasesNotificationsEnabled -> "suppressed:repository-disabled"
                oldReleases.any { it.release.id == newRelease.release.id } -> "suppressed:already-known"
                else -> "dispatched"
            }
            val repository = newRelease.repoToCheck.repository
            appLogger.d(
                "Notification :: decision :: event=release :: repository=${repository?.owner}/${repository?.name} " +
                    ":: global=${appSettings.notificationsSettings.newReleaseEnabled} " +
                    ":: repositoryEnabled=${newRelease.repoToCheck.areReleasesNotificationsEnabled} :: outcome=$outcome"
            )
            if (outcome == "dispatched") {
                notificationsSender.newRelease(newRelease)
            }
        }

        return Result.success(Unit)
    }

    override suspend fun fetchLastReleases(syncResultId: Long, repoToCheck: RepoToCheck): SyncResultEntry {
        val startAt = Clock.System.now()
        return remoteDataSource
            .getLastRelease(repoToCheck)
            .onSuccess {
                // remove the old one
                localDataSource.removeReleaseByRepoToCheck(repoToCheckId = repoToCheck.id)
            }
            .onSuccess { apiResult ->
                // insert the new one
                val releaseWithRepo = apiResult.data
                localDataSource.upsertRelease(releaseWithRepo.release)
            }
            .toSyncResultEntry(
                syncResultId = syncResultId,
                repoToCheckId = repoToCheck.id,
                origin = SyncResultEntry.Origin.RELEASE,
                startAt = startAt
            )
    }
}