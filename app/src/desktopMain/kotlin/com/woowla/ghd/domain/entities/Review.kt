package com.woowla.ghd.domain.entities

import kotlinx.datetime.Instant

data class Review(
    val id: String,
    val pullRequestId: String,
    val submittedAt: Instant?,
    val url: String,
    val state: ReviewState,
    val author: Author?,
) : Comparable<Review> {
    companion object {
        val defaultComparator = compareByDescending<Review> { it.submittedAt }
    }

    override fun compareTo(other: Review): Int {
        return defaultComparator.compare(this, other)
    }

    fun isReRequestedReview(): Boolean {
        return state == ReviewState.DISMISSED
    }
}