package com.woowla.ghd.data.local.room.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.woowla.ghd.data.local.room.entities.DbDeployment

@Dao
interface DeploymentDao {
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(dbDeploymentList: List<DbDeployment>)

    @Transaction
    @Query("SELECT * FROM deployment")
    suspend fun getAll(): List<DbDeployment>

    @Query("DELETE FROM deployment WHERE id IN (:ids)")
    suspend fun delete(ids: List<String>)

    @Query("DELETE FROM deployment WHERE repo_to_check_id = :repoToCheckId")
    suspend fun deleteByRepoToCheck(repoToCheckId: Long)
}