package com.woowla.ghd.domain.requests

import kotlinx.datetime.Instant

data class UpsertPullRequestRequest(
    val id: String,
    val number: Long?,
    val url: String?,
    val state: String?,
    val title: String?,
    val createdAt: Instant?,
    val updatedAt: Instant?,
    val mergedAt: Instant?,
    val draft: Boolean?,
    val baseRef: String?,
    val headRef: String?,
    val authorLogin: String?,
    val authorUrl: String?,
    val authorAvatarUrl: String?,
    val appSeenAt: Instant?,
    val totalCommentsCount: Long?,
    val repoToCheckId: Long
)