package com.woowla.ghd.data.local.room.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.woowla.ghd.data.local.room.entities.DbPullRequest
import com.woowla.ghd.data.local.room.entities.DbRelease
import com.woowla.ghd.data.local.room.entities.DbReview
import kotlinx.coroutines.flow.Flow

@Dao
interface ReleaseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(dbRelease: DbRelease)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(dbReleaseList: List<DbRelease>)

    @Transaction
    @Query("SELECT * FROM release")
    suspend fun getAll(): List<DbRelease>

    @Transaction
    @Query("SELECT * FROM release")
    fun getAllAsFlow(): Flow<List<DbRelease>>

    @Query("SELECT * FROM release WHERE id = :id")
    suspend fun get(id: String): DbRelease

    @Query("SELECT * FROM release WHERE id = :id")
    fun getAsFlow(id: String): Flow<DbRelease>

    @Transaction
    @Query("SELECT * FROM release WHERE id IN (:ids)")
    suspend fun get(ids: List<String>): List<DbRelease>

    @Transaction
    @Query("SELECT * FROM release WHERE id IN (:ids)")
    fun getAsFlow(ids: List<String>): Flow<List<DbRelease>>

    @Query("DELETE FROM release WHERE id = :id")
    suspend fun delete(id: String)

    @Query("DELETE FROM release WHERE id IN (:ids)")
    suspend fun delete(ids: List<String>)

    @Query("DELETE FROM release WHERE repo_to_check_id = :repoToCheckId")
    suspend fun deleteByRepoToCheck(repoToCheckId: Long)

    @Query("DELETE FROM release")
    suspend fun deleteAll()
}