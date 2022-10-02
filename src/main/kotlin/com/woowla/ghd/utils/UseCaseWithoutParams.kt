package com.woowla.ghd.utils

import com.woowla.ghd.KermitLogger

abstract class UseCaseWithoutParams<T: Any> {
    protected abstract suspend fun perform(): Result<T>

    suspend fun execute(): Result<T> {
        return try {
            perform()
        } catch (ex: Exception) {
            KermitLogger.e("UseCase", ex)
            Result.failure(ex)
        }
    }
}