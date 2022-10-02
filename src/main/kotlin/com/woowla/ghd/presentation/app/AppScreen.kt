package com.woowla.ghd.presentation.app

import com.woowla.ghd.domain.entities.RepoToCheck

sealed class AppScreen

sealed class AppTabScreen(val tabTitle: String, val tabIcon: String) : AppScreen() {
    companion object {
        fun values() = listOf(Pulls, Releases, Repos, Settings, About)
    }

    object Pulls: AppTabScreen(tabTitle = i18n.tab_title_pull_requests, tabIcon = AppIcons.gitPullRequest)
    object Releases: AppTabScreen(tabTitle = i18n.tab_title_releases, tabIcon = AppIcons.packages)
    object Repos: AppTabScreen(tabTitle = i18n.tab_title_repos_to_check, tabIcon = AppIcons.repository)
    object Settings: AppTabScreen(tabTitle = i18n.tab_title_settings, tabIcon = AppIcons.settings)
    object About: AppTabScreen(tabTitle = i18n.tab_title_about, tabIcon = AppIcons.infoEmpty)
}

sealed class AppFullScreen : AppScreen() {
    object ComponentsSample: AppFullScreen()
    data class RepoEdit(val repoToCheck: RepoToCheck?): AppFullScreen()
}



