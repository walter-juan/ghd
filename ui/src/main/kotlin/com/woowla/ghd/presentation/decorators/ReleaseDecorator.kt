package com.woowla.ghd.presentation.decorators

import com.woowla.ghd.domain.entities.ReleaseWithRepo
import com.woowla.ghd.extensions.toRelativeString
import com.woowla.ghd.i18nUi

class ReleaseDecorator(releaseWithRepo: ReleaseWithRepo) {
    val publishedAt: String = releaseWithRepo.release.publishedAt?.toRelativeString() ?: i18nUi.generic_unknown
    val name: String = releaseWithRepo.release.name ?: i18nUi.generic_unknown
    val fullRepo: String = "${releaseWithRepo.repoToCheck.owner}/${releaseWithRepo.repoToCheck.name}"
}