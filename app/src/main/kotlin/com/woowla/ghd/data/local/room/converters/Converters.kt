package com.woowla.ghd.data.local.room.converters

import androidx.room.TypeConverter
import kotlinx.datetime.Instant

class Converters {
    @TypeConverter
    fun stringToInstant(value: String?): Instant? {
        return value?.let { Instant.parse(it) }
    }

    @TypeConverter
    fun instantToString(value: Instant?): String? {
        return value?.toString()
    }
}