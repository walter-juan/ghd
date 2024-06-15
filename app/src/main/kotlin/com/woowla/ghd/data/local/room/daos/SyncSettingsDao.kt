package com.woowla.ghd.data.local.room.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.woowla.ghd.domain.entities.SyncSettings

@Dao
interface SyncSettingsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(dbSyncSettings: SyncSettings)

    @Transaction
    @Query("SELECT * FROM sync_setting WHERE id = :id")
    suspend fun get(id: String = SyncSettings.ID): SyncSettings?
}