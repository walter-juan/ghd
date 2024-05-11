package com.woowla.ghd.data.local.room.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.woowla.ghd.data.local.room.entities.DbReview
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(dbReview: DbReview)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(dbReviewList: List<DbReview>)

    @Transaction
    @Query("SELECT * FROM review")
    suspend fun getAll(): List<DbReview>

    @Transaction
    @Query("SELECT * FROM review")
    fun getAllAsFlow(): Flow<List<DbReview>>

    @Query("SELECT * FROM review WHERE id = :id")
    suspend fun get(id: String): DbReview

    @Query("SELECT * FROM review WHERE id = :id")
    fun getAsFlow(id: String): Flow<DbReview>

    @Transaction
    @Query("SELECT * FROM review WHERE id IN (:ids)")
    suspend fun get(ids: List<String>): List<DbReview>

    @Transaction
    @Query("SELECT * FROM review WHERE pull_request_id = :pullRequestId")
    suspend fun getByPullRequest(pullRequestId: String): List<DbReview>

    @Transaction
    @Query("SELECT * FROM review WHERE id IN (:ids)")
    fun getAsFlow(ids: List<String>): Flow<List<DbReview>>

    @Query("DELETE FROM review WHERE id = :id")
    suspend fun delete(id: String)

    @Query("DELETE FROM review WHERE pull_request_id IN (:pullRequestIds)")
    suspend fun deleteByPullRequest(pullRequestIds: List<String>)

    @Query("DELETE FROM review")
    suspend fun deleteAll()
}