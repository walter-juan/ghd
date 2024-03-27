package com.woowla.ghd.data.local

import com.woowla.ghd.data.local.db.DbSettings
import com.woowla.ghd.data.local.db.entities.DbPullRequest
import com.woowla.ghd.data.local.db.entities.DbRelease
import com.woowla.ghd.data.local.db.entities.DbRepoToCheck
import com.woowla.ghd.data.local.db.entities.DbReview
import com.woowla.ghd.data.local.db.entities.DbSyncResult
import com.woowla.ghd.data.local.db.entities.DbSyncResultEntry
import com.woowla.ghd.data.local.db.entities.DbSyncSettings
import com.woowla.ghd.data.local.db.newDbSuspendedTransaction
import com.woowla.ghd.data.local.db.tables.DbPullRequestTable
import com.woowla.ghd.data.local.db.tables.DbReleaseTable
import com.woowla.ghd.data.local.db.tables.DbReviewTable
import com.woowla.ghd.data.local.db.tables.DbSyncResultEntryTable
import com.woowla.ghd.data.local.db.tables.DbSyncResultTable
import com.woowla.ghd.data.local.prop.AppProperties
import com.woowla.ghd.data.local.db.utils.findByIdOrThrow
import com.woowla.ghd.data.local.db.utils.upsertById
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
import com.woowla.ghd.data.local.mappers.toSyncResultEntry
import com.woowla.ghd.data.local.mappers.toSyncSettings
import com.woowla.ghd.data.local.mappers.toSyncResult
import com.woowla.ghd.domain.requests.UpsertRepoToCheckRequest
import com.woowla.ghd.domain.requests.UpsertSyncResultEntryRequest
import com.woowla.ghd.domain.requests.UpsertSyncResultRequest
import kotlinx.datetime.Instant
import org.jetbrains.exposed.dao.load
import org.jetbrains.exposed.dao.with

class LocalDataSource(
    private val appProperties: AppProperties = AppProperties,
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
            newDbSuspendedTransaction {
                val dbEntity = getOrCreateSyncSettings()
                dbEntity.githubPatToken = syncSettings.githubPatToken ?: ""
                dbEntity.checkTimeout = syncSettings.checkTimeout
                dbEntity.pullRequestCleanUpTimeout = syncSettings.pullRequestCleanUpTimeout
            }
        }
    }

    suspend fun getLastSyncResult(): Result<SyncResult?> {
        return runCatching {
            newDbSuspendedTransaction {
                DbSyncResult.all().maxByOrNull { it.id }?.load(DbSyncResult::entries, DbSyncResultEntry::repoToCheck)?.toSyncResult()
            }
        }
    }

    /**
     * Get the all sync results without the entries
     */
    suspend fun getAllSyncResults(): Result<List<SyncResult>> {
        return runCatching {
            newDbSuspendedTransaction {
                DbSyncResult.all().toList().with(DbSyncResult::entries, DbSyncResultEntry::repoToCheck).map { it.toSyncResult() }
            }
        }
    }

    /**
     * Get the sync result by id without the entries
     */
    suspend fun getSyncResult(id: Long): Result<SyncResult> {
        return runCatching {
            newDbSuspendedTransaction {
                DbSyncResult.findByIdOrThrow(id).load(DbSyncResult::entries, DbSyncResultEntry::repoToCheck).toSyncResult()
            }
        }
    }

    suspend fun removeSyncResults(ids: List<Long>): Result<Unit> {
        return runCatching {
            newDbSuspendedTransaction {
                DbSyncResult.find { DbSyncResultTable.id inList ids }.forEach { it.delete() }
            }
        }
    }

    suspend fun upsertSyncResult(upsertRequest: UpsertSyncResultRequest): Result<DbSyncResult> {
        return runCatching {
            newDbSuspendedTransaction {
                DbSyncResult.upsertById(upsertRequest.id) {
                    startAt = upsertRequest.startAt
                    endAt = upsertRequest.endAt
                }
            }
        }
    }

    /**
     * Get the sync result entries by the sync result id with the repo to check
     */
    suspend fun getSyncResultEntries(syncResultId: Long): Result<List<SyncResultEntry>> {
        return runCatching {
            newDbSuspendedTransaction {
                DbSyncResultEntry
                    .find { DbSyncResultEntryTable.syncResultId eq syncResultId }
                    .with(DbSyncResultEntry::repoToCheck)
                    .toList()
                    .map { it.toSyncResultEntry() }
            }
        }
    }
    suspend fun upsertSyncResultEntries(upsertRequests: List<UpsertSyncResultEntryRequest>): Result<Unit> {
        return runCatching {
            newDbSuspendedTransaction {
                upsertRequests.forEach { upsertRequest ->
                    val dbSyncResult = DbSyncResult.findByIdOrThrow(upsertRequest.syncResultId)
                    val dbRepoToCheck = upsertRequest.repoToCheckId?.let(DbRepoToCheck::findByIdOrThrow)

                    DbSyncResultEntry.upsertById(upsertRequest.id) {
                        isSuccess = upsertRequest.isSuccess
                        startAt = upsertRequest.startAt
                        endAt = upsertRequest.endAt
                        origin = upsertRequest.origin
                        error = upsertRequest.error
                        errorMessage = upsertRequest.errorMessage
                        syncResultId = dbSyncResult.id
                        repoToCheck = dbRepoToCheck
                    }
                }
            }
        }
    }

    suspend fun getRepoToCheck(id: Long): Result<RepoToCheck> {
        return runCatching {
            newDbSuspendedTransaction {
                DbRepoToCheck.findByIdOrThrow(id).toRepoToCheck()
            }
        }
    }
    suspend fun getAllReposToCheck(): Result<List<RepoToCheck>> {
        return runCatching {
            newDbSuspendedTransaction {
                DbRepoToCheck.all().toList().map { it.toRepoToCheck() }
            }
        }
    }
    suspend fun upsertRepoToCheck(upsertRepoToCheckRequest: UpsertRepoToCheckRequest): Result<Unit> {
        return runCatching {
            newDbSuspendedTransaction {
                DbRepoToCheck.upsertById(upsertRepoToCheckRequest.id) {
                    owner = upsertRepoToCheckRequest.owner
                    name = upsertRepoToCheckRequest.name
                    groupName = upsertRepoToCheckRequest.groupName
                    pullBranchRegex = upsertRepoToCheckRequest.pullBranchRegex
                    arePullRequestsEnabled = upsertRepoToCheckRequest.arePullRequestsEnabled
                    areReleasesEnabled = upsertRepoToCheckRequest.areReleasesEnabled
                }
            }
        }
    }
    suspend fun removeRepoToCheck(id: Long): Result<Unit> {
        return runCatching {
            newDbSuspendedTransaction {
                DbRepoToCheck.findById(id)?.delete()
            }
        }
    }

    suspend fun getPullRequest(id: String): Result<PullRequest> {
        return runCatching {
            newDbSuspendedTransaction {
                DbPullRequest.findByIdOrThrow(id).load(DbPullRequest::repoToCheck).toPullRequest()
            }
        }
    }
    suspend fun getAllPullRequests(): Result<List<PullRequest>> {
        return runCatching {
            newDbSuspendedTransaction {
                DbPullRequest
                    .all()
                    .with(DbPullRequest::repoToCheck, DbPullRequest::reviews)
                    .toList()
                    .map { it.toPullRequest() }
            }
        }
    }
    suspend fun updateAppSeenAt(id: String, appSeenAt: Instant?): Result<Unit> {
        return runCatching {
            newDbSuspendedTransaction {
                DbPullRequest.findByIdOrThrow(id).apply {
                    this.appSeenAt = appSeenAt
                }
            }
        }
    }
    suspend fun upsertPullRequests(pullRequests: List<PullRequest>): Result<Unit> {
        return runCatching {
            newDbSuspendedTransaction {
                pullRequests.forEach { pullRequest ->
                    val dbRepoToCheck = DbRepoToCheck.findByIdOrThrow(pullRequest.repoToCheck.id)
                    DbPullRequest.upsertById(pullRequest.id) {
                        number = pullRequest.number
                        url = pullRequest.url
                        title = pullRequest.title
                        state = pullRequest.state.toString()
                        createdAt = pullRequest.createdAt
                        updatedAt = pullRequest.updatedAt
                        mergedAt = pullRequest.mergedAt
                        isDraft = pullRequest.isDraft
                        baseRef = pullRequest.baseRef
                        headRef = pullRequest.headRef
                        authorLogin = pullRequest.authorLogin
                        authorUrl = pullRequest.authorUrl
                        authorAvatarUrl = pullRequest.authorAvatarUrl
                        appSeenAt = pullRequest.appSeenAt
                        totalCommentsCount = pullRequest.totalCommentsCount
                        mergeable = pullRequest.mergeable.toString()
                        lastCommitCheckRollupStatus = pullRequest.lastCommitCheckRollupStatus.toString()
                        repoToCheck = dbRepoToCheck
                    }
                }
            }
        }
    }
    suspend fun removePullRequests(ids: List<String>): Result<Unit> {
        return runCatching {
            newDbSuspendedTransaction {
                DbPullRequest.find { DbPullRequestTable.id inList ids }.forEach { it.delete() }
            }
        }
    }

    suspend fun getAllReleases(): Result<List<Release>> {
        return runCatching {
            newDbSuspendedTransaction {
                DbRelease.all().with(DbRelease::repoToCheck).toList().map { it.toRelease() }
            }
        }
    }
    suspend fun upsertRelease(release: Release): Result<Unit> {
        return runCatching {
            newDbSuspendedTransaction {
                val dbRepoToCheck = DbRepoToCheck.findByIdOrThrow(release.repoToCheck.id)
                DbRelease.upsertById(release.id) {
                    name = release.name
                    tagName = release.tagName
                    url = release.url
                    publishedAt = release.publishedAt
                    authorLogin = release.authorLogin
                    authorUrl = release.authorUrl
                    authorAvatarUrl = release.authorAvatarUrl
                    repoToCheck = dbRepoToCheck
                }
            }
        }
    }
    suspend fun removeReleases(ids: List<String>): Result<Unit> {
        return runCatching {
            newDbSuspendedTransaction {
                DbRelease.find { DbReleaseTable.id inList ids }.forEach { it.delete() }
            }
        }
    }
    suspend fun removeReleaseByRepoToCheck(repoToCheckId: Long): Result<Unit> {
        return runCatching {
            newDbSuspendedTransaction {
                DbRelease.find { DbReleaseTable.repoToCheckId eq repoToCheckId }.forEach { it.delete() }
            }
        }
    }

    suspend fun removeReviewsByPullRequest(pullRequestIds: List<String>): Result<Unit> {
        return runCatching {
            newDbSuspendedTransaction {
                DbReview.find { DbReviewTable.pullRequestId inList pullRequestIds }.forEach { it.delete() }
            }
        }
    }
    suspend fun upsertReviews(reviews: List<Review>): Result<Unit> {
        if (reviews.isEmpty()) return Result.success(Unit)

        val reviewMap = reviews.groupBy { it.pullRequestId }
        return runCatching {
            newDbSuspendedTransaction {
                reviewMap.forEach { (pullRequestId, reviews) ->
                    val dbPullRequest = DbPullRequest.findByIdOrThrow(pullRequestId)
                    reviews.forEach { review ->
                        DbReview.upsertById(review.id) {
                            submittedAt = review.submittedAt
                            url = review.url
                            state = review.state.toString()
                            authorLogin = review.authorLogin
                            authorUrl = review.authorUrl
                            authorAvatarUrl = review.authorAvatarUrl
                            pullRequest = dbPullRequest
                        }
                    }
                }
            }
        }
    }

    private suspend fun getOrCreateSyncSettings(): DbSyncSettings {
        return newDbSuspendedTransaction {
            val dbSyncSettings = DbSyncSettings.findById(DbSettings.syncSettingsUUID)
            if (dbSyncSettings == null) {
                DbSyncSettings.new(DbSettings.syncSettingsUUID) {
                    githubPatToken = ""
                    checkTimeout = null
                    pullRequestCleanUpTimeout = null
                }
            } else {
                dbSyncSettings
            }
        }
    }
}