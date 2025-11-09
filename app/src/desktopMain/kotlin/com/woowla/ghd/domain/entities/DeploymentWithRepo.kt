package com.woowla.ghd.domain.entities

data class DeploymentWithRepo(
    val deployment: Deployment,
    val repoToCheck: RepoToCheck,
)
