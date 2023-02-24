package com.woowla.ghd.presentation.decorators

import com.woowla.ghd.domain.entities.Release
import com.woowla.ghd.presentation.app.i18n

class ReleaseDecoratorNew(release: Release) {
    val publishedAt: String = release.publishedAt?.let { i18n.release_published(it) } ?: i18n.generic_unknown
    val authorLogin: String = release.authorLogin ?: i18n.generic_unknown
    val name: String = release.name ?: i18n.generic_unknown
    val fullRepo: String = "${release.repoToCheck.owner}/${release.repoToCheck.name}"
}