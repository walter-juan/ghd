package com.woowla.ghd.domain.entities

data class RepoToCheck(
    val id: Long,
    val owner: String,
    val name: String,
    val groupName: String?,
    val pullBranchRegex: String?
)