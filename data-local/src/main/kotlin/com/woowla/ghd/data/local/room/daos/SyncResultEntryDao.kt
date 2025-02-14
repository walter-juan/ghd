package com.woowla.ghd.data.local.room.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.woowla.ghd.data.local.room.entities.DbSyncResultEntry

@Dao
interface SyncResultEntryDao {
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(syncResultEntryList: List<DbSyncResultEntry>)

    // TODO relations
//    @Transaction
//    @Query("SELECT * FROM sync_result_entry WHERE sync_result_id = :syncResultId")
//    suspend fun getBySyncResultWithRepos(syncResultId: Long): List<SyncResultEntryWithRepo>

    @Transaction
    @Query("SELECT * FROM sync_result_entry WHERE sync_result_id = :syncResultId")
    suspend fun getBySyncResult(syncResultId: Long): List<DbSyncResultEntry>

    @Transaction
    @Query("SELECT * FROM sync_result_entry WHERE id IN (:ids)")
    suspend fun get(ids: List<Long>): List<DbSyncResultEntry>
}