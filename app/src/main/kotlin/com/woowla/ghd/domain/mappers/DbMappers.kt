package com.woowla.ghd.domain.mappers

import com.woowla.ghd.data.local.db.entities.DbPullRequest
import com.woowla.ghd.data.local.db.entities.DbRelease
import com.woowla.ghd.data.local.db.entities.DbRepoToCheck
import com.woowla.ghd.data.local.db.entities.DbSyncSettings
import com.woowla.ghd.domain.entities.SyncSettings
import com.woowla.ghd.domain.entities.PullRequest
import com.woowla.ghd.domain.entities.Release
import com.woowla.ghd.domain.entities.RepoToCheck
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.factory.Mappers

@Mapper(uses = [EntityIdMapper::class, CheckTimeoutMapper::class, PullRequestCleanUpTimeoutMapper::class, PullRequestGitHubStateMapper::class])
interface DbMappers {
    companion object {
        val INSTANCE: DbMappers = Mappers.getMapper(DbMappers::class.java)
    }

    @Mapping(target = "checkTimeout", source = "checkTimeout", qualifiedByName = ["CheckTimeout", "CheckTimeoutToValidCheckTimeout"])
    @Mapping(target = "pullRequestCleanUpTimeout", source = "pullRequestCleanUpTimeout", qualifiedByName = ["PullRequestCleanUpTimeout", "PullRequestCleanUpTimeoutToValidPullRequestCleanUpTimeout"])
    fun dbSyncSettingsToSyncSettings(dbSyncSettings: DbSyncSettings): SyncSettings

    @Mapping(target = "pullNotificationsEnabled", defaultValue = "false")
    @Mapping(target = "releaseNotificationsEnabled", defaultValue = "false")
    fun dbRepoToCheckToRepoToCheck(dbRepoToCheck: DbRepoToCheck): RepoToCheck

    @Mapping(target = "pullNotificationsEnabled", defaultValue = "false")
    @Mapping(target = "releaseNotificationsEnabled", defaultValue = "false")
    fun dbRepoToCheckToRepoToCheck(dbRepoToCheck: List<DbRepoToCheck>): List<RepoToCheck>

    @Mapping(target = "id", source = "dbPullRequest.id")
    @Mapping(target = "gitHubState", source = "dbPullRequest.state")
    @Mapping(target = "repoToCheck", source = "dbPullRequest.repoToCheck")
    fun dbPullRequestToPullRequest(dbPullRequest: DbPullRequest): PullRequest

    @Mapping(target = "id", source = "dbPullRequest.id")
    @Mapping(target = "gitHubState", source = "dbPullRequest.state")
    @Mapping(target = "repoToCheck", source = "dbPullRequest.repoToCheck")
    fun dbPullRequestToPullRequest(dbPullRequest: List<DbPullRequest>): List<PullRequest>

    @Mapping(target = "id", source = "dbRelease.id")
    @Mapping(target = "name", source = "dbRelease.name")
    @Mapping(target = "repoToCheck", source = "dbRelease.repoToCheck")
    fun dbReleaseToRelease(dbRelease: DbRelease): Release

    @Mapping(target = "id", source = "dbRelease.id")
    @Mapping(target = "name", source = "dbRelease.name")
    @Mapping(target = "repoToCheck", source = "dbRelease.repoToCheck")
    fun dbReleaseToRelease(dbRelease: List<DbRelease>): List<Release>
}