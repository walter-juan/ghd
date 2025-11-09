package com.woowla.ghd.domain.entities

import kotlin.time.Instant

data class Deployment(
    val id: String,
    val repoToCheckId: Long,
    val state: DeploymentState,
    val environment: String?,
    val refName: String?,
    val refPrefix: String?,
    val createdAt: Instant,
    val updatedAt: Instant,
    val author: Author?,
)
