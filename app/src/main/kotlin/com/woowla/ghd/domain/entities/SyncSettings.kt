package com.woowla.ghd.domain.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import arrow.optics.optics

@Entity(tableName = "sync_setting")
@optics
data class SyncSettings(
    @PrimaryKey val id: String = ID,
    @ColumnInfo(name = "github_pat_token") val githubPatToken: String,
    @ColumnInfo(name = "check_timeout") val checkTimeout: Long?,
    @ColumnInfo(name = "pull_request_clean_up_timeout") val pullRequestCleanUpTimeout: Long?,
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
        val defaultCheckTimeout = requireNotNull(availableCheckTimeouts.minOrNull())
        fun getValidCheckTimeout(checkTimeout: Long?): Long {
            return if (availableCheckTimeouts.contains(checkTimeout)) {
                requireNotNull(checkTimeout)
            } else {
                defaultCheckTimeout
            }
        }
    }

    @Ignore
    val validCheckTimeout: Long = getValidCheckTimeout(checkTimeout)
    @Ignore
    val validPullRequestCleanUpTimeout: Long = getValidPullRequestCleanUpTimeout(pullRequestCleanUpTimeout)
}
