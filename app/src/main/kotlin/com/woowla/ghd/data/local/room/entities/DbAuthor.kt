package com.woowla.ghd.data.local.room.entities

import androidx.room.ColumnInfo
import com.woowla.ghd.domain.entities.Author
import io.mcarle.konvert.api.KonvertFrom
import io.mcarle.konvert.api.KonvertTo

@KonvertFrom(Author::class)
@KonvertTo(Author::class)
data class DbAuthor(
    @ColumnInfo(name = "author_login") val login: String?,
    @ColumnInfo(name = "author_url") val url: String?,
    @ColumnInfo(name = "author_avatar_url") val avatarUrl: String?,
) {
    companion object
}
