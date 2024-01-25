package com.woowla.ghd.domain.entities

import kotlinx.datetime.Instant

data class Release(
    val id: String,
    val name: String?,
    val tagName: String,
    val url: String,
    val publishedAt: Instant?,
    val authorLogin: String?,
    val authorUrl: String?,
    val authorAvatarUrl: String?,
    val repoToCheck: RepoToCheck
): Comparable<Release> {
    companion object {
        val defaultComparator = compareByDescending<Release> { it.publishedAt }
    }

    override fun compareTo(other: Release): Int {
        return defaultComparator.compare(this, other)
    }
}

/**
 * Return a list containing only the elements valid to store/show
 */
fun List<Release>.filterSyncValid(): List<Release> {
    return this.filter { release -> release.isSyncValid() }
}

/**
 * Return a list containing only the elements which are not valid to store/show.
 */
fun List<Release>.filterNotSyncValid(): List<Release> {
    return this.filterNot { release -> release.isSyncValid() }
}

fun Release.isSyncValid(): Boolean {
    val releaseEnabled = this.repoToCheck.areReleasesEnabled
    return releaseEnabled
}
