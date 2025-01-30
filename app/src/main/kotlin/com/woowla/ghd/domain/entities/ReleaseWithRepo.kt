package com.woowla.ghd.domain.entities

// TODO relations
data class ReleaseWithRepo(
    val release: Release,
    val repoToCheck: RepoToCheck,
) : Comparable<ReleaseWithRepo> {
    companion object {
        val defaultComparator = compareByDescending<ReleaseWithRepo> { it.repoToCheck.groupName }.thenBy { it.release.publishedAt }
    }

    val isSyncValid: Boolean = this.repoToCheck.areReleasesEnabled

    override fun compareTo(other: ReleaseWithRepo): Int {
        return defaultComparator.compare(this, other)
    }
}

/**
 * Return a list containing only the elements valid to store/show
 */
fun List<ReleaseWithRepo>.filterSyncValid(): List<ReleaseWithRepo> {
    return this.filter { release -> release.isSyncValid }
}

/**
 * Return a list containing only the elements which are not valid to store/show.
 */
fun List<ReleaseWithRepo>.filterNotSyncValid(): List<ReleaseWithRepo> {
    return this.filterNot { release -> release.isSyncValid }
}
