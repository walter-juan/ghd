package com.woowla.ghd.data.local.room

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DeleteColumn
import androidx.room.DeleteTable
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.AutoMigrationSpec
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.woowla.ghd.core.AppFolderFactory
import com.woowla.ghd.data.local.room.converters.Converters
import com.woowla.ghd.data.local.room.daos.DatabaseDao
import com.woowla.ghd.data.local.room.daos.PullRequestDao
import com.woowla.ghd.data.local.room.daos.ReleaseDao
import com.woowla.ghd.data.local.room.daos.RepoToCheckDao
import com.woowla.ghd.data.local.room.daos.ReviewDao
import com.woowla.ghd.data.local.room.daos.ReviewRequestDao
import com.woowla.ghd.data.local.room.daos.SyncResultDao
import com.woowla.ghd.data.local.room.daos.SyncResultEntryDao
import com.woowla.ghd.data.local.room.daos.SyncSettingsDao
import com.woowla.ghd.data.local.room.entities.DbPullRequest
import com.woowla.ghd.data.local.room.entities.DbRelease
import com.woowla.ghd.data.local.room.entities.DbRepoToCheck
import com.woowla.ghd.data.local.room.entities.DbReview
import com.woowla.ghd.data.local.room.entities.DbReviewRequest
import com.woowla.ghd.data.local.room.entities.DbSyncResult
import com.woowla.ghd.data.local.room.entities.DbSyncResultEntry
import com.woowla.ghd.data.local.room.entities.DbSyncSettings
import kotlinx.coroutines.Dispatchers

@Database(
    version = 7,
    entities = [
        DbPullRequest::class,
        DbRelease::class,
        DbRepoToCheck::class,
        DbReview::class,
        DbReviewRequest::class,
        DbSyncResult::class,
        DbSyncResultEntry::class,
        DbSyncSettings::class,
    ],
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3, spec = AppDatabase.AutoMigrationFrom2To3::class),
        AutoMigration(from = 3, to = 4, spec = AppDatabase.AutoMigrationFrom3To4::class),
        AutoMigration(from = 4, to = 5, spec = AppDatabase.AutoMigrationFrom4To5::class),
        AutoMigration(from = 5, to = 6),
        AutoMigration(from = 6, to = 7),
    ],
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    companion object {
        fun getRoomDatabase(appFolderFactory: AppFolderFactory): AppDatabase {
            val dbFile = appFolderFactory.folder.resolve("room-db").resolve("ghd.db")
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
    abstract fun reviewRequestDao(): ReviewRequestDao
    abstract fun syncResultDao(): SyncResultDao
    abstract fun syncResultEntryDao(): SyncResultEntryDao
    abstract fun syncSettingsDao(): SyncSettingsDao

    @DeleteColumn(tableName = "pull_request", columnName = "mergeable")
    @DeleteColumn(tableName = "pull_request", columnName = "app_seen_at")
    class AutoMigrationFrom2To3 : AutoMigrationSpec

    @DeleteTable(tableName = "pull_request_seen")
    @DeleteTable(tableName = "review_seen")
    class AutoMigrationFrom3To4 : AutoMigrationSpec

    class AutoMigrationFrom4To5 : AutoMigrationSpec
}