package com.woowla.ghd.domain.entities

data class ReviewRequest(
    val id: String,
    val pullRequestId: String,
    val author: Author?,
) : Comparable<ReviewRequest> {
    companion object {
        val defaultComparator = compareByDescending<ReviewRequest> { it.id }
    }

    override fun compareTo(other: ReviewRequest): Int {
        return defaultComparator.compare(this, other)
    }
}