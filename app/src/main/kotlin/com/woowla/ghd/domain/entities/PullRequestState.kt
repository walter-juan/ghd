package com.woowla.ghd.domain.entities

/**
 * The order for this enum values is important
 * as it is used for sorting, see [PullRequest.defaultComparator]
 */
enum class PullRequestState {
    UNKNOWN,
    OPEN,
    DRAFT,
    CLOSED,
    MERGED,
}