package com.woowla.ghd.domain.services

import com.woowla.ghd.data.local.LocalDataSource
import com.woowla.ghd.data.remote.RemoteDataSource
import com.woowla.ghd.domain.entities.AppSettings
import com.woowla.ghd.domain.entities.Release
import com.woowla.ghd.domain.entities.RepoToCheck
import com.woowla.ghd.domain.entities.SyncSettings
import com.woowla.ghd.domain.mappers.toRelease
import com.woowla.ghd.domain.mappers.toUpsertReleaseRequest
import com.woowla.ghd.domain.synchronization.SynchronizableService
import com.woowla.ghd.notifications.NotificationsSender
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class ReleaseService(
    private val localDataSource: LocalDataSource = LocalDataSource(),
    private val remoteDataSource: RemoteDataSource = RemoteDataSource(),
    private val notificationsSender: NotificationsSender = NotificationsSender(),
    private val appSettingsService: AppSettingsService = AppSettingsService(),
) : SynchronizableService {
    suspend fun getAll(): Result<List<Release>> {
        return localDataSource.getAllReleases()
            .mapCatching { dbReleases ->
                dbReleases.map { it.toRelease() }
            }.mapCatching { releases ->
                releases.sorted()
            }
    }

    override suspend fun synchronize(syncSettings: SyncSettings, repoToCheckList: List<RepoToCheck>) {
        val releasesBefore = getAll().getOrDefault(listOf())

        coroutineScope {
            repoToCheckList
                .map { dbRepoToCheck ->
                    async { fetchLastReleases(dbRepoToCheck) }
                }
                .awaitAll()
        }

        val releasesAfter = getAll().getOrDefault(listOf())
        appSettingsService.get().onSuccess {  appSettings ->
            sendNotifications(appSettings = appSettings, oldReleases = releasesBefore, newReleases = releasesAfter)
        }
    }

    suspend fun sendNotifications(appSettings: AppSettings, oldReleases: List<Release>, newReleases: List<Release>): Result<Unit> {
        val oldReleasesIds = oldReleases.map { it.id }

        // notification for a new release
        if (appSettings.newReleaseNotificationsEnabled) {
            newReleases
                .filterNot {
                    oldReleasesIds.contains(it.id)
                }
                .forEach { newRelease ->
                    notificationsSender.newRelease(newRelease)
                }
        }

        // notification for an update
        if (appSettings.updatedReleaseNotificationsEnabled) {
            newReleases
                .filter { newRelease ->
                    val oldRelease = oldReleases.firstOrNull { it.id == newRelease.id }

                    if (oldRelease != null) {
                        oldRelease.publishedAt != newRelease.publishedAt
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

    private suspend fun fetchLastReleases(repoToCheck: RepoToCheck) {
        remoteDataSource
            .getLastRelease(owner = repoToCheck.owner, repo = repoToCheck.name)
            .onSuccess {
                // remove the old one
                localDataSource.removeReleaseByRepoToCheck(repoToCheckId = repoToCheck.id)
            }
            .onSuccess { apiRelease ->
                // insert the new one
                val releaseUpsertRequest = apiRelease.toUpsertReleaseRequest(repoToCheck.id)
                localDataSource.upsertRelease(releaseUpsertRequest)
            }
    }
}