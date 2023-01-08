package com.woowla.ghd.domain.services

import com.woowla.ghd.data.local.LocalDataSource
import com.woowla.ghd.data.remote.RemoteDataSource
import com.woowla.ghd.domain.entities.Release
import com.woowla.ghd.domain.entities.RepoToCheck
import com.woowla.ghd.domain.entities.SyncSettings
import com.woowla.ghd.domain.mappers.ApiMappers
import com.woowla.ghd.domain.mappers.DbMappers
import com.woowla.ghd.domain.synchronization.SynchronizableService
import com.woowla.ghd.notifications.NotificationsSender

class ReleaseService(
    private val localDataSource: LocalDataSource = LocalDataSource(),
    private val remoteDataSource: RemoteDataSource = RemoteDataSource(),
    private val notificationsSender: NotificationsSender = NotificationsSender(),
) : SynchronizableService {
    suspend fun getAll(): Result<List<Release>> {
        return localDataSource.getAllReleases()
            .mapCatching { dbReleases ->
                DbMappers.INSTANCE.dbReleaseToRelease(dbReleases)
            }.mapCatching { releases ->
                releases.sorted()
            }
    }

    override suspend fun synchronize(syncSettings: SyncSettings, repoToCheckList: List<RepoToCheck>) {
        val releasesBefore = getAll().getOrDefault(listOf())

        repoToCheckList.forEach { dbRepoToCheck ->
            fetchLastReleases(dbRepoToCheck)
        }

        val releasesAfter = getAll().getOrDefault(listOf())
        sendNotifications(oldReleases = releasesBefore, newReleases = releasesAfter)
    }

    suspend fun sendNotifications(oldReleases: List<Release>, newReleases: List<Release>): Result<Unit> {
        val oldReleasesIds = oldReleases.map { it.id }

        // notification for a new release
        newReleases
            .filter {
                it.repoToCheck.releaseNotificationsEnabled
            }
            .filterNot {
                oldReleasesIds.contains(it.id)
            }
            .forEach { newRelease ->
                notificationsSender.newRelease(newRelease)
            }

        // notification for an update
        newReleases
            .filter {
                it.repoToCheck.releaseNotificationsEnabled
            }
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

        return Result.success(Unit)
    }

    private suspend fun fetchLastReleases(repoToCheck: RepoToCheck) {
        val apiMappers = ApiMappers.INSTANCE

        remoteDataSource
            .getLastRelease(owner = repoToCheck.owner, repo = repoToCheck.name)
            .onSuccess {
                // remove the old one
                localDataSource.removeReleaseByRepoToCheck(repoToCheckId = repoToCheck.id)
            }
            .onSuccess { apiRelease ->
                // insert the new one
                val releaseUpsertRequest = apiMappers.lastReleaseToUpsertRequest(apiRelease, repoToCheck.id)
                localDataSource.upsertRelease(releaseUpsertRequest)
            }
    }
}