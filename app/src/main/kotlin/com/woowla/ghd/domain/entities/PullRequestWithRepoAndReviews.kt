package com.woowla.ghd.domain.entities

// TODO relations
data class PullRequestWithRepoAndReviews(
    val pullRequest: PullRequest,
    val repoToCheck: RepoToCheck,
    val reviews: List<Review>,
): Comparable<PullRequestWithRepoAndReviews> {
    override fun compareTo(other: PullRequestWithRepoAndReviews): Int {
        return this.pullRequest.compareTo(other.pullRequest)
    }

    val canBeMergedByReviews = reviews.isNotEmpty() &&
            reviews.any { it.state == ReviewState.APPROVED } &&
            !reviews.any { it.state == ReviewState.CHANGES_REQUESTED }


    val canBeMerged = pullRequest.canBeMergedByMergeable && canBeMergedByReviews
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