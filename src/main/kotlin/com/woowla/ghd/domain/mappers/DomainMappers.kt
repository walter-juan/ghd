package com.woowla.ghd.domain.mappers

import com.woowla.ghd.data.local.DbAppSettings
import com.woowla.ghd.domain.entities.AppSettings
import com.woowla.ghd.domain.entities.PullRequest
import com.woowla.ghd.domain.entities.Release
import com.woowla.ghd.domain.entities.RepoToCheck
import com.woowla.ghd.domain.requests.UpsertPullRequestRequest
import com.woowla.ghd.domain.requests.UpsertReleaseRequest
import com.woowla.ghd.domain.requests.UpsertRepoToCheckRequest
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.factory.Mappers

@Mapper(uses = [InstantMapper::class, PullRequestGitHubStateMapper::class])
interface DomainMappers {
    companion object {
        val INSTANCE: DomainMappers = Mappers.getMapper(DomainMappers::class.java)
    }

    fun appSettingsToDbAppSettings(appSettings: AppSettings): DbAppSettings

    @Mapping(target = "state", source = "gitHubState")
    fun pullRequestToUpsertRequest(pullRequest: PullRequest): UpsertPullRequestRequest

    fun releaseToUpsertRequest(release: Release): UpsertReleaseRequest

    fun repoToCheckToUpsertRequest(repoToCheck: RepoToCheck): UpsertRepoToCheckRequest
}