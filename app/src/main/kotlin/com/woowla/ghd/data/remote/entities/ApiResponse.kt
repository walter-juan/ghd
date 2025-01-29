package com.woowla.ghd.data.remote.entities

data class ApiResponse<T : Any>(val data: T, val rateLimit: ApiRateLimit)
