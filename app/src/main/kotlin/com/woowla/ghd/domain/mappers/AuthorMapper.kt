package com.woowla.ghd.domain.mappers

import com.woowla.ghd.data.remote.GetLastReleaseQuery
import com.woowla.ghd.data.remote.GetPullRequestsQuery
import org.mapstruct.Named

@Named("Author")
class AuthorMapper {
    @Named("PullRequestAuthorToLoginString")
    fun AuthorToLoginString(value: GetPullRequestsQuery.Author?): String? {
        return value?.login
    }

    @Named("PullRequestAuthorToUrlString")
    fun AuthorToUrlString(value: GetPullRequestsQuery.Author?): String? {
        return value?.avatarUrl?.toString()
    }

    @Named("PullRequestAuthorToAvatarUrlString")
    fun AuthorToAvatarUrlString(value: GetPullRequestsQuery.Author?): String? {
        return value?.avatarUrl?.toString()
    }


    @Named("LastReleaseAuthorToLoginString")
    fun AuthorToLoginString(value: GetLastReleaseQuery.Author?): String? {
        return value?.login
    }

    @Named("LastReleaseAuthorToUrlString")
    fun AuthorToUrlString(value: GetLastReleaseQuery.Author?): String? {
        return value?.avatarUrl?.toString()
    }

    @Named("LastReleaseAuthorToAvatarUrlString")
    fun AuthorToAvatarUrlString(value: GetLastReleaseQuery.Author?): String? {
        return value?.avatarUrl?.toString()
    }
}