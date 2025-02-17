package com.woowla.ghd.domain.entities

/**
 * The order for this enum values is important
 * as it is used for sorting, see [PullRequestWithRepoAndReviews.defaultComparator]
 */
enum class PullRequestStateExtended {
    OPEN,
    DRAFT,
    CLOSED,
    MERGED,
    UNKNOWN,
}