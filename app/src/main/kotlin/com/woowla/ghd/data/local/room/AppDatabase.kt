package com.woowla.ghd.data.local.room

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
import com.woowla.ghd.data.local.room.entities.DbPullRequest
import com.woowla.ghd.data.local.room.entities.DbRelease
import com.woowla.ghd.data.local.room.entities.DbRepoToCheck
import com.woowla.ghd.data.local.room.entities.DbReview
import com.woowla.ghd.data.local.room.entities.DbSyncResult
import com.woowla.ghd.data.local.room.entities.DbSyncResultEntry
import com.woowla.ghd.data.local.room.entities.DbSyncSettings
import kotlinx.coroutines.Dispatchers
import kotlin.concurrent.Volatile

@Database(
    version = 1,
    entities = [
        DbPullRequest::class,
        DbRelease::class,
        DbRepoToCheck::class,
        DbReview::class,
        DbSyncResult::class,
        DbSyncResultEntry::class,
        DbSyncSettings::class,
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