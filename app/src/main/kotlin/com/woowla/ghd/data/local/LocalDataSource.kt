package com.woowla.ghd.data.local

import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import com.woowla.ghd.AppFolderFactory
import com.woowla.ghd.domain.requests.UpsertPullRequestRequest
import com.woowla.ghd.domain.requests.UpsertReleaseRequest
import com.woowla.ghd.domain.requests.UpsertRepoToCheckRequest
import java.nio.file.Paths
import java.util.*

class LocalDataSource(
    database: GhdDatabase = ghdDatabaseInstance,
) {
    companion object {
        private val ghdDatabaseInstance: GhdDatabase by lazy {
            val name = "ghd.sqlite"
            val path = Paths.get(AppFolderFactory.folder, name)

            val url = "jdbc:sqlite:$path"

            val driver = JdbcSqliteDriver(
                url,
                Properties(1).apply { put("foreign_keys", "true") }
            )

            GhdDatabase.Schema.create(driver)
            GhdDatabase(driver)
        }
    }

    private val appSettingsUUID = "06f16337-4ded-4296-8b51-18b23fe3c1c4"
    private val appSettingsQueries = database.appSettingsQueries
    private val repoToCheckQueries = database.repoToCheckQueries
    private val pullRequestQueries = database.pullRequestQueries
    private val releaseQueries = database.releaseQueries

    suspend fun getAppSettings(): Result<DbAppSettings> {
        return runCatching {
            getOrCreateSettings()
        }
    }
    suspend fun updateAppSettings(dbAppSettings: DbAppSettings): Result<Unit> {
        return runCatching {
            appSettingsQueries.insertOrUpdate(dbAppSettings.copy(id = appSettingsUUID))
        }
    }

    suspend fun getRepoToCheck(id: Long): Result<DbRepoToCheck> {
        return runCatching {
            repoToCheckQueries.select(id).executeAsOne()
        }
    }
    suspend fun getAllReposToCheck(): Result<List<DbRepoToCheck>> {
        return runCatching {
            repoToCheckQueries.selectAll().executeAsList()
        }
    }
    suspend fun upsertRepoToCheck(upsertRequest: UpsertRepoToCheckRequest): Result<Unit> {
        return runCatching {
            repoToCheckQueries.insertOrUpdate(
                id = upsertRequest.id,
                owner = upsertRequest.owner,
                name = upsertRequest.name,
                pullNotificationsEnabled = upsertRequest.pullNotificationsEnabled,
                releaseNotificationsEnabled = upsertRequest.releaseNotificationsEnabled,
                groupName = upsertRequest.groupName,
                pullBranchRegex = upsertRequest.pullBranchRegex
            )
        }
    }
    suspend fun removeRepoToCheck(id: Long): Result<Unit> {
        return runCatching {
            repoToCheckQueries.delete(id)
        }
    }

    suspend fun getPullRequest(id: String): Result<DbPullRequest> {
        return runCatching {
            pullRequestQueries.select(id = id).executeAsOne()
        }
    }
    suspend fun getPullRequestsByRepository(repoToCheckId: Long): Result<List<DbPullRequest>> {
        return runCatching {
            pullRequestQueries.selectByRepoToCheck(repoToCheckId = repoToCheckId).executeAsList()
        }
    }
    suspend fun getAllPullRequests(): Result<List<DbPullRequest>> {
        return runCatching {
            pullRequestQueries.selectAll().executeAsList()
        }
    }
    suspend fun upsertPullRequest(upsertRequest: UpsertPullRequestRequest): Result<Unit> {
        return upsertPullRequests(listOf(upsertRequest))
    }
    suspend fun updateAppSeenAt(id: String, appSeenAt: String?): Result<Unit> {
        return runCatching {
            pullRequestQueries.updateAppSeenAt(id = id, appSeenAt = appSeenAt)
        }
    }
    suspend fun upsertPullRequests(upsertRequests: List<UpsertPullRequestRequest>): Result<Unit> {
        return runCatching {
            upsertRequests.forEach { upsertRequest ->
                pullRequestQueries.insertOrUpdate(
                    id = upsertRequest.id,
                    number = upsertRequest.number,
                    url = upsertRequest.url,
                    title = upsertRequest.title,
                    state = upsertRequest.state,
                    createdAt = upsertRequest.createdAt,
                    updatedAt = upsertRequest.updatedAt,
                    mergedAt = upsertRequest.mergedAt,
                    draft = upsertRequest.draft,
                    baseRef = upsertRequest.baseRef,
                    headRef = upsertRequest.headRef,
                    authorLogin = upsertRequest.authorLogin,
                    authorUrl = upsertRequest.authorUrl,
                    authorAvatarUrl = upsertRequest.authorAvatarUrl,
                    appSeenAt = upsertRequest.appSeenAt,
                    repoToCheckId = upsertRequest.repoToCheckId,
                )
            }
        }
    }
    suspend fun removePullRequests(id: String): Result<Unit> {
        return removePullRequests(listOf(id))
    }
    suspend fun removePullRequests(ids: List<String>): Result<Unit> {
        return runCatching {
            ids.forEach { id ->
                pullRequestQueries.delete(id)
            }
        }
    }

    suspend fun getReleaseByRepository(repoToCheckId: Long): Result<DbRelease> {
        return runCatching {
            releaseQueries.selectByRepoToCheck(repoToCheckId = repoToCheckId).executeAsOne()
        }
    }
    suspend fun getAllReleases(): Result<List<DbRelease>> {
        return runCatching {
            releaseQueries.selectAll().executeAsList()
        }
    }
    suspend fun upsertRelease(upsertRequest: UpsertReleaseRequest): Result<Unit> {
        return runCatching {
            releaseQueries.insertOrUpdate(
                id = upsertRequest.id,
                name = upsertRequest.name,
                tagName = upsertRequest.tagName,
                url = upsertRequest.url,
                publishedAt = upsertRequest.publishedAt,
                authorLogin = upsertRequest.authorLogin,
                authorUrl = upsertRequest.authorUrl,
                authorAvatarUrl = upsertRequest.authorAvatarUrl,
                repoToCheckId = upsertRequest.repoToCheckId,
            )
        }
    }
    suspend fun removeRelease(id: String): Result<Unit> {
        return runCatching {
            releaseQueries.delete(id)
        }
    }
    suspend fun removeReleaseByRepoToCheck(repoToCheckId: Long): Result<Unit> {
        return runCatching {
            releaseQueries.deleteByRepoToCheck(repoToCheckId)
        }
    }

    private fun getOrCreateSettings(): DbAppSettings {
        val query = appSettingsQueries.select(id = appSettingsUUID)
        val dbAppSettings = query.executeAsOneOrNull()

        return if (dbAppSettings == null) {
            appSettingsQueries.insertOrUpdate(DbAppSettings(
                id = appSettingsUUID,
                githubPatToken = "",
                checkTimeout = null,
                synchronizedAt = null,
                appDarkTheme = null,
                pullRequestCleanUpTimeout = null,
            ))
            appSettingsQueries.select(id = appSettingsUUID).executeAsOne()
        } else {
            dbAppSettings
        }
    }
}