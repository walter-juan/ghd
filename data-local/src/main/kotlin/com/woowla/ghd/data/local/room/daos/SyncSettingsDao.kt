package com.woowla.ghd.data.local.room.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.woowla.ghd.data.local.room.entities.DbSyncSettings

@Dao
interface SyncSettingsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(dbSyncSettings: DbSyncSettings)

    @Transaction
    @Query("SELECT * FROM sync_setting WHERE id = :id")
    suspend fun get(id: String = DbSyncSettings.ID): DbSyncSettings?
}