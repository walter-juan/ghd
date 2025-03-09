package com.woowla.ghd.data.local.room.entities

import androidx.room.ColumnInfo
import com.woowla.ghd.domain.entities.GitHubRepository
import io.mcarle.konvert.api.KonvertFrom
import io.mcarle.konvert.api.KonvertTo

@KonvertFrom(GitHubRepository::class)
@KonvertTo(GitHubRepository::class)
data class DbGitHubRepository(
    @ColumnInfo(name = "owner") val owner: String,
    @ColumnInfo(name = "name") val name: String,
) {
    companion object
}
