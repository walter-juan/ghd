package com.woowla.ghd.presentation.app

import com.woowla.ghd.BuildConfig

internal object i18nApp {
    val app_name = if (BuildConfig.DEBUG) {
        "DEBUG // GHD - GitHub Dashboard"
    } else {
        "GHD - GitHub Dashboard"
    }

    val menu_bar_menu_actions = "Actions"
    val menu_bar_menu_item_synchronize = "Synchronize"

    val tray_tooltip = app_name
    val tray_item_synchronize = "Synchronize"
    val tray_item_show_app = "Show"
    val tray_item_hide_app = "Hide in tray"
    val tray_item_exit = "Exit"

    val tab_title_pull_requests = "Pulls"
    val tab_title_releases = "Releases"
    val tab_title_repos_to_check = "Repos"
    val tab_title_notifications = "Notifications"
    val tab_title_settings = "Settings"
    val tab_title_about = "About"
    val tab_title_about_libraries = "About libraries"

    val screen_edit_repo_to_check_pull_request_section = "Sync pull requests"

    val dialog_new_app_version_title = "Update available"
    val dialog_new_app_version_ignore_button = "Ignore"
    val dialog_new_app_version_update_button = "Update now"
    val dialog_new_app_version_text: (String) -> String = { "A new version of GHD ($it) is available for download! Would you like to update it now?" }
    val dialog_new_app_version_current_version = "Current version: ${BuildConfig.APP_VERSION}"
    val dialog_new_app_version_latest_version: (String) -> String = { "Latest version: $it" }
}
