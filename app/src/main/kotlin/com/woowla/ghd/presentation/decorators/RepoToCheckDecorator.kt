package com.woowla.ghd.presentation.decorators

import com.woowla.compose.icon.collections.tabler.Tabler
import com.woowla.compose.icon.collections.tabler.tabler.Filled
import com.woowla.compose.icon.collections.tabler.tabler.Outline
import com.woowla.compose.icon.collections.tabler.tabler.filled.Bell
import com.woowla.compose.icon.collections.tabler.tabler.outline.Refresh
import com.woowla.compose.icon.collections.tabler.tabler.outline.X
import com.woowla.ghd.domain.entities.RepoToCheck

class RepoToCheckDecorator(val repoToCheck: RepoToCheck) {
    val fullRepo = "${repoToCheck.gitHubRepository?.owner}/${repoToCheck.gitHubRepository?.name}"


    val pullRequestsSyncIcon = when {
        repoToCheck.arePullRequestsEnabled && repoToCheck.arePullRequestsNotificationsEnabled -> Tabler.Filled.Bell
        repoToCheck.arePullRequestsEnabled -> Tabler.Outline.Refresh
        else -> Tabler.Outline.X
    }

    val releasesSyncIcon = when {
        repoToCheck.areReleasesEnabled && repoToCheck.areReleasesNotificationsEnabled -> Tabler.Filled.Bell
        repoToCheck.areReleasesEnabled -> Tabler.Outline.Refresh
        else -> Tabler.Outline.X
    }
}