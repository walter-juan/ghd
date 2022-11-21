package com.woowla.ghd.data.local.db

import com.woowla.ghd.AppFolderFactory
import com.woowla.ghd.KermitLogger
import com.woowla.ghd.data.local.db.exceptions.toDbException
import com.woowla.ghd.data.local.db.tables.DbSyncSettingsTable
import com.woowla.ghd.data.local.db.tables.DbPullRequestTable
import com.woowla.ghd.data.local.db.tables.DbReleaseTable
import com.woowla.ghd.data.local.db.tables.DbRepoToCheckTable
import com.woowla.ghd.extensions.mapFailure
import java.util.*
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.DatabaseConfig
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

object DbSettings {
    private const val dbFolder = "db"
    private const val dbName = "ghd.h2"
    private const val dbDriver = "org.h2.Driver"
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

    suspend fun initDb(filePassword: String) {
        mutex.withLock {
            INSTANCE = Database.connect(url = getDbUrl(), driver = dbDriver, user = dbUser, password = getDbPassword(filePassword), databaseConfig = dbConfig)
            newDbSuspendedTransaction {
                SchemaUtils.create (DbSyncSettingsTable, DbPullRequestTable, DbPullRequestTable, DbReleaseTable, DbRepoToCheckTable)
            }
        }
    }

    /**
     * Test the database connection
     * @param filePassword The password file to use.
     * @param ifExists If this is false a database will be created. Usually this should be used as false
     */
    suspend fun testConnection(filePassword: String = "testConnection", ifExists: Boolean = true): Result<Unit> {
        return runCatching {
            Database.connect(url = getDbUrl(ifExists = ifExists), driver = dbDriver, user = dbUser, password = getDbPassword(filePassword))
        }.mapCatching { db ->
            newDbSuspendedTransaction(db = db) {
                val version = exec("SELECT SETTING_VALUE FROM INFORMATION_SCHEMA.SETTINGS WHERE SETTING_NAME = 'info.VERSION';") { it.next(); it.getString(1) }
                KermitLogger.d("The database versions is [$version]")
            }
            TransactionManager.closeAndUnregister(db)
        }.mapFailure {
            it.toDbException()
        }
    }

    /**
     * Delete the database and it's related files
     * @return `true` if the database files has been deleted correctly
     */
    suspend fun deleteDb(): Boolean {
        INSTANCE?.let { db -> TransactionManager.closeAndUnregister(db) }
        INSTANCE = null
        return dbFolderPath.toFile().deleteRecursively()
    }

    private fun getDbPassword(filePassword: String): String {
        // check if is empty because the "$filePassword $dbPwd" is correct and won't throw a invalid password error
        return if (filePassword.isEmpty()) {
            dbPwd
        } else {
            "$filePassword $dbPwd"
        }
    }

    private fun getDbUrl(ifExists: Boolean = false): String {
        val url = mutableListOf("jdbc:h2:$dbPath", "CIPHER=AES")
        if (ifExists) {
            url.add("IFEXISTS=TRUE")
        }
        return url.joinToString(separator = ";")
    }
}

suspend fun <T> newDbSuspendedTransaction(
    context: CoroutineContext? = DbSettings.dispatcher,
    db: Database? = DbSettings.getDb(),
    transactionIsolation: Int? = null,
    statement: suspend Transaction.() -> T
): T = newSuspendedTransaction(context, db, transactionIsolation, statement)