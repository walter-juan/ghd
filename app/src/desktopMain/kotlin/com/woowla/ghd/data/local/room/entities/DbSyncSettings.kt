package com.woowla.ghd.data.local.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import arrow.optics.optics
import com.woowla.ghd.domain.entities.SyncSettings
import io.mcarle.konvert.api.KonvertFrom
import io.mcarle.konvert.api.KonvertTo

@Entity(tableName = "sync_setting")
@optics
@KonvertFrom(SyncSettings::class)
@KonvertTo(SyncSettings::class)
data class DbSyncSettings(
    @PrimaryKey val id: String = ID,
    @ColumnInfo(name = "github_pat_token") val githubPatToken: String,
    @ColumnInfo(name = "check_timeout") val checkTimeout: Long?,
    @ColumnInfo(name = "pull_request_clean_up_timeout") val pullRequestCleanUpTimeout: Long?,
) {
    companion object {
        const val ID = "06f16337-4ded-4296-8b51-18b23fe3c1c4"
    }
}
