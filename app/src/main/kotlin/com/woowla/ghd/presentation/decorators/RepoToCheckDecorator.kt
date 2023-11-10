package com.woowla.ghd.presentation.decorators

import com.woowla.ghd.domain.entities.RepoToCheck

class RepoToCheckDecorator(val repoToCheck: RepoToCheck) {
    val fullRepo = "${repoToCheck.owner}/${repoToCheck.name}"

    val enabledFeatures = buildString {
        append("${repoToCheck.arePullRequestsEnabled.toCheck()} Pulls ")
        append("${repoToCheck.areReleasesEnabled.toCheck()} Releases")
    }

    private fun Boolean.toCheck(): String = if (this) { "☑" } else { "☐" }
}