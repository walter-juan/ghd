package com.woowla.ghd.domain.entities

data class RepoToCheck(
    val id: Long = 0,
    val repository: Repository?,
    val groupName: String?,
    val pullBranchRegex: String?,
    val deploymentRefNameRegex: String?,
    val deploymentEnvironments: String?,
    val deploymentDownloadLimit: Int,
    val arePullRequestsEnabled: Boolean,
    val areReleasesEnabled: Boolean,
    val arePullRequestsNotificationsEnabled: Boolean,
    val areReleasesNotificationsEnabled: Boolean,
    val areDeploymentsEnabled: Boolean,
) : Comparable<RepoToCheck> {
    companion object {
        const val DEFAULT_DEPLOYMENT_DOWNLOAD_LIMIT = 10

        fun newInstance() = RepoToCheck(
            repository = null,
            groupName = null,
            pullBranchRegex = null,
            deploymentRefNameRegex = null,
            deploymentEnvironments = null,
            deploymentDownloadLimit = DEFAULT_DEPLOYMENT_DOWNLOAD_LIMIT,
            arePullRequestsEnabled = false,
            areReleasesEnabled = false,
            arePullRequestsNotificationsEnabled = false,
            areReleasesNotificationsEnabled = false,
            areDeploymentsEnabled = false,
        )
        val defaultComparator = compareBy<RepoToCheck> { it.groupName }.thenBy { it.repository?.name }
    }

    val deploymentEnvironmentsList: List<String> = deploymentEnvironments
        ?.split(",")
        ?.map { it.trim() }
        ?.filter { it.isNotEmpty() }
        ?: listOf()

    override fun compareTo(other: RepoToCheck): Int {
        return defaultComparator.compare(this, other)
    }
}