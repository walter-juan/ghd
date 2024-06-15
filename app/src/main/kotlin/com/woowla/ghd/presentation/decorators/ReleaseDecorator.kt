package com.woowla.ghd.presentation.decorators

import com.woowla.ghd.domain.entities.ReleaseWithRepo
import com.woowla.ghd.presentation.app.i18n

class ReleaseDecorator(releaseWithRepo: ReleaseWithRepo) {
    val publishedAt: String = releaseWithRepo.release.publishedAt?.let { i18n.release_published(it) } ?: i18n.generic_unknown
    val authorLogin: String = releaseWithRepo.release.author?.login ?: i18n.generic_unknown
    val name: String = releaseWithRepo.release.name ?: i18n.generic_unknown
    val fullRepo: String = "${releaseWithRepo.repoToCheck.owner}/${releaseWithRepo.repoToCheck.name}"
}