package com.woowla.ghd.domain.entities

import kotlin.time.Instant

data class DeploymentStatus(
    val id: String,
    val description: String?,
    val state: DeploymentStatusState,
    val createdAt: Instant,
    val updatedAt: Instant,
)
