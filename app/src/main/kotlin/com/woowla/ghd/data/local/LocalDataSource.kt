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
import com.woowla.ghd.data.local.prop.entities.PropAppSettings
import com.woowla.ghd.domain.requests.UpsertPullRequestRequest
import com.woowla.ghd.domain.requests.UpsertReleaseRequest
import com.woowla.ghd.domain.requests.UpsertRepoToCheckRequest
import com.woowla.ghd.data.local.db.utils.findByIdOrThrow
import com.woowla.ghd.data.local.db.utils.upsertById
import com.woowla.ghd.domain.requests.UpsertAppSettingsRequest
import com.woowla.ghd.domain.requests.UpsertReviewRequest
import com.woowla.ghd.domain.requests.UpsertSyncResultEntryRequest
import com.woowla.ghd.domain.requests.UpsertSyncResultRequest
import com.woowla.ghd.domain.requests.UpsertSyncSettingsRequest
import kotlinx.datetime.Instant
import org.jetbrains.exposed.dao.load
import org.jetbrains.exposed.dao.with

class LocalDataSource(
    private val appProperties: AppProperties = AppProperties,
) {
    suspend fun getAppSettings(): Result<PropAppSettings> {
        return runCatching {
            appProperties.load()
            PropAppSettings(
                darkTheme = appProperties.darkTheme,
                encryptedDatabase = appProperties.encryptedDatabase,
                newPullRequestsNotificationsEnabled = appProperties.newPullRequestsNotificationsEnabled,
                updatedPullRequestsNotificationsEnabled = appProperties.updatedPullRequestsNotificationsEnabled,
                newReleaseNotificationsEnabled = appProperties.newReleaseNotificationsEnabled,
                updatedReleaseNotificationsEnabled = appProperties.updatedReleaseNotificationsEnabled,
            )
        }
    }

    suspend fun updateAppSettings(upsertRequest: UpsertAppSettingsRequest): Result<Unit> {
        return runCatching {
            appProperties.load()
            appProperties.darkTheme = upsertRequest.darkTheme
            appProperties.encryptedDatabase = upsertRequest.encryptedDatabase
            appProperties.newPullRequestsNotificationsEnabled = upsertRequest.newPullRequestsNotificationsEnabled
            appProperties.updatedPullRequestsNotificationsEnabled = upsertRequest.updatedPullRequestsNotificationsEnabled
            appProperties.newReleaseNotificationsEnabled = upsertRequest.newReleaseNotificationsEnabled
            appProperties.updatedReleaseNotificationsEnabled = upsertRequest.updatedReleaseNotificationsEnabled
            appProperties.store()
        }
    }

    suspend fun getSyncSettings(): Result<DbSyncSettings> {
        return runCatching {
            getOrCreateSyncSettings()
        }
    }

    suspend fun updateSyncSettings(upsertRequest: UpsertSyncSettingsRequest): Result<Unit> {
        return runCatching {
            newDbSuspendedTransaction {
                val dbEntity = getOrCreateSyncSettings()
                dbEntity.githubPatToken = upsertRequest.githubPatToken
                dbEntity.checkTimeout = upsertRequest.checkTimeout
                dbEntity.pullRequestCleanUpTimeout = upsertRequest.pullRequestCleanUpTimeout
            }
        }
    }

    suspend fun getLastSyncResult(): Result<DbSyncResult?> {
        return runCatching {
            newDbSuspendedTransaction {
                DbSyncResult.all().maxByOrNull { it.id }?.load(DbSyncResult::entries, DbSyncResultEntry::repoToCheck)
            }
        }
    }

    /**
     * Get the all sync results without the entries
     */
    suspend fun getAllSyncResults(): Result<List<DbSyncResult>> {
        return runCatching {
            newDbSuspendedTransaction {
                DbSyncResult.all().toList().with(DbSyncResult::entries, DbSyncResultEntry::repoToCheck)
            }
        }
    }

    /**
     * Get the sync result by id without the entries
     */
    suspend fun getSyncResult(id: Long): Result<DbSyncResult> {
        return runCatching {
            newDbSuspendedTransaction {
                DbSyncResult.findByIdOrThrow(id).load(DbSyncResult::entries, DbSyncResultEntry::repoToCheck)
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
    suspend fun getSyncResultEntries(syncResultId: Long): Result<List<DbSyncResultEntry>> {
        return runCatching {
            newDbSuspendedTransaction {
                DbSyncResultEntry.find { DbSyncResultEntryTable.syncResultId eq syncResultId }.with(DbSyncResultEntry::repoToCheck).toList()
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

    suspend fun getRepoToCheck(id: Long): Result<DbRepoToCheck> {
        return runCatching {
            newDbSuspendedTransaction {
                DbRepoToCheck.findByIdOrThrow(id)
            }
        }
    }
    suspend fun getAllReposToCheck(): Result<List<DbRepoToCheck>> {
        return runCatching {
            newDbSuspendedTransaction {
                DbRepoToCheck.all().toList()
            }
        }
    }
    suspend fun upsertRepoToCheck(upsertRequest: UpsertRepoToCheckRequest): Result<Unit> {
        return runCatching {
            newDbSuspendedTransaction {
                DbRepoToCheck.upsertById(upsertRequest.id) {
                    owner = upsertRequest.owner
                    name = upsertRequest.name
                    groupName = upsertRequest.groupName
                    pullBranchRegex = upsertRequest.pullBranchRegex
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

    suspend fun getPullRequest(id: String): Result<DbPullRequest> {
        return runCatching {
            newDbSuspendedTransaction {
                DbPullRequest.findByIdOrThrow(id).load(DbPullRequest::repoToCheck)
            }
        }
    }
    suspend fun getAllPullRequests(): Result<List<DbPullRequest>> {
        return runCatching {
            newDbSuspendedTransaction {
                DbPullRequest.all().with(DbPullRequest::repoToCheck, DbPullRequest::reviews).toList()
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
    suspend fun upsertPullRequests(upsertRequests: List<UpsertPullRequestRequest>): Result<Unit> {
        return runCatching {
            newDbSuspendedTransaction {
                upsertRequests.forEach { upsertRequest ->
                    val dbRepoToCheck = DbRepoToCheck.findByIdOrThrow(upsertRequest.repoToCheckId)
                    DbPullRequest.upsertById(upsertRequest.id) {
                        number = upsertRequest.number
                        url = upsertRequest.url
                        title = upsertRequest.title
                        state = upsertRequest.state
                        createdAt = upsertRequest.createdAt
                        updatedAt = upsertRequest.updatedAt
                        mergedAt = upsertRequest.mergedAt
                        draft = upsertRequest.draft
                        baseRef = upsertRequest.baseRef
                        headRef = upsertRequest.headRef
                        authorLogin = upsertRequest.authorLogin
                        authorUrl = upsertRequest.authorUrl
                        authorAvatarUrl = upsertRequest.authorAvatarUrl
                        appSeenAt = upsertRequest.appSeenAt
                        totalCommentsCount = upsertRequest.totalCommentsCount
                        mergeable = upsertRequest.mergeable
                        lastCommitCheckRollupStatus = upsertRequest.lastCommitCheckRollupStatus
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

    suspend fun getAllReleases(): Result<List<DbRelease>> {
        return runCatching {
            newDbSuspendedTransaction {
                DbRelease.all().with(DbRelease::repoToCheck).toList()
            }
        }
    }
    suspend fun upsertRelease(upsertRequest: UpsertReleaseRequest): Result<Unit> {
        return runCatching {
            newDbSuspendedTransaction {
                val dbRepoToCheck = DbRepoToCheck.findByIdOrThrow(upsertRequest.repoToCheckId)
                DbRelease.upsertById(upsertRequest.id) {
                    name = upsertRequest.name
                    tagName = upsertRequest.tagName
                    url = upsertRequest.url
                    publishedAt = upsertRequest.publishedAt
                    authorLogin = upsertRequest.authorLogin
                    authorUrl = upsertRequest.authorUrl
                    authorAvatarUrl = upsertRequest.authorAvatarUrl
                    repoToCheck = dbRepoToCheck
                }
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
    suspend fun upsertReviews(upsertRequests: List<UpsertReviewRequest>): Result<Unit> {
        return runCatching {
            newDbSuspendedTransaction {
                upsertRequests.forEach { upsertRequest ->
                    val dbPullRequest = DbPullRequest.findByIdOrThrow(upsertRequest.pullRequestId)
                    DbReview.upsertById(upsertRequest.id) {
                        submittedAt = upsertRequest.submittedAt
                        url = upsertRequest.url
                        state = upsertRequest.state
                        authorLogin = upsertRequest.authorLogin
                        authorUrl = upsertRequest.authorUrl
                        authorAvatarUrl = upsertRequest.authorAvatarUrl
                        pullRequest = dbPullRequest
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