package com.woowla.ghd.domain.entities

import kotlinx.datetime.Instant

data class Review(
    val id: String,
    val url: String,
    val submittedAt: Instant?,
    val state: ReviewState,
    val authorLogin: String?,
    val authorUrl: String?,
    val authorAvatarUrl: String?
): Comparable<Review> {
    companion object {
        val defaultComparator = compareByDescending<Review> { it.submittedAt }
    }

    override fun compareTo(other: Review): Int {
        return defaultComparator.compare(this, other)
    }
}