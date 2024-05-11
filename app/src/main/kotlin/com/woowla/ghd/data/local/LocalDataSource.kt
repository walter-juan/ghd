package com.woowla.ghd.data.local

import com.woowla.ghd.data.local.prop.AppProperties
import com.woowla.ghd.data.local.mappers.toAppSettings
import com.woowla.ghd.data.local.mappers.toPullRequest
import com.woowla.ghd.data.local.mappers.toRelease
import com.woowla.ghd.domain.entities.AppSettings
import com.woowla.ghd.domain.entities.PullRequest
import com.woowla.ghd.domain.entities.Release
import com.woowla.ghd.domain.entities.RepoToCheck
import com.woowla.ghd.domain.entities.Review
import com.woowla.ghd.domain.entities.SyncResult
import com.woowla.ghd.domain.entities.SyncResultEntry
import com.woowla.ghd.domain.entities.SyncSettings
import com.woowla.ghd.data.local.mappers.toRepoToCheck
import com.woowla.ghd.data.local.mappers.toSyncResult
import com.woowla.ghd.data.local.mappers.toSyncResultEntry
import com.woowla.ghd.data.local.mappers.toSyncSettings
import com.woowla.ghd.data.local.room.AppDatabase
import com.woowla.ghd.data.local.room.entities.DbAuthor
import com.woowla.ghd.data.local.room.entities.DbPullRequest
import com.woowla.ghd.data.local.room.entities.DbRelease
import com.woowla.ghd.data.local.room.entities.DbRepoToCheck
import com.woowla.ghd.data.local.room.entities.DbReview
import com.woowla.ghd.data.local.room.entities.DbSyncResult
import com.woowla.ghd.data.local.room.entities.DbSyncResultEntry
import com.woowla.ghd.data.local.room.entities.DbSyncSettings
import com.woowla.ghd.domain.requests.UpsertRepoToCheckRequest
import com.woowla.ghd.domain.requests.UpsertSyncResultEntryRequest
import com.woowla.ghd.domain.requests.UpsertSyncResultRequest
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
            appProperties.encryptedDatabase = appSettings.encryptedDatabase
            appProperties.newPullRequestsNotificationsEnabled = appSettings.newPullRequestsNotificationsEnabled
            appProperties.updatedPullRequestsNotificationsEnabled = appSettings.updatedPullRequestsNotificationsEnabled
            appProperties.newReleaseNotificationsEnabled = appSettings.newReleaseNotificationsEnabled
            appProperties.updatedReleaseNotificationsEnabled = appSettings.updatedReleaseNotificationsEnabled
            appProperties.store()
        }
    }


    suspend fun getSyncSettings(): Result<SyncSettings> {
        return runCatching {
            getOrCreateSyncSettings().toSyncSettings()
        }
    }
    suspend fun updateSyncSettings(syncSettings: SyncSettings): Result<Unit> {
        return runCatching {
            appDatabase.syncSettingsDao().insert(DbSyncSettings(
                githubPatToken = syncSettings.githubPatToken ?: "",
                checkTimeout = syncSettings.checkTimeout,
                pullRequestCleanUpTimeout = syncSettings.pullRequestCleanUpTimeout,
            ))
        }
    }


    suspend fun getLastSyncResult(): Result<SyncResult?> {
        return runCatching {
            // TODO relations
            val dbRepoToCheckList = appDatabase.repoToCheckDao().getAll()
            val dbSyncResult = appDatabase.syncResultDao().getLast()
            val dbSyncResultEntryList = appDatabase.syncResultEntryDao().getBySyncResult(syncResultId = dbSyncResult.id)
            val syncResultEntryList = dbSyncResultEntryList.map { it.toSyncResultEntry(dbRepoToCheckList) }
            dbSyncResult.toSyncResult(syncResultEntryList)
//            appDatabase.syncResultDao().getLastWithEntriesAndRepos().toSyncResult()
        }
    }
    /**
     * Get the all sync results without the entries
     */
    suspend fun getAllSyncResults(): Result<List<SyncResult>> {
        return runCatching {
            // TODO relations
            val dbRepoToCheckList = appDatabase.repoToCheckDao().getAll()

            appDatabase.syncResultDao()
                .getAll()
                .map { dbSyncResult ->
                    val dbSyncResultEntryList = appDatabase.syncResultEntryDao().getBySyncResult(syncResultId = dbSyncResult.id)
                    val syncResultEntryList = dbSyncResultEntryList.map { it.toSyncResultEntry(dbRepoToCheckList) }
                    dbSyncResult.toSyncResult(syncResultEntryList)
                }
//            appDatabase
//                .syncResultDao()
//                .getAllWithEntriesAndRepos()
//                .map { it.toSyncResult() }
        }
    }
    /**
     * Get the sync result by id without the entries
     */
    suspend fun getSyncResult(id: Long): Result<SyncResult> {
        return runCatching {
            // TODO relations
            val dbRepoToCheckList = appDatabase.repoToCheckDao().getAll()
            val dbSyncResultEntryList = appDatabase.syncResultEntryDao().getBySyncResult(syncResultId = id)
            val syncResultEntryList = dbSyncResultEntryList.map { it.toSyncResultEntry(dbRepoToCheckList) }

            appDatabase.syncResultDao()
                .get(id)
                .toSyncResult(syncResultEntryList)
//            appDatabase
//                .syncResultDao()
//                .getWithEntriesAndRepos(id)
//                .toSyncResult()
        }
    }
    suspend fun removeSyncResults(ids: List<Long>): Result<Unit> {
        return runCatching {
            appDatabase.syncResultDao().delete(ids)
        }
    }
    suspend fun upsertSyncResult(upsertRequest: UpsertSyncResultRequest): Result<DbSyncResult> {
        return runCatching {
            val syncResultDao = appDatabase.syncResultDao()
            val dbSyncResult = if (upsertRequest.id == null) {
                DbSyncResult(
                    startAt = upsertRequest.startAt,
                    endAt = upsertRequest.endAt,
                )
            } else {
                DbSyncResult(
                    id = upsertRequest.id,
                    startAt = upsertRequest.startAt,
                    endAt = upsertRequest.endAt,
                )
            }
            val rowId = syncResultDao.insert(dbSyncResult)
            syncResultDao.get(rowId)
        }
    }


    /**
     * Get the sync result entries by the sync result id with the repo to check
     */
    suspend fun getSyncResultEntries(syncResultId: Long): Result<List<SyncResultEntry>> {
        return runCatching {
            val dbRepoToCheckList = appDatabase.repoToCheckDao().getAll()
            appDatabase.syncResultEntryDao()
                .getBySyncResult(syncResultId = syncResultId)
                .map { it.toSyncResultEntry(dbRepoToCheckList) }
            // TODO relations
//            appDatabase
//                .syncResultEntryDao()
//                .getBySyncResultWithRepos(syncResultId)
//                .map { it.toSyncResultEntry() }
        }
    }
    suspend fun upsertSyncResultEntries(upsertRequests: List<UpsertSyncResultEntryRequest>): Result<Unit> {
        return runCatching {
            val syncResultEntryList = upsertRequests.map { upsertRequest ->
                val dbSyncResultid = appDatabase.syncResultDao().get(upsertRequest.syncResultId).id
                val repoToCheckId = upsertRequest.repoToCheckId?.let { appDatabase.repoToCheckDao().get(it) }?.id
                if (upsertRequest.id == null) {
                    DbSyncResultEntry(
                        isSuccess = upsertRequest.isSuccess,
                        startAt = upsertRequest.startAt,
                        endAt = upsertRequest.endAt,
                        origin = upsertRequest.origin,
                        error = upsertRequest.error,
                        errorMessage = upsertRequest.errorMessage,
                        syncResultId = dbSyncResultid,
                        repoToCheckId = repoToCheckId,
                    )
                } else {
                    DbSyncResultEntry(
                        id = upsertRequest.id,
                        isSuccess = upsertRequest.isSuccess,
                        startAt = upsertRequest.startAt,
                        endAt = upsertRequest.endAt,
                        origin = upsertRequest.origin,
                        error = upsertRequest.error,
                        errorMessage = upsertRequest.errorMessage,
                        syncResultId = dbSyncResultid,
                        repoToCheckId = repoToCheckId,
                    )
                }
            }
            appDatabase.syncResultEntryDao().insert(syncResultEntryList)
        }
    }


    suspend fun getRepoToCheck(id: Long): Result<RepoToCheck> {
        return runCatching {
            appDatabase.repoToCheckDao().get(id).toRepoToCheck()
        }
    }
    suspend fun getAllReposToCheck(): Result<List<RepoToCheck>> {
        // TODO remove
//        val repoToCheckDao = appDatabase.repoToCheckDao()
//        if (repoToCheckDao.getAll().isEmpty()) {
//            val a = newDbSuspendedTransaction {
//                DbRepoToCheck.all().toList().map { it.toRepoToCheck() }
//            }
//            repoToCheckDao.insert(a.map {
//                DbRepoToCheck(
//                    id = it.id,
//                    owner = it.owner,
//                    name = it.name,
//                    groupName = it.groupName,
//                    pullBranchRegex = it.pullBranchRegex,
//                    arePullRequestsEnabled = it.arePullRequestsEnabled,
//                    areReleasesEnabled = it.areReleasesEnabled,
//                )
//            })
//        }

        return runCatching {
            appDatabase.repoToCheckDao().getAll().map { it.toRepoToCheck() }
        }
    }
    suspend fun upsertRepoToCheck(upsertRepoToCheckRequest: UpsertRepoToCheckRequest): Result<Unit> {
        return runCatching {
            val dbRepoToCheck = if(upsertRepoToCheckRequest.id == null) {
                DbRepoToCheck(
                    owner = upsertRepoToCheckRequest.owner,
                    name = upsertRepoToCheckRequest.name,
                    groupName = upsertRepoToCheckRequest.groupName,
                    pullBranchRegex = upsertRepoToCheckRequest.pullBranchRegex,
                    arePullRequestsEnabled = upsertRepoToCheckRequest.arePullRequestsEnabled,
                    areReleasesEnabled = upsertRepoToCheckRequest.areReleasesEnabled,
                )
            } else {
                DbRepoToCheck(
                    id = upsertRepoToCheckRequest.id,
                    owner = upsertRepoToCheckRequest.owner,
                    name = upsertRepoToCheckRequest.name,
                    groupName = upsertRepoToCheckRequest.groupName,
                    pullBranchRegex = upsertRepoToCheckRequest.pullBranchRegex,
                    arePullRequestsEnabled = upsertRepoToCheckRequest.arePullRequestsEnabled,
                    areReleasesEnabled = upsertRepoToCheckRequest.areReleasesEnabled,
                )
            }
            appDatabase.repoToCheckDao().insert(dbRepoToCheck)
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
        // TODO update, update seen directly without retrieving first
        return runCatching {
            val pullRequestDao = appDatabase.pullRequestDao()
            val dbPullRequest = pullRequestDao.get(id)
            pullRequestDao.insert(dbPullRequest.copy(appSeenAt = appSeenAt))
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


    private suspend fun getOrCreateSyncSettings(): DbSyncSettings {
        val dbSyncSettings = appDatabase.syncSettingsDao().get()
        val defaultDbSyncSettings = DbSyncSettings(
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
