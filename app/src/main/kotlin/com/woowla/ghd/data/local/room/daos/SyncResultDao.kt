package com.woowla.ghd.data.local.room.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.woowla.ghd.data.local.room.entities.DbSyncResult

@Dao
interface SyncResultDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(dbSyncResult: DbSyncResult): Long

    // TODO relations
//    @Transaction
//    @Query("SELECT * FROM sync_result")
//    suspend fun getAllWithEntriesAndRepos(): List<DbSyncResultWithEntriesAndRepos>
//
//    @Transaction
//    @Query("SELECT * FROM sync_result WHERE id = :id")
//    suspend fun getWithEntriesAndRepos(id: Long): DbSyncResultWithEntriesAndRepos
//
//    @Transaction
//    @Query("SELECT * FROM sync_result ORDER BY id LIMIT 1")
//    suspend fun getLastWithEntriesAndRepos(): DbSyncResultWithEntriesAndRepos

    @Transaction
    @Query("SELECT * FROM sync_result ORDER BY id DESC LIMIT 1")
    suspend fun getLast(): DbSyncResult

    @Transaction
    @Query("SELECT * FROM sync_result")
    suspend fun getAll(): List<DbSyncResult>

    @Query("SELECT * FROM sync_result WHERE id = :id")
    suspend fun get(id: Long): DbSyncResult

    @Transaction
    @Query("DELETE FROM sync_result WHERE id IN (:ids)")
    suspend fun delete(ids: List<Long>)
}