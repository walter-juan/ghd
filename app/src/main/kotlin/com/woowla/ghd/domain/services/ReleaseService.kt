package com.woowla.ghd.domain.services

import com.woowla.ghd.data.local.LocalDataSource
import com.woowla.ghd.data.remote.RemoteDataSource
import com.woowla.ghd.domain.entities.AppSettings
import com.woowla.ghd.domain.entities.RepoToCheck
import com.woowla.ghd.domain.entities.SyncResultEntry
import com.woowla.ghd.domain.entities.SyncSettings
import com.woowla.ghd.data.remote.mappers.toRelease
import com.woowla.ghd.domain.entities.ReleaseWithRepo
import com.woowla.ghd.domain.entities.filterNotSyncValid
import com.woowla.ghd.domain.mappers.toSyncResultEntry
import com.woowla.ghd.domain.synchronization.SynchronizableService
import com.woowla.ghd.notifications.NotificationsSender
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.datetime.Clock

class ReleaseService(
    private val localDataSource: LocalDataSource = LocalDataSource(),
    private val remoteDataSource: RemoteDataSource = RemoteDataSource(),
    private val notificationsSender: NotificationsSender = NotificationsSender(),
    private val appSettingsService: AppSettingsService = AppSettingsService(),
) : SynchronizableService {
    suspend fun getAll(): Result<List<ReleaseWithRepo>> {
        return localDataSource.getAllReleases()
            .mapCatching { releases ->
                releases.sorted()
            }
    }

    override suspend fun synchronize(syncResultId: Long, syncSettings: SyncSettings, repoToCheckList: List<RepoToCheck>): List<SyncResultEntry> {
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

        val releasesAfter = getAll().getOrDefault(listOf())
        appSettingsService.get().onSuccess {  appSettings ->
            sendNotifications(appSettings = appSettings, oldReleases = releasesBefore, newReleases = releasesAfter)
        }

        return syncApiResults
    }

    suspend fun cleanUp() {
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

    suspend fun sendNotifications(appSettings: AppSettings, oldReleases: List<ReleaseWithRepo>, newReleases: List<ReleaseWithRepo>): Result<Unit> {
        val oldReleasesIds = oldReleases.map { it.release.id }

        // notification for a new release
        if (appSettings.newReleaseNotificationsEnabled) {
            newReleases
                .filterNot {
                    oldReleasesIds.contains(it.release.id)
                }
                .forEach { newRelease ->
                    notificationsSender.newRelease(newRelease)
                }
        }

        // notification for an update
        if (appSettings.updatedReleaseNotificationsEnabled) {
            newReleases
                .filter { newRelease ->
                    val oldRelease = oldReleases.firstOrNull { it.release.id == newRelease.release.id }

                    if (oldRelease != null) {
                        oldRelease.release.publishedAt != newRelease.release.publishedAt
                    } else {
                        false
                    }
                }
                .forEach { newRelease ->
                    notificationsSender.updateRelease(newRelease)
                }
        }

        return Result.success(Unit)
    }

    private suspend fun fetchLastReleases(syncResultId: Long, repoToCheck: RepoToCheck): SyncResultEntry {
        val startAt = Clock.System.now()
        return remoteDataSource
            .getLastRelease(owner = repoToCheck.owner, repo = repoToCheck.name)
            .onSuccess {
                // remove the old one
                localDataSource.removeReleaseByRepoToCheck(repoToCheckId = repoToCheck.id)
            }
            .onSuccess { apiRelease ->
                // insert the new one
                val release = apiRelease.toRelease(repoToCheck)
                localDataSource.upsertRelease(release)
            }
            .toSyncResultEntry(
                syncResultId = syncResultId,
                repoToCheckId = repoToCheck.id,
                origin = SyncResultEntry.Origin.RELEASE,
                startAt = startAt
            )
    }
}