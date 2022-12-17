package com.woowla.ghd.data.local

import com.woowla.ghd.data.local.db.DbSettings
import com.woowla.ghd.data.local.db.entities.DbPullRequest
import com.woowla.ghd.data.local.db.entities.DbRelease
import com.woowla.ghd.data.local.db.entities.DbRepoToCheck
import com.woowla.ghd.data.local.db.entities.DbSyncSettings
import com.woowla.ghd.data.local.db.newDbSuspendedTransaction
import com.woowla.ghd.data.local.db.tables.DbPullRequestTable
import com.woowla.ghd.data.local.db.tables.DbReleaseTable
import com.woowla.ghd.data.local.prop.AppProperties
import com.woowla.ghd.data.local.prop.entities.PropAppSettings
import com.woowla.ghd.domain.requests.UpsertPullRequestRequest
import com.woowla.ghd.domain.requests.UpsertReleaseRequest
import com.woowla.ghd.domain.requests.UpsertRepoToCheckRequest
import com.woowla.ghd.data.local.db.utils.findByIdOrThrow
import com.woowla.ghd.data.local.db.utils.upsertById
import com.woowla.ghd.domain.requests.UpsertAppSettings
import com.woowla.ghd.domain.requests.UpsertSyncSettings
import kotlinx.datetime.Instant
import kotlinx.datetime.toInstant
import org.jetbrains.exposed.dao.load
import org.jetbrains.exposed.dao.with

class LocalDataSource(
    private val appProperties: AppProperties = AppProperties,
) {
    suspend fun getAppSettings(): Result<PropAppSettings> {
        return runCatching {
            appProperties.load()
            PropAppSettings(
                darkTheme = appProperties.darkTheme
            )
        }
    }

    suspend fun updateAppSettings(upsertRequest: UpsertAppSettings): Result<Unit> {
        return runCatching {
            appProperties.load()
            appProperties.darkTheme = upsertRequest.darkTheme
            appProperties.store()
        }
    }

    suspend fun getSyncSettings(): Result<DbSyncSettings> {
        return runCatching {
            getOrCreateSyncSettings()
        }
    }

    suspend fun updateSyncSettings(upsertRequest: UpsertSyncSettings): Result<Unit> {
        return runCatching {
            newDbSuspendedTransaction {
                val dbEntity = getOrCreateSyncSettings()
                dbEntity.githubPatToken = upsertRequest.githubPatToken
                dbEntity.checkTimeout = upsertRequest.checkTimeout
                dbEntity.synchronizedAt = upsertRequest.synchronizedAt
                dbEntity.pullRequestCleanUpTimeout = upsertRequest.pullRequestCleanUpTimeout
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
                    pullNotificationsEnabled = upsertRequest.pullNotificationsEnabled
                    releaseNotificationsEnabled = upsertRequest.releaseNotificationsEnabled
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
                DbPullRequest.all().with(DbPullRequest::repoToCheck).toList()
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

    private suspend fun getOrCreateSyncSettings(): DbSyncSettings {
        return newDbSuspendedTransaction {
            val dbSyncSettings = DbSyncSettings.findById(DbSettings.syncSettingsUUID)
            if (dbSyncSettings == null) {
                DbSyncSettings.new(DbSettings.syncSettingsUUID) {
                    githubPatToken = ""
                    checkTimeout = null
                    synchronizedAt = null
                    pullRequestCleanUpTimeout = null
                }
            } else {
                dbSyncSettings
            }
        }
    }
}