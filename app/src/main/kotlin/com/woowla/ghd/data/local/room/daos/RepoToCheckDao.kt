package com.woowla.ghd.data.local.room.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.woowla.ghd.domain.entities.RepoToCheck

@Dao
interface RepoToCheckDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(dbRepoToCheck: RepoToCheck)

    @Transaction
    @Query("SELECT * FROM repo_to_check")
    suspend fun getAll(): List<RepoToCheck>

    @Query("SELECT * FROM repo_to_check WHERE id = :id")
    suspend fun get(id: Long): RepoToCheck

    @Query("DELETE FROM repo_to_check WHERE id = :id")
    suspend fun delete(id: Long)
}