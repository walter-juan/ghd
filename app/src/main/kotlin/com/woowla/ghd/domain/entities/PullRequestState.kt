package com.woowla.ghd.domain.entities

enum class PullRequestState {
    OPEN,
    CLOSED,
    MERGED,
    UNKNOWN,
}

fun PullRequestState.toPullRequestStateExtended(isDraft: Boolean): PullRequestStateExtended {
    return when (this) {
        PullRequestState.OPEN -> if (isDraft) {
            PullRequestStateExtended.DRAFT
        } else {
            PullRequestStateExtended.OPEN
        }
        PullRequestState.MERGED -> PullRequestStateExtended.MERGED
        PullRequestState.CLOSED -> PullRequestStateExtended.CLOSED
        PullRequestState.UNKNOWN -> PullRequestStateExtended.UNKNOWN
    }
}