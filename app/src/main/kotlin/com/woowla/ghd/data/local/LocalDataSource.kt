package com.woowla.ghd.data.local

import com.woowla.ghd.data.local.prop.AppProperties
import com.woowla.ghd.data.local.room.AppDatabase
import com.woowla.ghd.domain.entities.*
import kotlinx.datetime.Instant

class LocalDataSource(
    private val appProperties: AppProperties = AppProperties,
    private val appDatabase: AppDatabase = AppDatabase.getInstance()
) {
    suspend fun getAppSettings(): Result<AppSettings> {
        return runCatching {
            appProperties.load()
            AppSettings(
                darkTheme = appProperties.darkTheme,
                newPullRequestsNotificationsEnabled = appProperties.newPullRequestsNotificationsEnabled,
                updatedPullRequestsNotificationsEnabled = appProperties.updatedPullRequestsNotificationsEnabled,
                newReleaseNotificationsEnabled = appProperties.newReleaseNotificationsEnabled,
                updatedReleaseNotificationsEnabled = appProperties.updatedReleaseNotificationsEnabled
            )
        }
    }
    suspend fun updateAppSettings(appSettings: AppSettings): Result<Unit> {
        return runCatching {
            appProperties.load()
            appProperties.darkTheme = appSettings.darkTheme
            appProperties.newPullRequestsNotificationsEnabled = appSettings.newPullRequestsNotificationsEnabled
            appProperties.updatedPullRequestsNotificationsEnabled = appSettings.updatedPullRequestsNotificationsEnabled
            appProperties.newReleaseNotificationsEnabled = appSettings.newReleaseNotificationsEnabled
            appProperties.updatedReleaseNotificationsEnabled = appSettings.updatedReleaseNotificationsEnabled
            appProperties.store()
        }
    }


    suspend fun getSyncSettings(): Result<SyncSettings> {
        return runCatching {
            getOrCreateSyncSettings()
        }
    }
    suspend fun updateSyncSettings(syncSettings: SyncSettings): Result<Unit> {
        return runCatching {
            appDatabase.syncSettingsDao().insert(syncSettings)
        }
    }


    suspend fun getLastSyncResult(): Result<SyncResultWithEntitiesAndRepos?> {
        return runCatching {
            val repoToCheckList = appDatabase.repoToCheckDao().getAll()
            val syncResult = appDatabase.syncResultDao().getLast()
            val syncResultEntryList = appDatabase.syncResultEntryDao().getBySyncResult(syncResultId = syncResult.id)
            val syncResultEntryWithRepoList = syncResultEntryList.map { syncResultEntry ->
                SyncResultEntryWithRepo(
                    syncResultEntry = syncResultEntry,
                    repoToCheck = repoToCheckList.firstOrNull { it.id == syncResultEntry.repoToCheckId }
                )
            }
            SyncResultWithEntitiesAndRepos(
                syncResult = syncResult,
                syncResultEntries = syncResultEntryWithRepoList,
            )
        }
    }
    suspend fun getAllSyncResults(): Result<List<SyncResultWithEntitiesAndRepos>> {
        return runCatching {
            val repoToCheckList = appDatabase.repoToCheckDao().getAll()
            appDatabase.syncResultDao()
                .getAll()
                .map { syncResult ->
                    val syncResultEntryList = appDatabase.syncResultEntryDao().getBySyncResult(syncResultId = syncResult.id)
                    val syncResultEntryWithRepoList = syncResultEntryList.map { syncResultEntry ->
                        SyncResultEntryWithRepo(
                            syncResultEntry = syncResultEntry,
                            repoToCheck = repoToCheckList.firstOrNull { it.id == syncResultEntry.repoToCheckId }
                        )
                    }
                    SyncResultWithEntitiesAndRepos(
                        syncResult = syncResult,
                        syncResultEntries = syncResultEntryWithRepoList,
                    )
                }
        }
    }
    suspend fun getSyncResult(id: Long): Result<SyncResultWithEntitiesAndRepos> {
        return runCatching {
            val repoToCheckList = appDatabase.repoToCheckDao().getAll()
            val syncResult = appDatabase.syncResultDao().get(id)
            val syncResultEntryList = appDatabase.syncResultEntryDao().getBySyncResult(syncResultId = id)
            val syncResultEntryWithRepoList = syncResultEntryList.map { syncResultEntry ->
                SyncResultEntryWithRepo(
                    syncResultEntry = syncResultEntry,
                    repoToCheck = repoToCheckList.firstOrNull { it.id == syncResultEntry.repoToCheckId }
                )
            }
            SyncResultWithEntitiesAndRepos(
                syncResult = syncResult,
                syncResultEntries = syncResultEntryWithRepoList,
            )
        }
    }
    suspend fun removeSyncResults(ids: List<Long>): Result<Unit> {
        return runCatching {
            appDatabase.syncResultDao().delete(ids)
        }
    }
    suspend fun upsertSyncResult(syncResult: SyncResult): Result<SyncResult> {
        return runCatching {
            val syncResultDao = appDatabase.syncResultDao()
            val rowId = syncResultDao.insert(syncResult)
            syncResultDao.get(rowId)
        }
    }


    /**
     * Get the sync result entries by the sync result id with the repo to check
     */
    suspend fun getSyncResultEntries(syncResultId: Long): Result<List<SyncResultEntryWithRepo>> {
        return runCatching {
            val repoToCheckList = appDatabase.repoToCheckDao().getAll()
            val syncResultEntryList = appDatabase.syncResultEntryDao().getBySyncResult(syncResultId = syncResultId)

            syncResultEntryList.map { syncResultEntry ->
                SyncResultEntryWithRepo(
                    syncResultEntry = syncResultEntry,
                    repoToCheck = repoToCheckList.firstOrNull { it.id == syncResultEntry.repoToCheckId }
                )
            }
        }
    }
    suspend fun upsertSyncResultEntries(syncResultEntryList: List<SyncResultEntry>): Result<Unit> {
        return runCatching {
            appDatabase.syncResultEntryDao().insert(syncResultEntryList)
        }
    }


    suspend fun getRepoToCheck(id: Long): Result<RepoToCheck> {
        return runCatching {
            appDatabase.repoToCheckDao().get(id)
        }
    }
    suspend fun getAllReposToCheck(): Result<List<RepoToCheck>> {
        return runCatching {
            appDatabase.repoToCheckDao().getAll()
        }
    }
    suspend fun upsertRepoToCheck(repoToCheck: RepoToCheck): Result<Unit> {
        return runCatching {
            appDatabase.repoToCheckDao().insert(repoToCheck)
        }
    }
    suspend fun removeRepoToCheck(id: Long): Result<Unit> {
        return runCatching {
            appDatabase.repoToCheckDao().delete(id)
        }
    }


    suspend fun getPullRequest(id: String): Result<PullRequestWithRepoAndReviews> {
        return runCatching {
            val repoToCheckList = appDatabase.repoToCheckDao().getAll()
            val reviewList = appDatabase.reviewDao().getByPullRequest(pullRequestId = id)
            val pullRequest = appDatabase.pullRequestDao().get(id)
            PullRequestWithRepoAndReviews(
                pullRequest = pullRequest,
                repoToCheck = repoToCheckList.first { it.id == pullRequest.repoToCheckId },
                reviews = reviewList,
            )
        }
    }
    suspend fun getAllPullRequests(): Result<List<PullRequestWithRepoAndReviews>> {
        return runCatching {
            val repoToCheckList = appDatabase.repoToCheckDao().getAll()
            appDatabase.pullRequestDao()
                .getAll()
                .map { pullRequest ->
                    val reviewList = appDatabase.reviewDao().getByPullRequest(pullRequestId = pullRequest.id)
                    PullRequestWithRepoAndReviews(
                        pullRequest = pullRequest,
                        repoToCheck = repoToCheckList.first { it.id == pullRequest.repoToCheckId },
                        reviews = reviewList,
                    )
                }
        }
    }
    suspend fun updateAppSeenAt(id: String, appSeenAt: Instant?): Result<Unit> {
        return runCatching {
            appDatabase.pullRequestDao().updateSeenAt(id = id, appSeenAt = appSeenAt)
        }
    }
    suspend fun upsertPullRequests(pullRequests: List<PullRequest>): Result<Unit> {
        return runCatching {
            appDatabase.pullRequestDao().insert(pullRequests)
        }
    }
    suspend fun removePullRequests(ids: List<String>): Result<Unit> {
        return runCatching {
            appDatabase.pullRequestDao().delete(ids)
        }
    }


    suspend fun getAllReleases(): Result<List<ReleaseWithRepo>> {
        return runCatching {
            val repoToCheckList = appDatabase.repoToCheckDao().getAll()
            appDatabase.releaseDao().getAll().map { release ->
                ReleaseWithRepo(
                    release = release,
                    repoToCheck = repoToCheckList.first { it.id == release.repoToCheckId }
                )
            }
        }
    }
    suspend fun upsertRelease(release: Release): Result<Unit> {
        return runCatching {
            appDatabase.releaseDao().insert(release)
        }
    }
    suspend fun removeReleases(ids: List<String>): Result<Unit> {
        return runCatching {
            appDatabase.releaseDao().delete(ids)
        }
    }
    suspend fun removeReleaseByRepoToCheck(repoToCheckId: Long): Result<Unit> {
        return runCatching {
            appDatabase.releaseDao().deleteByRepoToCheck(repoToCheckId = repoToCheckId)
        }
    }


    suspend fun removeReviewsByPullRequest(pullRequestIds: List<String>): Result<Unit> {
        return runCatching {
            appDatabase.reviewDao().deleteByPullRequest(pullRequestIds)
        }
    }
    suspend fun upsertReviews(reviews: List<Review>): Result<Unit> {
        if (reviews.isEmpty()) return Result.success(Unit)

        return runCatching {
            appDatabase.reviewDao().insert(reviews)
        }
    }


    private suspend fun getOrCreateSyncSettings(): SyncSettings {
        val dbSyncSettings = appDatabase.syncSettingsDao().get()
        val defaultDbSyncSettings = SyncSettings(
            githubPatToken = "",
            checkTimeout = null,
            pullRequestCleanUpTimeout = null,
        )
        return if (dbSyncSettings == null) {
            appDatabase.syncSettingsDao().insert(defaultDbSyncSettings)
            defaultDbSyncSettings
        } else {
            dbSyncSettings
        }
    }
}
