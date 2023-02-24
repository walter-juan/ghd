package com.woowla.ghd.domain.requests

import kotlinx.datetime.Instant

data class UpsertReviewRequest(
    val id: String,
    val state: String?,
    val url: String,
    val submittedAt: Instant?,
    val authorLogin: String?,
    val authorUrl: String?,
    val authorAvatarUrl: String?,
    val pullRequestId: String
)