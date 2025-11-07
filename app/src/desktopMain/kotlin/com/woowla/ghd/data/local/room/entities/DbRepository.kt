package com.woowla.ghd.data.local.room.entities

import androidx.room.ColumnInfo
import com.woowla.ghd.domain.entities.Repository
import io.mcarle.konvert.api.KonvertFrom
import io.mcarle.konvert.api.KonvertTo

@KonvertFrom(Repository::class)
@KonvertTo(Repository::class)
data class DbRepository(
    @ColumnInfo(name = "owner") val owner: String,
    @ColumnInfo(name = "name") val name: String,
) {
    companion object
}
