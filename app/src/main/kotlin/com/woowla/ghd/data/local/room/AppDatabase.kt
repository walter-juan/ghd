package com.woowla.ghd.data.local.room

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.woowla.ghd.AppFolderFactory
import com.woowla.ghd.data.local.room.converters.Converters
import com.woowla.ghd.data.local.room.daos.DatabaseDao
import com.woowla.ghd.data.local.room.daos.PullRequestDao
import com.woowla.ghd.data.local.room.daos.ReleaseDao
import com.woowla.ghd.data.local.room.daos.RepoToCheckDao
import com.woowla.ghd.data.local.room.daos.ReviewDao
import com.woowla.ghd.data.local.room.daos.SyncResultDao
import com.woowla.ghd.data.local.room.daos.SyncResultEntryDao
import com.woowla.ghd.data.local.room.daos.SyncSettingsDao
import com.woowla.ghd.domain.entities.PullRequest
import com.woowla.ghd.domain.entities.Release
import com.woowla.ghd.domain.entities.RepoToCheck
import com.woowla.ghd.domain.entities.Review
import com.woowla.ghd.domain.entities.SyncResult
import com.woowla.ghd.domain.entities.SyncResultEntry
import com.woowla.ghd.domain.entities.SyncSettings
import kotlinx.coroutines.Dispatchers
import kotlin.concurrent.Volatile

@Database(
    version = 2,
    entities = [
        PullRequest::class,
        Release::class,
        RepoToCheck::class,
        Review::class,
        SyncResult::class,
        SyncResultEntry::class,
        SyncSettings::class,
    ],
    autoMigrations = [
        AutoMigration(from = 1, to = 2)
    ],
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                getRoomDatabase().also { INSTANCE = it }
            }
        }

        private fun getRoomDatabase(): AppDatabase {
            val dbFile = AppFolderFactory.folder.resolve("room-db").resolve("ghd.db")
            return Room.databaseBuilder<AppDatabase>(name = dbFile.toString())
                .setDriver(BundledSQLiteDriver())
                .setQueryCoroutineContext(Dispatchers.IO)
                .build()
        }
    }

    abstract fun databaseDao(): DatabaseDao
    abstract fun pullRequestDao(): PullRequestDao
    abstract fun releaseDao(): ReleaseDao
    abstract fun repoToCheckDao(): RepoToCheckDao
    abstract fun reviewDao(): ReviewDao
    abstract fun syncResultDao(): SyncResultDao
    abstract fun syncResultEntryDao(): SyncResultEntryDao
    abstract fun syncSettingsDao(): SyncSettingsDao
}