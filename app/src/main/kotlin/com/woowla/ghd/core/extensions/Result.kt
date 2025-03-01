package com.woowla.ghd.core.extensions

inline fun <V, V2> Result<V>.flatMap(transform: (V) -> Result<V2>): Result<V2> {
    return fold(
        onSuccess = {
            transform(it)
        },
        onFailure = {
            Result.failure(it)
        }
    )
}

inline fun <V : Any> Result<V>.mapFailure(transform: (Throwable) -> Throwable): Result<V> {
    val exception = exceptionOrNull()
    return if (isFailure) {
        requireNotNull(exception) { "The exception of this result shouldn't be null in this point" }
        Result.failure(transform.invoke(exception))
    } else {
        this
    }
}

inline fun <V : Any> Result<V>.mapFailureCatching(transform: (Throwable) -> Throwable): Result<V> {
    val exception = exceptionOrNull()
    return if (isFailure) {
        requireNotNull(exception) { "The exception of this result shouldn't be null in this point" }
        try {
            Result.failure(transform.invoke(exception))
        } catch (th: Throwable) {
            Result.failure(th)
        }
    } else {
        this
    }
}
