package com.woowla.ghd.domain.entities

import kotlinx.datetime.Instant

abstract class ReviewBase(
): Comparable<ReviewBase> {
    companion object {
        val defaultComparator = compareByDescending<ReviewBase> { it.submittedAt }
    }

    abstract val id: String
    abstract val pullRequestId: String
    abstract val submittedAt: Instant?
    abstract val url: String
    abstract val state: ReviewState
    abstract val author: Author?

    override fun compareTo(other: ReviewBase): Int {
        return defaultComparator.compare(this, other)
    }
}