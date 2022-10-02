package com.woowla.ghd.domain.mappers

import kotlinx.datetime.Instant
import kotlinx.datetime.toInstant

class InstantMapper {
    fun stringToInstant(value: String?): Instant? {
        return value?.toInstant()
    }
    fun instantToString(value: Instant?): String? {
        return value?.toString()
    }
}