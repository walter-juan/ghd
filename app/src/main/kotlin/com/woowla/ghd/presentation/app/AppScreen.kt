package com.woowla.ghd.presentation.app

import com.woowla.compose.remixicon.BusinessArchiveLine
import com.woowla.compose.remixicon.DevelopmentGitPullRequestLine
import com.woowla.compose.remixicon.DevelopmentGitRepositoryLine
import com.woowla.compose.remixicon.RemixiconRes
import com.woowla.compose.remixicon.SystemInformationLine
import com.woowla.compose.remixicon.SystemSettings3Line

interface NavigationScreen {
    val icon: String
    val title: String
}
sealed class AppScreen(open val route: String) {
    data object Splash : AppScreen(route = "splash")
    data object Login : AppScreen(route = "login")
    data object Home : AppScreen(route = "home")

    data object PullRequestList : AppScreen(route = "pull-request"), NavigationScreen {
        override val icon: String = RemixiconRes.DevelopmentGitPullRequestLine
        override val title: String = i18n.tab_title_pull_requests
    }
    data object ReleaseList : AppScreen(route = "release"), NavigationScreen {
        override val icon: String = RemixiconRes.BusinessArchiveLine
        override val title: String = i18n.tab_title_releases
    }
    data object RepoToCheckList : AppScreen(route = "repo-to-check"), NavigationScreen {
        override val icon: String = RemixiconRes.DevelopmentGitRepositoryLine
        override val title: String = i18n.tab_title_repos_to_check
    }
    data object Settings : AppScreen(route = "settings"), NavigationScreen {
        override val icon: String = RemixiconRes.SystemSettings3Line
        override val title: String = i18n.tab_title_settings
    }
    data object About : AppScreen(route = "about"), NavigationScreen {
        override val icon: String = RemixiconRes.SystemInformationLine
        override val title: String = i18n.tab_title_about
    }

    data object RepoToCheckNew : AppScreen(route = "repo-to-check/new")
    data object RepoToCheckEdit : AppScreen(route = "repo-to-check/{id}/edit")
    data object RepoToCheckBulkSample : AppScreen(route = "repo-to-check-bulk-sample")
    data object SyncResultList : AppScreen(route = "sync-results")
    data object SyncResult : AppScreen(route = "sync-results/{id}")
    data object ComponentsSample : AppScreen(route = "components-sample")
}