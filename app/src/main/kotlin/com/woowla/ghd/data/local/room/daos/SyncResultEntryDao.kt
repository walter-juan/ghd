package com.woowla.ghd.data.local.room.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.woowla.ghd.data.local.room.entities.DbPullRequest
import com.woowla.ghd.data.local.room.entities.DbRepoToCheck
import com.woowla.ghd.data.local.room.entities.DbReview
import com.woowla.ghd.data.local.room.entities.DbSyncResultEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface SyncResultEntryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(dbSyncResultEntry: DbSyncResultEntry)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(dbSyncResultEntryList: List<DbSyncResultEntry>)

    // TODO relations
//    @Transaction
//    @Query("SELECT * FROM sync_result_entry WHERE sync_result_id = :syncResultId")
//    suspend fun getBySyncResultWithRepos(syncResultId: Long): List<DbSyncResultEntryWithRepoToCheck>

    @Transaction
    @Query("SELECT * FROM sync_result_entry WHERE sync_result_id = :syncResultId")
    suspend fun getBySyncResult(syncResultId: Long): List<DbSyncResultEntry>

    @Transaction
    @Query("SELECT * FROM sync_result_entry")
    suspend fun getAll(): List<DbSyncResultEntry>

    @Transaction
    @Query("SELECT * FROM sync_result_entry")
    fun getAllAsFlow(): Flow<List<DbSyncResultEntry>>

    @Query("SELECT * FROM sync_result_entry WHERE id = :id")
    suspend fun get(id: Long): DbSyncResultEntry

    @Query("SELECT * FROM sync_result_entry WHERE id = :id")
    fun getAsFlow(id: Long): Flow<DbSyncResultEntry>

    @Transaction
    @Query("SELECT * FROM sync_result_entry WHERE id IN (:ids)")
    suspend fun get(ids: List<Long>): List<DbSyncResultEntry>

    @Transaction
    @Query("SELECT * FROM sync_result_entry WHERE id IN (:ids)")
    fun getAsFlow(ids: List<Long>): Flow<List<DbSyncResultEntry>>

    @Query("DELETE FROM sync_result_entry WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM sync_result_entry")
    suspend fun deleteAll()
}