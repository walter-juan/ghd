package com.woowla.ghd.app

import androidx.compose.ui.graphics.vector.ImageVector
import com.woowla.compose.icon.collections.tabler.Tabler
import com.woowla.compose.icon.collections.tabler.tabler.Outline
import com.woowla.compose.icon.collections.tabler.tabler.outline.Bell
import com.woowla.compose.icon.collections.tabler.tabler.outline.BrandGithub
import com.woowla.compose.icon.collections.tabler.tabler.outline.CloudUpload
import com.woowla.compose.icon.collections.tabler.tabler.outline.GitPullRequest
import com.woowla.compose.icon.collections.tabler.tabler.outline.InfoCircle
import com.woowla.compose.icon.collections.tabler.tabler.outline.Package
import com.woowla.compose.icon.collections.tabler.tabler.outline.Settings
import kotlinx.serialization.Serializable

interface NavigationScreen {
    val icon: ImageVector
    val title: String
}
@Serializable
sealed class AppScreen(open val route: String = "") {
    @Serializable
    data object Splash : AppScreen()
    @Serializable
    data object Home : AppScreen()

    @Serializable
    data object PullRequestList : AppScreen(), NavigationScreen {
        override val icon: ImageVector = Tabler.Outline.GitPullRequest
        override val title: String = i18nApp.tab_title_pull_requests
    }
    @Serializable
    data object ReleaseList : AppScreen(), NavigationScreen {
        override val icon: ImageVector = Tabler.Outline.Package
        override val title: String = i18nApp.tab_title_releases
    }
    @Serializable
    data object DeploymentList : AppScreen(), NavigationScreen {
        override val icon: ImageVector = Tabler.Outline.CloudUpload
        override val title: String = i18nApp.tab_title_deployments
    }
    @Serializable
    data object RepoToCheckList : AppScreen(), NavigationScreen {
        override val icon: ImageVector = Tabler.Outline.BrandGithub
        override val title: String = i18nApp.tab_title_repos_to_check
    }
    @Serializable
    data object Notifications : AppScreen(), NavigationScreen {
        override val icon: ImageVector = Tabler.Outline.Bell
        override val title: String = i18nApp.tab_title_notifications
    }
    @Serializable
    data object Settings : AppScreen(), NavigationScreen {
        override val icon: ImageVector = Tabler.Outline.Settings
        override val title: String = i18nApp.tab_title_settings
    }
    @Serializable
    data object About : AppScreen(), NavigationScreen {
        override val icon: ImageVector = Tabler.Outline.InfoCircle
        override val title: String = i18nApp.tab_title_about
    }
    @Serializable
    data object AboutLibraries : AppScreen(), NavigationScreen {
        override val icon: ImageVector = Tabler.Outline.InfoCircle
        override val title: String = i18nApp.tab_title_about_libraries
    }

    @Serializable
    data object RepoToCheckNew : AppScreen()
    @Serializable
    data class RepoToCheckEdit(val id: Long) : AppScreen()
    @Serializable
    data object RepoToCheckBulk : AppScreen()
    @Serializable
    data object SyncResultList : AppScreen()
    @Serializable
    data class SyncResult(val id: Long) : AppScreen()
}