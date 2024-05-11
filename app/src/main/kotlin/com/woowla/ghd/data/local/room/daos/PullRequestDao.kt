package com.woowla.ghd.data.local.room.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.woowla.ghd.data.local.room.entities.DbPullRequest
import com.woowla.ghd.data.local.room.entities.DbReview
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

@Dao
interface PullRequestDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(dbPullRequest: DbPullRequest)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(dbPullRequestList: List<DbPullRequest>)

    @Query("UPDATE pull_request SET app_seen_at = :appSeenAt WHERE id = :id")
    suspend fun updateSeenAt(id: String, appSeenAt: Instant?)

    @Transaction
    @Query("SELECT * FROM pull_request")
    suspend fun getAll(): List<DbPullRequest>

    @Transaction
    @Query("SELECT * FROM pull_request")
    fun getAllAsFlow(): Flow<List<DbPullRequest>>

    @Transaction
    @Query("SELECT * FROM pull_request JOIN review ON pull_request.id = review.pull_request_id")
    suspend fun getAllWithReviews(): Map<DbPullRequest, List<DbReview>>

    @Query("SELECT * FROM pull_request WHERE id = :id")
    suspend fun get(id: String): DbPullRequest

    @Query("SELECT * FROM pull_request WHERE id = :id")
    fun getAsFlow(id: String): Flow<DbPullRequest>

    @Transaction
    @Query("SELECT * FROM pull_request JOIN review ON pull_request.id = review.pull_request_id WHERE pull_request.id = :id")
    suspend fun getWithReviews(id: String): Map<DbPullRequest, List<DbReview>>

    @Transaction
    @Query("SELECT * FROM pull_request WHERE id IN (:ids)")
    suspend fun get(ids: List<String>): List<DbPullRequest>

    @Transaction
    @Query("SELECT * FROM pull_request WHERE id IN (:ids)")
    fun getAsFlow(ids: List<String>): Flow<List<DbPullRequest>>

    @Query("DELETE FROM pull_request WHERE id IN (:ids)")
    suspend fun delete(ids: List<String>)

    @Query("DELETE FROM pull_request WHERE id = :id")
    suspend fun delete(id: String)

    @Query("DELETE FROM pull_request")
    suspend fun deleteAll()
}