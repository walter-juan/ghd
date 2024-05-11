package com.woowla.ghd.data.local.room.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.woowla.ghd.data.local.room.entities.DbSyncResult
import kotlinx.coroutines.flow.Flow

@Dao
interface SyncResultDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(dbSyncResult: DbSyncResult): Long

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(dbSyncResultList: List<DbSyncResult>)

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
    @Query("SELECT * FROM sync_result ORDER BY id LIMIT 1")
    fun getLastAsFlow(): Flow<DbSyncResult>

    @Transaction
    @Query("SELECT * FROM sync_result")
    suspend fun getAll(): List<DbSyncResult>

    @Transaction
    @Query("SELECT * FROM sync_result")
    fun getAllAsFlow(): Flow<List<DbSyncResult>>

    @Query("SELECT * FROM sync_result WHERE id = :id")
    suspend fun get(id: Long): DbSyncResult

    @Query("SELECT * FROM sync_result WHERE id = :id")
    fun getAsFlow(id: Long): Flow<DbSyncResult>

    @Transaction
    @Query("SELECT * FROM sync_result WHERE id IN (:ids)")
    suspend fun get(ids: List<Long>): List<DbSyncResult>

    @Transaction
    @Query("SELECT * FROM sync_result WHERE id IN (:ids)")
    fun getAsFlow(ids: List<Long>): Flow<List<DbSyncResult>>

    @Query("DELETE FROM sync_result WHERE id = :id")
    suspend fun delete(id: Long)

    @Transaction
    @Query("DELETE FROM sync_result WHERE id IN (:ids)")
    suspend fun delete(ids: List<Long>)

    @Query("DELETE FROM sync_result")
    suspend fun deleteAll()
}