package com.woowla.ghd.data.local.room.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.woowla.ghd.domain.entities.ReviewSeen

@Dao
interface ReviewSeenDao {
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(dbReviewList: List<ReviewSeen>)

    @Transaction
    @Query("SELECT * FROM review_seen WHERE pull_request_id = :pullRequestId")
    suspend fun getByPullRequest(pullRequestId: String): List<ReviewSeen>

    @Query("DELETE FROM review_seen WHERE pull_request_id IN (:pullRequestIds)")
    suspend fun deleteByPullRequest(pullRequestIds: List<String>)
}