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

    val isApproved: Boolean
        get() = state == ReviewState.APPROVED

    val isCommentedOrChangesRequested: Boolean
        get() = state == ReviewState.COMMENTED || state == ReviewState.CHANGES_REQUESTED || state == ReviewState.UNKNOWN


    override fun compareTo(other: Review): Int {
        return defaultComparator.compare(this, other)
    }

    fun reRequestedReview(): Boolean {
        return state == ReviewState.DISMISSED
    }
}

fun List<Review>.removeCopilotReviews(): List<Review> {
    return filterNot { it.author?.isCopilotReviewer ?: false }
}

fun List<Review>.anyNonApproved(): Boolean {
    return any { !it.isApproved }
}

fun List<Review>.anyApproved(): Boolean {
    return any { it.isApproved }
}

fun List<Review>.anyCommentedOrChangesRequested(): Boolean {
    return any { it.isCommentedOrChangesRequested }
}