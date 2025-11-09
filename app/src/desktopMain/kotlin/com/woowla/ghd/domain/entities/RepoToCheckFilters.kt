package com.woowla.ghd.domain.entities

import arrow.optics.optics

@optics data class RepoToCheckFilters(
    val groupNames: Set<String>,
    val pullRequestSyncEnabled: Boolean,
    val pullRequestNotificationsEnabled: Boolean,
    val pullRequestBranchFilterActive: Boolean,
    val releasesSyncEnabled: Boolean,
    val releasesNotificationsEnabled: Boolean,
    val deploymentsSyncEnabled: Boolean,
) {
    companion object {
        val Default = RepoToCheckFilters(
            groupNames = emptySet(),
            pullRequestSyncEnabled = false,
            pullRequestNotificationsEnabled = false,
            pullRequestBranchFilterActive = false,
            releasesSyncEnabled = false,
            releasesNotificationsEnabled = false,
            deploymentsSyncEnabled = false,
        )
    }

    fun anyFilterActive(): Boolean {
        return pullRequestSyncEnabled ||
                pullRequestNotificationsEnabled ||
                pullRequestBranchFilterActive ||
                releasesSyncEnabled ||
                releasesNotificationsEnabled ||
                deploymentsSyncEnabled
    }
}