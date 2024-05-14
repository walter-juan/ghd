package com.woowla.ghd.data.local

import com.woowla.ghd.data.local.prop.AppProperties
import com.woowla.ghd.data.local.mappers.toAppSettings
import com.woowla.ghd.data.local.mappers.toPullRequest
import com.woowla.ghd.data.local.mappers.toRelease
import com.woowla.ghd.data.local.room.AppDatabase
import com.woowla.ghd.data.local.room.entities.DbAuthor
import com.woowla.ghd.data.local.room.entities.DbPullRequest
import com.woowla.ghd.data.local.room.entities.DbRelease
import com.woowla.ghd.data.local.room.entities.DbReview
import com.woowla.ghd.domain.entities.*
import kotlinx.datetime.Instant

class LocalDataSource(
    private val appProperties: AppProperties = AppProperties,
    private val appDatabase: AppDatabase = AppDatabase.getInstance()
) {
    suspend fun getAppSettings(): Result<AppSettings> {
        return runCatching {
            appProperties.load()
            appProperties.toAppSettings()
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


    suspend fun getPullRequest(id: String): Result<PullRequest> {
        // TODO relations
        return runCatching {
            val dbRepoToCheckList = appDatabase.repoToCheckDao().getAll()
            val dbReviewList = appDatabase.reviewDao().getByPullRequest(pullRequestId = id)
            appDatabase.pullRequestDao().get(id).toPullRequest(dbRepoToCheckList, dbReviewList)
        }
    }
    suspend fun getAllPullRequests(): Result<List<PullRequest>> {
        // TODO relations
        return runCatching {
            val dbRepoToCheckList = appDatabase.repoToCheckDao().getAll()

            appDatabase.pullRequestDao()
                .getAll()
                .map { dbPullRequest ->
                    val dbReviewList = appDatabase.reviewDao().getByPullRequest(pullRequestId = dbPullRequest.id)
                    dbPullRequest.toPullRequest(dbRepoToCheckList, dbReviewList)
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
            appDatabase.pullRequestDao().insert(
                pullRequests.map { pullRequest ->
                    DbPullRequest(
                        id = pullRequest.id,
                        number = pullRequest.number,
                        url = pullRequest.url,
                        title = pullRequest.title,
                        state = pullRequest.state.toString(),
                        createdAt = pullRequest.createdAt,
                        updatedAt = pullRequest.updatedAt,
                        mergedAt = pullRequest.mergedAt,
                        isDraft = pullRequest.isDraft,
                        baseRef = pullRequest.baseRef,
                        headRef = pullRequest.headRef,
                        author = DbAuthor(
                            login = pullRequest.authorLogin,
                            url = pullRequest.authorUrl,
                            avatarUrl = pullRequest.authorAvatarUrl,
                        ),
                        appSeenAt = pullRequest.appSeenAt,
                        totalCommentsCount = pullRequest.totalCommentsCount,
                        mergeable = pullRequest.mergeable.toString(),
                        lastCommitCheckRollupStatus = pullRequest.lastCommitCheckRollupStatus.toString(),
                        repoToCheckId = pullRequest.repoToCheck.id,
                    )
                }
            )
        }
    }
    suspend fun removePullRequests(ids: List<String>): Result<Unit> {
        return runCatching {
            appDatabase.pullRequestDao().delete(ids)
        }
    }


    suspend fun getAllReleases(): Result<List<Release>> {
        // TODO relations
        return runCatching {
            val dbRepoToCheckList = appDatabase.repoToCheckDao().getAll()
            appDatabase.releaseDao().getAll().map {
                it.toRelease(dbRepoToCheckList)
            }
        }
    }
    suspend fun upsertRelease(release: Release): Result<Unit> {
        return runCatching {
            appDatabase.releaseDao().insert(
                DbRelease(
                    id = release.id,
                    name = release.name,
                    tagName = release.tagName,
                    url = release.url,
                    publishedAt = release.publishedAt,
                    author = DbAuthor(
                        login = release.authorLogin,
                        url = release.authorUrl,
                        avatarUrl = release.authorAvatarUrl,
                    ),
                    repoToCheckId = release.repoToCheck.id,
                )
            )
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

        val reviewDao = appDatabase.reviewDao()
        return runCatching {
            reviewDao.insert(
                reviews.map { review ->
                    DbReview(
                        id = review.id,
                        submittedAt = review.submittedAt,
                        url = review.url,
                        state = review.state.toString(),
                        author = DbAuthor(
                            login = review.authorLogin,
                            url = review.authorUrl,
                            avatarUrl = review.authorAvatarUrl,
                        ),
                        pullRequestId = review.pullRequestId,
                    )
                }
            )
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
