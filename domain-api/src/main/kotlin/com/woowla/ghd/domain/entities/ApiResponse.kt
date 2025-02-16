package com.woowla.ghd.domain.entities

data class ApiResponse<T : Any>(val data: T, val rateLimit: RateLimit)
