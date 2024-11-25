package com.woowla.ghd.domain.entities

enum class MergeGitHubStateStatus {
    BEHIND,
    BLOCKED,
    CLEAN,
    DIRTY,
    HAS_HOOKS,
    UNSTABLE,
    UNKNOWN,
}