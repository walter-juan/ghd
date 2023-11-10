package com.woowla.ghd.domain.entities

/**
 * The order for this enum values is important
 * as it is used for sorting, see [PullRequest.defaultComparator]
 */
enum class PullRequestStateWithDraft {
    UNKNOWN,
    OPEN,
    DRAFT,
    CLOSED,
    MERGED,
}