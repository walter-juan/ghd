package com.woowla.ghd.domain.mappers

import com.woowla.ghd.domain.entities.AppSettings
import org.mapstruct.Named

@Named("PullRequestCleanUpTimeout")
class PullRequestCleanUpTimeoutMapper {
    @Named("PullRequestCleanUpTimeoutToValidPullRequestCleanUpTimeout")
    fun pullRequestCleanUpTimeoutToValidPullRequestCleanUpTimeout(cleanUpTimeout: Long?): Long {
        return AppSettings.getValidPullRequestCleanUpTimeout(cleanUpTimeout)
    }
}