package com.woowla.ghd.data.local.room.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.woowla.ghd.data.local.room.entities.DbPullRequest
import kotlinx.datetime.Instant

@Dao
interface PullRequestDao {
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(dbPullRequestList: List<DbPullRequest>)

    @Query("UPDATE pull_request SET app_seen_at = :appSeenAt WHERE id = :id")
    suspend fun updateSeenAt(id: String, appSeenAt: Instant?)

    @Transaction
    @Query("SELECT * FROM pull_request")
    suspend fun getAll(): List<DbPullRequest>

    @Query("SELECT * FROM pull_request WHERE id = :id")
    suspend fun get(id: String): DbPullRequest

    @Query("DELETE FROM pull_request WHERE id IN (:ids)")
    suspend fun delete(ids: List<String>)
}