package com.woowla.ghd.domain.mappers

import com.woowla.ghd.domain.entities.PullRequestGitHubState
import com.woowla.ghd.domain.entities.PullRequestState

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

    fun pullRequestGitHubStateToPullRequestState(isDraft: Boolean, value: PullRequestGitHubState?): PullRequestState {
        return when (value) {
            PullRequestGitHubState.OPEN -> if (isDraft) {
                PullRequestState.DRAFT
            } else {
                PullRequestState.OPEN
            }
            PullRequestGitHubState.MERGED -> PullRequestState.MERGED
            PullRequestGitHubState.CLOSED -> PullRequestState.CLOSED
            PullRequestGitHubState.UNKNOWN, null -> PullRequestState.UNKNOWN
        }
    }
}