package com.woowla.ghd.domain.requests

data class UpsertPullRequestRequest(
    val id: String,
    val number: Long?,
    val url: String?,
    val state: String?,
    val title: String?,
    val createdAt: String?,
    val updatedAt: String?,
    val mergedAt: String?,
    val draft: Boolean?,
    val baseRef: String?,
    val headRef: String?,
    val authorLogin: String?,
    val authorUrl: String?,
    val authorAvatarUrl: String?,
    val appSeenAt: String?,
    val repoToCheckId: Long
)