package com.woowla.ghd.domain.requests

import kotlinx.datetime.Instant

data class UpsertReleaseRequest(
    val id: String,
    val name: String?,
    val tagName: String?,
    val url: String?,
    val publishedAt: Instant?,
    val authorLogin: String?,
    val authorUrl: String?,
    val authorAvatarUrl: String?,
    val repoToCheckId: Long
)