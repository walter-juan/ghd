package com.woowla.ghd.data.local.room.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.woowla.ghd.domain.entities.PullRequestSeen

@Dao
interface PullRequestSeenDao {
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(dbPullRequestList: List<PullRequestSeen>)

    @Transaction
    @Query("SELECT * FROM pull_request_seen")
    suspend fun getAll(): List<PullRequestSeen>

    @Query("SELECT * FROM pull_request_seen WHERE id = :id")
    suspend fun get(id: String): PullRequestSeen?

    @Query("DELETE FROM pull_request_seen WHERE id IN (:ids)")
    suspend fun delete(ids: List<String>)
}