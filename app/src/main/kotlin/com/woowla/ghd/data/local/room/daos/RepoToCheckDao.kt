package com.woowla.ghd.data.local.room.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.woowla.ghd.data.local.room.entities.DbRepoToCheck
import kotlinx.coroutines.flow.Flow

@Dao
interface RepoToCheckDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(dbRepoToCheck: DbRepoToCheck)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(dbRepoToCheckList: List<DbRepoToCheck>)

    @Transaction
    @Query("SELECT * FROM repo_to_check")
    suspend fun getAll(): List<DbRepoToCheck>

    @Transaction
    @Query("SELECT * FROM repo_to_check")
    fun getAllAsFlow(): Flow<List<DbRepoToCheck>>

    @Query("SELECT * FROM repo_to_check WHERE id = :id")
    suspend fun get(id: Long): DbRepoToCheck

    @Query("SELECT * FROM repo_to_check WHERE id = :id")
    fun getAsFlow(id: Long): Flow<DbRepoToCheck>

    @Transaction
    @Query("SELECT * FROM repo_to_check WHERE id IN (:ids)")
    suspend fun get(ids: List<Long>): List<DbRepoToCheck>

    @Transaction
    @Query("SELECT * FROM repo_to_check WHERE id IN (:ids)")
    fun getAsFlow(ids: List<Long>): Flow<List<DbRepoToCheck>>

    @Query("DELETE FROM repo_to_check WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM repo_to_check")
    suspend fun deleteAll()
}