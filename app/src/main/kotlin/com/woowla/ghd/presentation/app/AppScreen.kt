package com.woowla.ghd.presentation.app

import com.woowla.compose.tabler.OutlineBrandGithub
import com.woowla.compose.tabler.OutlineGitPullRequest
import com.woowla.compose.tabler.OutlineInfoCircle
import com.woowla.compose.tabler.OutlinePackage
import com.woowla.compose.tabler.OutlineSettings
import com.woowla.compose.tabler.TablerIconsRes

interface NavigationScreen {
    val icon: String
    val title: String
}
sealed class AppScreen(open val route: String) {
    data object Splash : AppScreen(route = "splash")
    data object Login : AppScreen(route = "login")
    data object Home : AppScreen(route = "home")

    data object PullRequestList : AppScreen(route = "pull-request"), NavigationScreen {
        override val icon: String = TablerIconsRes.OutlineGitPullRequest
        override val title: String = i18n.tab_title_pull_requests
    }
    data object ReleaseList : AppScreen(route = "release"), NavigationScreen {
        override val icon: String = TablerIconsRes.OutlinePackage
        override val title: String = i18n.tab_title_releases
    }
    data object RepoToCheckList : AppScreen(route = "repo-to-check"), NavigationScreen {
        override val icon: String = TablerIconsRes.OutlineBrandGithub
        override val title: String = i18n.tab_title_repos_to_check
    }
    data object Settings : AppScreen(route = "settings"), NavigationScreen {
        override val icon: String = TablerIconsRes.OutlineSettings
        override val title: String = i18n.tab_title_settings
    }
    data object About : AppScreen(route = "about"), NavigationScreen {
        override val icon: String = TablerIconsRes.OutlineInfoCircle
        override val title: String = i18n.tab_title_about
    }

    data object RepoToCheckNew : AppScreen(route = "repo-to-check/new")
    data object RepoToCheckEdit : AppScreen(route = "repo-to-check/{id}/edit")
    data object RepoToCheckBulkSample : AppScreen(route = "repo-to-check-bulk-sample")
    data object SyncResultList : AppScreen(route = "sync-results")
    data object SyncResult : AppScreen(route = "sync-results/{id}")
    data object ComponentsSample : AppScreen(route = "components-sample")
}