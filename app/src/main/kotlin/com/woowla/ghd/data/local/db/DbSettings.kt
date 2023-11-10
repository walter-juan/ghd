package com.woowla.ghd.data.local.db

import com.woowla.ghd.AppFolderFactory
import com.woowla.ghd.data.local.db.exceptions.toDbException
import com.woowla.ghd.extensions.mapFailure
import java.util.*
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.flywaydb.core.Flyway
import org.h2.jdbcx.JdbcDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.DatabaseConfig
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import javax.sql.DataSource

object DbSettings {
    private const val dbFolder = "db"
    private const val dbName = "ghd-v2.h2"
    private const val dbUser = "ghd-user"
    private const val dbPwd = "ghd-pwd"
    private val dbFolderPath by lazy {
        AppFolderFactory.folder.resolve(dbFolder)
    }
    private val dbPath by lazy { dbFolderPath.resolve(dbName) }
    private val mutex = Mutex()

    val syncSettingsUUID = UUID.fromString("06f16337-4ded-4296-8b51-18b23fe3c1c4")

    @Volatile private var INSTANCE: Database? = null

    val dispatcher: CoroutineDispatcher = Dispatchers.IO
    private val dbConfig = DatabaseConfig {
        keepLoadedReferencesOutOfTransaction = true
    }

    fun getDb(): Database = requireNotNull(INSTANCE) { "Not intialized database" }

    suspend fun initDb(filePassword: String?) {
        mutex.withLock {
            val dataSource = getDbDataSource(filePassword = filePassword, createIfNotExists = true)

            val flyway = Flyway
                .configure()
                .dataSource(dataSource)
                .baselineOnMigrate(true)
                .baselineVersion("1")
                .validateMigrationNaming(true)
                .load()
            flyway.migrate()

            INSTANCE = Database.connect(datasource = dataSource, databaseConfig = dbConfig)
//            newDbSuspendedTransaction {
//                SchemaUtils.create(DbPullRequestTable, DbReleaseTable, DbRepoToCheckTable, DbReviewTable, DbSyncResultTable, DbSyncResultEntryTable, DbSyncSettingsTable)
//            }
        }
    }

    /**
     * Close the database connection
     */
    suspend fun closeDb() {
        INSTANCE?.let { db -> TransactionManager.closeAndUnregister(db) }
        INSTANCE = null
    }

    /**
     * Check if the database exists
     */
    suspend fun dbExists(): Boolean {
        val dbFolderFile = dbFolderPath.toFile()
        return if (dbFolderFile.exists() && dbFolderFile.isDirectory) {
            dbFolderFile.listFiles()?.isNotEmpty() ?: false
        } else {
            false
        }
    }

    /**
     * Test the database connection
     * @param filePassword The password file to use or null to not use password
     * @param createIfNotExists If this is true the database will be created. Usually this should be used as true
     */
    suspend fun testConnection(filePassword: String? = "testConnection", createIfNotExists: Boolean = false): Result<Unit> {
        return runCatching {
            val dataSource = getDbDataSource(filePassword = filePassword, createIfNotExists = createIfNotExists)
            val isValid = dataSource.connection.use { it.isValid(2) }
        }.mapFailure {
            it.toDbException()
        }
    }

    /**
     * Delete the database and it's related files
     * @return `true` if the database files has been deleted correctly
     */
    suspend fun deleteDb(): Boolean {
        closeDb()
        return dbFolderPath.toFile().deleteRecursively()
    }

    /**
     * Create a new database connection
     * @param filePassword The password file to use or null to not use password
     * @param createIfNotExists If this is true the database will be created. Usually this should be used as true
     */
    private fun getDbDataSource(filePassword: String?, createIfNotExists: Boolean = true): DataSource {
        val encryptDatabase = filePassword != null

        val url = mutableListOf("jdbc:h2:$dbPath")
        if (encryptDatabase) {
            url.add("CIPHER=AES")
        }
        if (!createIfNotExists) {
            url.add("IFEXISTS=TRUE")
        }

        return JdbcDataSource().apply {
            setURL(url.joinToString(separator = ";"))
            if (encryptDatabase) {
                setUser(dbUser)
                if (!filePassword.isNullOrEmpty()) {
                    setPassword("$filePassword $dbPwd")
                }
            }
        }
    }
}

suspend fun <T> newDbSuspendedTransaction(
    context: CoroutineContext? = DbSettings.dispatcher,
    db: Database? = DbSettings.getDb(),
    transactionIsolation: Int? = null,
    statement: suspend Transaction.() -> T
): T = newSuspendedTransaction(context, db, transactionIsolation, statement)