package com.woowla.ghd.data.local.db.exceptions

import org.h2.api.ErrorCode
import java.sql.SQLException

abstract class DbException(message: String? = null, cause: Throwable? = null) : Exception(message, cause)
class DbWrongPasswordFormatException(message: String? = null, cause: Throwable? = null) : DbException(message, cause)
class DbWrongUserOrPasswordException(message: String? = null, cause: Throwable? = null) : DbException(message, cause)
class DbWrongEncryptionPasswordOrAlgorithmException(message: String? = null, cause: Throwable? = null) : DbException(message, cause)
class DbDatabaseNotFoundException(message: String? = null, cause: Throwable? = null) : DbException(message, cause)
class DbDatabaseAlreadyOpenException(message: String? = null, cause: Throwable? = null) : DbException(message, cause)
class DbUnknownException(message: String? = null, cause: Throwable? = null) : DbException(message, cause)

fun Throwable.toDbException(): DbException {
    return when(this) {
        is SQLException -> toDbException()
        else -> DbUnknownException()
    }
}

fun SQLException.toDbException(): DbException {
    return when(errorCode) {
        ErrorCode.WRONG_PASSWORD_FORMAT -> DbWrongPasswordFormatException()
        ErrorCode.WRONG_USER_OR_PASSWORD -> DbWrongUserOrPasswordException()
        ErrorCode.FILE_ENCRYPTION_ERROR_1 -> DbWrongEncryptionPasswordOrAlgorithmException()
        ErrorCode.DATABASE_NOT_FOUND_WITH_IF_EXISTS_1 -> DbDatabaseNotFoundException()
        ErrorCode.DATABASE_ALREADY_OPEN_1 -> DbDatabaseAlreadyOpenException()
        else -> DbUnknownException()
    }
}