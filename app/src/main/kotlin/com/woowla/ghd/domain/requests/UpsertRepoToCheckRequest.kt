package com.woowla.ghd.domain.requests

data class UpsertRepoToCheckRequest(
    val id: Long?,
    val owner: String,
    val name: String,
    val pullNotificationsEnabled: Boolean,
    val releaseNotificationsEnabled: Boolean,
    val groupName: String?,
    val pullBranchRegex: String?
) {
    companion object {
        fun newInstance(): UpsertRepoToCheckRequest {
            return UpsertRepoToCheckRequest(
                id = null,
                owner = "",
                name = "",
                pullNotificationsEnabled = true,
                releaseNotificationsEnabled = true,
                groupName = null,
                pullBranchRegex = null
            )
        }
    }
}