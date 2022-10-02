package com.woowla.ghd.domain.requests

data class UpsertReleaseRequest(
    val id: String,
    val name: String?,
    val tagName: String?,
    val url: String?,
    val publishedAt: String?,
    val authorLogin: String?,
    val authorUrl: String?,
    val authorAvatarUrl: String?,
    val repoToCheckId: Long
)