package com.woowla.ghd.data.local.room.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.woowla.ghd.data.local.room.entities.DbReview

@Dao
interface ReviewDao {
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(dbReviewList: List<DbReview>)

    @Transaction
    @Query("SELECT * FROM review WHERE pull_request_id = :pullRequestId")
    suspend fun getByPullRequest(pullRequestId: String): List<DbReview>

    @Query("DELETE FROM review WHERE pull_request_id IN (:pullRequestIds)")
    suspend fun deleteByPullRequest(pullRequestIds: List<String>)
}