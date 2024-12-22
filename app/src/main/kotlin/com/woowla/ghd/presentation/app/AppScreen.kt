package com.woowla.ghd.presentation.app

import androidx.compose.ui.graphics.vector.ImageVector
import com.woowla.compose.icon.collections.tabler.Tabler
import com.woowla.compose.icon.collections.tabler.tabler.Outline
import com.woowla.compose.icon.collections.tabler.tabler.outline.*

interface NavigationScreen {
    val icon: ImageVector
    val title: String
}
sealed class AppScreen(open val route: String) {
    data object Splash : AppScreen(route = "splash")
    data object Login : AppScreen(route = "login")
    data object Home : AppScreen(route = "home")

    data object PullRequestList : AppScreen(route = "pull-request"), NavigationScreen {
        override val icon: ImageVector = Tabler.Outline.GitPullRequest
        override val title: String = i18n.tab_title_pull_requests
    }
    data object ReleaseList : AppScreen(route = "release"), NavigationScreen {
        override val icon: ImageVector = Tabler.Outline.Package
        override val title: String = i18n.tab_title_releases
    }
    data object RepoToCheckList : AppScreen(route = "repo-to-check"), NavigationScreen {
        override val icon: ImageVector = Tabler.Outline.BrandGithub
        override val title: String = i18n.tab_title_repos_to_check
    }
    data object Notifications : AppScreen(route = "notifications"), NavigationScreen {
        override val icon: ImageVector = Tabler.Outline.Bell
        override val title: String = i18n.tab_title_notifications
    }
    data object Settings : AppScreen(route = "settings"), NavigationScreen {
        override val icon: ImageVector = Tabler.Outline.Settings
        override val title: String = i18n.tab_title_settings
    }
    data object About : AppScreen(route = "about"), NavigationScreen {
        override val icon: ImageVector = Tabler.Outline.InfoCircle
        override val title: String = i18n.tab_title_about
    }

    data object RepoToCheckNew : AppScreen(route = "repo-to-check/new")
    data object RepoToCheckEdit : AppScreen(route = "repo-to-check/{id}/edit")
    data object RepoToCheckBulkSample : AppScreen(route = "repo-to-check-bulk-sample")
    data object SyncResultList : AppScreen(route = "sync-results")
    data object SyncResult : AppScreen(route = "sync-results/{id}")
    data object ComponentsSample : AppScreen(route = "components-sample")
}