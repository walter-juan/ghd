package com.woowla.ghd.domain.entities

data class PullRequestWithRepoAndReviews(
    val repoToCheck: RepoToCheck,
    val pullRequest: PullRequest,
    val reviews: List<Review>,
    val reviewRequests: List<ReviewRequest>,
) : Comparable<PullRequestWithRepoAndReviews> {
    companion object {
        val defaultComparator = compareBy<PullRequestWithRepoAndReviews> {
            it.pullRequest.stateExtended
        }.thenByDescending {
            when(it.pullRequest.stateExtended) {
                PullRequestStateExtended.OPEN,
                PullRequestStateExtended.DRAFT,
                PullRequestStateExtended.UNKNOWN -> {
                    it.pullRequest.createdAt
                }
                PullRequestStateExtended.CLOSED -> {
                    it.pullRequest.closedAt
                }
                PullRequestStateExtended.MERGED -> {
                    it.pullRequest.mergedAt
                }
            }
        }
    }

    override fun compareTo(other: PullRequestWithRepoAndReviews): Int {
        return defaultComparator.compare(this, other)
    }

    val hasBranchToExclude: Boolean
        get() {
            val regexStr = repoToCheck.pullBranchRegex
            return if (!pullRequest.headRef.isNullOrBlank() && !regexStr.isNullOrBlank()) {
                !pullRequest.headRef.matches(regexStr.toRegex())
            } else {
                false
            }
        }

    fun isSyncValid(syncSettings: SyncSettings): Boolean {
        val cleanUpTimeout = syncSettings.validPullRequestCleanUpTimeout
        val isOld = pullRequest.isOld(cleanUpTimeout)
        val hasBranchToExclude = this.hasBranchToExclude
        val pullsEnabled = this.repoToCheck.arePullRequestsEnabled

        return !isOld && !hasBranchToExclude && pullsEnabled
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