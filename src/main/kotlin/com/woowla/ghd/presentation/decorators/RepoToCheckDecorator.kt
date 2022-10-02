package com.woowla.ghd.presentation.decorators

import com.woowla.ghd.domain.entities.RepoToCheck

class RepoToCheckDecorator(val repoToCheck: RepoToCheck) {
    val fullRepo = "${repoToCheck.owner}/${repoToCheck.name}"

    val enabledNotifications = buildString {
        append("${repoToCheck.pullNotificationsEnabled.toCheck()} Pulls ")
        append("${repoToCheck.releaseNotificationsEnabled.toCheck()} Releases")
    }

    private fun Boolean.toCheck(): String = if (this) { "☑" } else { "☐" }
}