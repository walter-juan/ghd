package com.woowla.ghd.domain.entities

import kotlin.time.Instant

data class Deployment(
    val id: String,
    val description: String?,
    val payload: String?,
    val creator: Author,

    val environment: String?,
    val latestEnvironment: String?,
    val originalEnvironment: String?,

    val state: DeploymentState,
    val task: String?,

    val statuses: List<DeploymentStatus>,

    val createdAt: Instant,
    val updatedAt: Instant,
)
