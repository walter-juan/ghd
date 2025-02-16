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

    fun reRequestedReview(): Boolean {
        return state == ReviewState.DISMISSED
    }
}

fun List<Review>.anyNonApproved(): Boolean {
    return any { it.state != ReviewState.APPROVED }
}

fun List<Review>.anyCommentedOrChangesRequested(): Boolean {
    return any { it.state == ReviewState.COMMENTED || it.state == ReviewState.CHANGES_REQUESTED || it.state == ReviewState.UNKNOWN }
}