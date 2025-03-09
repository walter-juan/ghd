package com.woowla.ghd.presentation.decorators

import com.woowla.ghd.domain.entities.ReleaseWithRepo
import com.woowla.ghd.core.extensions.toRelativeString
import com.woowla.ghd.presentation.i18nUi

class ReleaseDecorator(releaseWithRepo: ReleaseWithRepo) {
    val publishedAt: String = releaseWithRepo.release.publishedAt?.toRelativeString() ?: i18nUi.generic_unknown
    val name: String = releaseWithRepo.release.name ?: i18nUi.generic_unknown
    val fullRepo = "${releaseWithRepo.repoToCheck.gitHubRepository?.owner}/${releaseWithRepo.repoToCheck.gitHubRepository?.name}"
}