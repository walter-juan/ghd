package com.woowla.ghd.domain.entities

// TODO relations
data class SyncResultWithEntriesAndRepos(
    val syncResult: SyncResult,
    val syncResultEntries: List<SyncResultEntryWithRepo>,
) {
    val entriesSize: Int
    val errorPercentage: Int
    val status: SyncResult.Status
    init {
        entriesSize = syncResultEntries.size

        errorPercentage = if (entriesSize == 0) {
            0
        } else {
            syncResultEntries.count { !it.syncResultEntry.isSuccess } * 100 / syncResultEntries.size
        }

        status = when(errorPercentage) {
            0 -> SyncResult.Status.SUCCESS
            in 0..5 -> SyncResult.Status.WARNING
            in 5..25 -> SyncResult.Status.ERROR
            else -> SyncResult.Status.CRITICAL
        }
    }
}