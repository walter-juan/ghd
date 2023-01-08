package com.woowla.ghd.domain.entities

import kotlinx.datetime.Instant

data class SyncSettings(
    val githubPatToken: String?,
    val checkTimeout: Long?,
    val pullRequestCleanUpTimeout: Long?,
    val synchronizedAt: Instant?,
) {
    companion object {
        val availablePullRequestCleanUpTimeout = listOf<Long>(1, 2, 4, 8, 16, 24, 48, 72)
        val defaultPullRequestCleanUpTimeout = requireNotNull(availablePullRequestCleanUpTimeout.maxOrNull())
        fun getValidPullRequestCleanUpTimeout(pullRequestCleanUpTimeout: Long?): Long {
            return if (availablePullRequestCleanUpTimeout.contains(pullRequestCleanUpTimeout)) {
                requireNotNull(pullRequestCleanUpTimeout)
            } else {
                defaultPullRequestCleanUpTimeout
            }
        }


        val availableCheckTimeouts = listOf<Long>(1, 5, 10, 15, 30)
        val defaultCheckTimeout = requireNotNull(availableCheckTimeouts.minOrNull())
        fun getValidCheckTimeout(checkTimeout: Long?): Long {
            return if (availableCheckTimeouts.contains(checkTimeout)) {
                requireNotNull(checkTimeout)
            } else {
                defaultCheckTimeout
            }
        }
    }

    fun getValidPullRequestCleanUpTimeout(): Long = Companion.getValidPullRequestCleanUpTimeout(pullRequestCleanUpTimeout)
}
