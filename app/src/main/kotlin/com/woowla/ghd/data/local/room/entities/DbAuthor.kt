package com.woowla.ghd.data.local.room.entities

import androidx.room.ColumnInfo

data class DbAuthor(
    @ColumnInfo(name = "author_login") val login: String?,
    @ColumnInfo(name = "author_url") val url: String?,
    @ColumnInfo(name = "author_avatar_url") val avatarUrl: String?,
)
