package com.woowla.ghd.utils

import com.woowla.ghd.KermitLogger

abstract class UseCase<Params: Any, T: Any> {
    protected abstract suspend fun perform(params: Params): Result<T>

    suspend fun execute(params: Params): Result<T> {
        return try {
            perform(params)
        } catch (ex: Exception) {
            KermitLogger.e("UseCase", ex)
            Result.failure(ex)
        }
    }
}