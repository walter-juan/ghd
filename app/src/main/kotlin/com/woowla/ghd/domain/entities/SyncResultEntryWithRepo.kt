package com.woowla.ghd.domain.entities

data class SyncResultEntryWithRepo(
    val syncResultEntry: SyncResultEntry,
    val repoToCheck: RepoToCheck?,
) : Comparable<SyncResultEntryWithRepo> {
    override fun compareTo(other: SyncResultEntryWithRepo): Int {
        return this.syncResultEntry.compareTo(other.syncResultEntry)
    }
}