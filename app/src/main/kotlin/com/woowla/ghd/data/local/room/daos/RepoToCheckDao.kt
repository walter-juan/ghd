package com.woowla.ghd.data.local.room.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.woowla.ghd.data.local.room.entities.DbRepoToCheck

@Dao
interface RepoToCheckDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(dbRepoToCheck: DbRepoToCheck)

    @Transaction
    @Query("SELECT * FROM repo_to_check")
    suspend fun getAll(): List<DbRepoToCheck>

    @Query("SELECT * FROM repo_to_check WHERE id = :id")
    suspend fun get(id: Long): DbRepoToCheck

    @Query("DELETE FROM repo_to_check WHERE id = :id")
    suspend fun delete(id: Long)
}