package com.woowla.ghd.domain.mappers

import com.woowla.ghd.data.local.DbAppSettings
import com.woowla.ghd.data.local.DbPullRequest
import com.woowla.ghd.data.local.DbRelease
import com.woowla.ghd.data.local.DbRepoToCheck
import com.woowla.ghd.domain.entities.AppSettings
import com.woowla.ghd.domain.entities.PullRequest
import com.woowla.ghd.domain.entities.Release
import com.woowla.ghd.domain.entities.RepoToCheck
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.factory.Mappers

@Mapper(uses = [InstantMapper::class, CheckTimeoutMapper::class, PullRequestCleanUpTimeoutMapper::class, PullRequestGitHubStateMapper::class])
interface DbMappers {
    companion object {
        val INSTANCE: DbMappers = Mappers.getMapper(DbMappers::class.java)
    }

    @Mapping(target = "checkTimeout", source = "checkTimeout", qualifiedByName = ["CheckTimeout", "CheckTimeoutToValidCheckTimeout"])
    @Mapping(target = "pullRequestCleanUpTimeout", source = "pullRequestCleanUpTimeout", qualifiedByName = ["PullRequestCleanUpTimeout", "PullRequestCleanUpTimeoutToValidPullRequestCleanUpTimeout"])
    fun dbAppSettingsToAppSettings(dbAppSettings: DbAppSettings): AppSettings

    @Mapping(target = "pullNotificationsEnabled", defaultValue = "false")
    @Mapping(target = "releaseNotificationsEnabled", defaultValue = "false")
    fun dbRepoToCheckToRepoToCheck(dbRepoToCheck: DbRepoToCheck): RepoToCheck

    @Mapping(target = "pullNotificationsEnabled", defaultValue = "false")
    @Mapping(target = "releaseNotificationsEnabled", defaultValue = "false")
    fun dbRepoToCheckToRepoToCheck(dbRepoToCheck: List<DbRepoToCheck>): List<RepoToCheck>

    @Mapping(target = "id", source = "dbPullRequest.id")
    @Mapping(target = "gitHubState", source = "dbPullRequest.state")
    fun dbPullRequestToPullRequest(dbPullRequest: DbPullRequest, repoToCheck: RepoToCheck): PullRequest

    @Mapping(target = "id", source = "dbRelease.id")
    @Mapping(target = "name", source = "dbRelease.name")
    fun dbReleaseToRelease(dbRelease: DbRelease, repoToCheck: RepoToCheck): Release
}