package com.woowla.ghd.domain.entities

data class RepoToCheck(
    val id: Long = 0,
    val gitHubRepository: GitHubRepository?,
    val groupName: String?,
    val pullBranchRegex: String?,
    val arePullRequestsEnabled: Boolean,
    val areReleasesEnabled: Boolean,
    val arePullRequestsNotificationsEnabled: Boolean,
    val areReleasesNotificationsEnabled: Boolean,
) : Comparable<RepoToCheck> {
    companion object {
        fun newInstance() = RepoToCheck(
            gitHubRepository = null,
            groupName = null,
            pullBranchRegex = null,
            arePullRequestsEnabled = false,
            areReleasesEnabled = false,
            arePullRequestsNotificationsEnabled = false,
            areReleasesNotificationsEnabled = false,
        )
        val defaultComparator = compareBy<RepoToCheck> { it.groupName }.thenBy { it.gitHubRepository?.name }
    }

    override fun compareTo(other: RepoToCheck): Int {
        return defaultComparator.compare(this, other)
    }
}