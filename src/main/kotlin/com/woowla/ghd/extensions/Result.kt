package com.woowla.ghd.extensions

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