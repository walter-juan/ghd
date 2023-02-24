package com.woowla.ghd.domain.entities

import kotlinx.datetime.Instant

data class Release(
    val id: String,
    val name: String?,
    val tagName: String,
    val url: String,
    val publishedAt: Instant?,
    val authorLogin: String?,
    val authorUrl: String?,
    val authorAvatarUrl: String?,
    val repoToCheckId: Long,
    val repoToCheck: RepoToCheck
): Comparable<Release> {
    companion object {
        val defaultComparator = compareByDescending<Release> { it.publishedAt }
    }

    override fun compareTo(other: Release): Int {
        return defaultComparator.compare(this, other)
    }
}
