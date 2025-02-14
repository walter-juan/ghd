package com.woowla.ghd.data.local.room.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.woowla.ghd.data.local.room.entities.DbRelease

@Dao
interface ReleaseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(dbRelease: DbRelease)

    @Transaction
    @Query("SELECT * FROM release")
    suspend fun getAll(): List<DbRelease>

    @Query("DELETE FROM release WHERE id IN (:ids)")
    suspend fun delete(ids: List<String>)

    @Query("DELETE FROM release WHERE repo_to_check_id = :repoToCheckId")
    suspend fun deleteByRepoToCheck(repoToCheckId: Long)
}