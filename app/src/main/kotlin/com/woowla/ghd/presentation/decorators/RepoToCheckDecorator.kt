package com.woowla.ghd.presentation.decorators

import com.woowla.ghd.domain.entities.RepoToCheck

class RepoToCheckDecorator(val repoToCheck: RepoToCheck) {
    val fullRepo = "${repoToCheck.owner}/${repoToCheck.name}"
}