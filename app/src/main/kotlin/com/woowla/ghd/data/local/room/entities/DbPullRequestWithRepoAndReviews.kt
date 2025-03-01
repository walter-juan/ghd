package com.woowla.ghd.data.local.room.entities

import com.woowla.ghd.domain.entities.PullRequestWithRepoAndReviews
import io.mcarle.konvert.api.KonvertFrom
import io.mcarle.konvert.api.KonvertTo

// TODO relations
@KonvertFrom(PullRequestWithRepoAndReviews::class)
@KonvertTo(PullRequestWithRepoAndReviews::class)
data class DbPullRequestWithRepoAndReviews(
    val repoToCheck: DbRepoToCheck,

    val pullRequest: DbPullRequest,
    val reviews: List<DbReview>,
) {
    companion object
}