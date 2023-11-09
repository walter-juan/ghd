package com.woowla.ghd.domain.requests

data class UpsertRepoToCheckRequest(
    val id: Long?,
    val owner: String,
    val name: String,
    val groupName: String?,
    val pullBranchRegex: String?
) {
    companion object {
        fun newInstance(): UpsertRepoToCheckRequest {
            return UpsertRepoToCheckRequest(
                id = null,
                owner = "",
                name = "",
                groupName = null,
                pullBranchRegex = null
            )
        }
    }
}