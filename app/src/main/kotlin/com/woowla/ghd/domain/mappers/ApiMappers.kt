package com.woowla.ghd.domain.mappers

import com.woowla.ghd.data.remote.GetLastReleaseQuery
import com.woowla.ghd.data.remote.GetPullRequestsQuery
import com.woowla.ghd.domain.requests.UpsertPullRequestRequest
import com.woowla.ghd.domain.requests.UpsertReleaseRequest
import kotlinx.datetime.Instant
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.factory.Mappers

@Mapper(uses = [InstantMapper::class, AuthorMapper::class, AnyMapper::class])
interface ApiMappers {
    companion object {
        val INSTANCE: ApiMappers = Mappers.getMapper(ApiMappers::class.java)
    }

    @Mapping(target = "baseRef", source = "pullRequestNode.baseRefName")
    @Mapping(target = "headRef", source = "pullRequestNode.headRefName")
    @Mapping(target = "authorLogin", source = "pullRequestNode.author", qualifiedByName = ["Author", "PullRequestAuthorToLoginString"])
    @Mapping(target = "authorUrl", source = "pullRequestNode.author", qualifiedByName = ["Author", "PullRequestAuthorToUrlString"])
    @Mapping(target = "authorAvatarUrl", source = "pullRequestNode.author", qualifiedByName = ["Author", "PullRequestAuthorToAvatarUrlString"])
    fun pullRequestNodeToUpsertRequest(pullRequestNode: GetPullRequestsQuery.Node, appSeenAt: Instant?, repoToCheckId: Long): UpsertPullRequestRequest

    @Mapping(target = "authorLogin", source = "lastRelease.author", qualifiedByName = ["Author", "LastReleaseAuthorToLoginString"])
    @Mapping(target = "authorUrl", source = "lastRelease.author", qualifiedByName = ["Author", "LastReleaseAuthorToUrlString"])
    @Mapping(target = "authorAvatarUrl", source = "lastRelease.author", qualifiedByName = ["Author", "LastReleaseAuthorToAvatarUrlString"])
    fun lastReleaseToUpsertRequest(lastRelease: GetLastReleaseQuery.LatestRelease, repoToCheckId: Long): UpsertReleaseRequest
}