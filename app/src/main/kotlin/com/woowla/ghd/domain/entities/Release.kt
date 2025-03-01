package com.woowla.ghd.domain.entities

import kotlinx.datetime.Instant

data class Release(
    val id: String,
    val repoToCheckId: Long,
    val name: String?,
    val tagName: String,
    val url: String,
    val publishedAt: Instant?,
    val author: Author?,
)
