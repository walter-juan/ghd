package com.woowla.ghd.data.local.room.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.woowla.ghd.domain.entities.PullRequest
import kotlinx.datetime.Instant

@Dao
interface PullRequestDao {
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(dbPullRequestList: List<PullRequest>)

    @Transaction
    @Query("SELECT * FROM pull_request")
    suspend fun getAll(): List<PullRequest>

    @Query("SELECT * FROM pull_request WHERE id = :id")
    suspend fun get(id: String): PullRequest

    @Query("DELETE FROM pull_request WHERE id IN (:ids)")
    suspend fun delete(ids: List<String>)
}