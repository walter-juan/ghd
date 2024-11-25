package com.woowla.ghd.domain.entities

// TODO relations
data class PullRequestWithRepoAndReviews(
    val repoToCheck: RepoToCheck,

    val pullRequest: PullRequest,
    val reviews: List<Review>,

    val pullRequestSeen: PullRequestSeen?,
    val reviewsSeen: List<ReviewSeen>,
): Comparable<PullRequestWithRepoAndReviews> {
    companion object {
        val defaultComparator = compareBy<PullRequestWithRepoAndReviews> { it.pullRequest.stateExtended }.thenBy { it.seen }.thenByDescending { it.pullRequest.createdAt }
    }

    override fun compareTo(other: PullRequestWithRepoAndReviews): Int {
        return defaultComparator.compare(this, other)
    }

    val seen = pullRequestSeen != null && pullRequestSeen.updatedAt >= pullRequest.updatedAt

    fun seenDiff(): PullRequestDiff {
        return if (pullRequestSeen == null) {
            PullRequestDiff(
                stateChanged = false,
                commentAdded = false,
                reviewsChanged = false,
                checkStatusChanged = false,
                codeChanged = false,
            )
        } else {
            val allReviewsString = reviews
                .sortedBy { review -> review.id }
                .map { review -> review.id }
            val allReviewsSeenString = reviewsSeen
                .sortedBy { review -> review.id }
                .map { review -> review.id }
            PullRequestDiff(
                stateChanged = pullRequest.state != pullRequestSeen.state,
                commentAdded = (pullRequest.totalCommentsCount ?: 0) > (pullRequestSeen.totalCommentsCount ?: 0),
                reviewsChanged = allReviewsString != allReviewsSeenString,
                checkStatusChanged = pullRequest.lastCommitCheckRollupStatus != pullRequestSeen.lastCommitCheckRollupStatus,
                codeChanged = pullRequest.lastCommitSha1 != pullRequestSeen.lastCommitSha1,
            )
        }
    }
}

/**
 * Return a list containing only the elements valid to store/show
 */
fun List<PullRequestWithRepoAndReviews>.filterSyncValid(syncSettings: SyncSettings): List<PullRequestWithRepoAndReviews> {
    return this.filter { it.isSyncValid(syncSettings) }
}

/**
 * Return a list containing only the elements which are not valid to store/show.
 */
fun List<PullRequestWithRepoAndReviews>.filterNotSyncValid(syncSettings: SyncSettings): List<PullRequestWithRepoAndReviews> {
    return this.filterNot { it.isSyncValid(syncSettings) }
}

fun PullRequestWithRepoAndReviews.isSyncValid(syncSettings: SyncSettings): Boolean {
    val cleanUpTimeout = syncSettings.validPullRequestCleanUpTimeout
    val isOld = pullRequest.isOld(cleanUpTimeout)
    val hasBranchToExclude = this.hasBranchToExclude
    val pullsEnabled = this.repoToCheck.arePullRequestsEnabled

    return !isOld && !hasBranchToExclude && pullsEnabled
}

val PullRequestWithRepoAndReviews.hasBranchToExclude: Boolean
    get() {
        val regexStr = repoToCheck.pullBranchRegex
        return if (!pullRequest.headRef.isNullOrBlank() && !regexStr.isNullOrBlank()) {
            !pullRequest.headRef.matches(regexStr.toRegex())
        } else {
            false
        }
    }