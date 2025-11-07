package com.woowla.ghd.data.local.room.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.woowla.ghd.data.local.room.entities.DbReviewRequest

@Dao
interface ReviewRequestDao {
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(dbReviewRequestList: List<DbReviewRequest>)

    @Transaction
    @Query("SELECT * FROM review_request WHERE pull_request_id = :pullRequestId")
    suspend fun getByPullRequest(pullRequestId: String): List<DbReviewRequest>

    @Query("DELETE FROM review_request WHERE pull_request_id IN (:pullRequestIds)")
    suspend fun deleteByPullRequest(pullRequestIds: List<String>)
}