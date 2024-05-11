package com.woowla.ghd.data.local.room.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface DatabaseDao {
    @Transaction
    suspend fun resetDatabase() {
        deleteAllRepoToCheck()

        deleteAllPullRequest()
        deleteAllReview()
        deleteAllRelease()
        deleteAllSyncResult()
        deleteAllSynResultEntry()
        deleteAllSyncSetting()

        clearPrimaryKeyIndex()
    }

    @Transaction @Query("DELETE FROM sqlite_sequence") suspend fun clearPrimaryKeyIndex()

    @Transaction @Query("DELETE FROM pull_request") suspend fun deleteAllPullRequest()
    @Transaction @Query("DELETE FROM release") suspend fun deleteAllRelease()
    @Transaction @Query("DELETE FROM repo_to_check") suspend fun deleteAllRepoToCheck()
    @Transaction @Query("DELETE FROM review") suspend fun deleteAllReview()
    @Transaction @Query("DELETE FROM sync_result") suspend fun deleteAllSyncResult()
    @Transaction @Query("DELETE FROM sync_result_entry") suspend fun deleteAllSynResultEntry()
    @Transaction @Query("DELETE FROM sync_setting") suspend fun deleteAllSyncSetting()
}