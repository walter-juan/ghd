package com.woowla.ghd.data.local.room.entities

import com.woowla.ghd.domain.entities.DeploymentWithRepo
import com.woowla.ghd.domain.entities.ReleaseWithRepo
import io.mcarle.konvert.api.KonvertFrom
import io.mcarle.konvert.api.KonvertTo

// TODO relations
@KonvertFrom(DeploymentWithRepo::class)
@KonvertTo(DeploymentWithRepo::class)
data class DbDeploymentWithRepo(
    val deployment: DbDeployment,
    val repoToCheck: DbRepoToCheck,
) {
    companion object
}

