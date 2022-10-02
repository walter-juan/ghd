package com.woowla.ghd.domain.mappers

import com.woowla.ghd.domain.entities.PullRequestGitHubState

class PullRequestGitHubStateMapper {
    fun stringToPullRequestGitHubState(value: String?): PullRequestGitHubState {
        return if (value.isNullOrBlank()) {
            PullRequestGitHubState.UNKNOWN
        } else {
            try {
                PullRequestGitHubState.valueOf(value)
            } catch (ex: Exception) {
                PullRequestGitHubState.UNKNOWN
            }
        }
    }
    fun pullRequestGitHubStateToString(value: PullRequestGitHubState?): String {
        return value?.toString() ?: PullRequestGitHubState.UNKNOWN.toString()
    }
}