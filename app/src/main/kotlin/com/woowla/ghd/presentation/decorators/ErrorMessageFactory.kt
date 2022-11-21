package com.woowla.ghd.presentation.decorators

import com.woowla.ghd.data.local.db.exceptions.DbDatabaseAlreadyOpenException
import com.woowla.ghd.data.local.db.exceptions.DbDatabaseNotFoundException
import com.woowla.ghd.data.local.db.exceptions.DbException
import com.woowla.ghd.data.local.db.exceptions.DbUnknownException
import com.woowla.ghd.data.local.db.exceptions.DbWrongEncryptionPasswordOrAlgorithmException
import com.woowla.ghd.data.local.db.exceptions.DbWrongPasswordFormatException
import com.woowla.ghd.data.local.db.exceptions.DbWrongUserOrPasswordException
import com.woowla.ghd.presentation.app.i18n

object ErrorMessageFactory {
    fun create(th: Throwable): String {
        return when(th) {
            is DbException -> {
                when(th) {
                    is DbWrongPasswordFormatException -> i18n.error_db_wrong_password_format
                    is DbWrongUserOrPasswordException -> i18n.error_db_wrong_user_or_password
                    is DbWrongEncryptionPasswordOrAlgorithmException -> i18n.error_db_wrong_encryption_password
                    is DbDatabaseNotFoundException -> i18n.error_db_database_not_foung
                    is DbDatabaseAlreadyOpenException -> i18n.error_db_database_already_open
                    is DbUnknownException -> i18n.error_db_unknown
                    else -> i18n.error_unknown
                }
            }
            else -> i18n.error_unknown
        }
    }
}
