package com.woowla.ghd.domain.entities

/**
 * Comprehensive status regarding whether the pull request meets all the requirements to be merged
 */
enum class MergeGitHubStateStatus {
    /**
     * The PR is clean, can be merged
     */
    CLEAN,

    /**
     * The PR is blocked (e.g., by failing checks or insufficient reviews)
     */
    BLOCKED,

    /**
     * The branch is behind the target branch and needs to be updated
     */
    BEHIND,

    /**
     * The PR has merge conflicts
     */
    DIRTY,

    /**
     * Merge is blocked by Git hooks
     */
    HAS_HOOKS,

    /**
     * The merge is unstable due to failing required checks
     */
    UNSTABLE,

    /**
     * Merge state is unknown
     */
    UNKNOWN,
}