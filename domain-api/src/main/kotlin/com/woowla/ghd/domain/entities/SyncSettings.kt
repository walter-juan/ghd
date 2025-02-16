package com.woowla.ghd.domain.entities

import arrow.optics.optics

@optics
data class SyncSettings(
    val id: String = ID,
    val githubPatToken: String,
    val checkTimeout: Long?,
    val pullRequestCleanUpTimeout: Long?,
) {
    companion object {
        const val ID = "06f16337-4ded-4296-8b51-18b23fe3c1c4"

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
        const val DEFAULT_CHECKOUT_TIMEOUT = 5L
        fun getValidCheckTimeout(checkTimeout: Long?): Long {
            return if (availableCheckTimeouts.contains(checkTimeout)) {
                requireNotNull(checkTimeout)
            } else {
                DEFAULT_CHECKOUT_TIMEOUT
            }
        }
    }

    val validCheckTimeout: Long = getValidCheckTimeout(checkTimeout)

    val validPullRequestCleanUpTimeout: Long = getValidPullRequestCleanUpTimeout(pullRequestCleanUpTimeout)
}
