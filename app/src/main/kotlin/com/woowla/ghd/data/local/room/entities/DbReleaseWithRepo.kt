package com.woowla.ghd.data.local.room.entities

import com.woowla.ghd.domain.entities.ReleaseWithRepo
import io.mcarle.konvert.api.KonvertFrom
import io.mcarle.konvert.api.KonvertTo

// TODO relations
@KonvertFrom(ReleaseWithRepo::class)
@KonvertTo(ReleaseWithRepo::class)
data class DbReleaseWithRepo(
    val release: DbRelease,
    val repoToCheck: DbRepoToCheck,
) {
    companion object
}

