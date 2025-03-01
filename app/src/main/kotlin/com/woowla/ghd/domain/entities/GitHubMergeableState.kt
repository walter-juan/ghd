package com.woowla.ghd.domain.entities

/**
 * Whether or not the pull request can be merged based on the existence of merge conflicts.
 */
enum class GitHubMergeableState {
    /**
     * The pull request cannot be merged due to merge conflicts.
     */
    CONFLICTING,

    /**
     * The pull request can be merged.
     */
    MERGEABLE,

    /**
     * The mergeability of the pull request is still being calculated.
     */
    UNKNOWN,
}