package com.woowla.ghd.presentation.app

import com.woowla.ghd.BuildConfig
import com.woowla.ghd.extensions.format
import com.woowla.ghd.extensions.toHRString
import kotlinx.datetime.Instant

object i18n {
    val app_name = if (BuildConfig.DEBUG) {
        "DEBUG // GHD - GitHub Dashboard"
    } else {
        "GHD - GitHub Dashboard"
    }
    val generic_loading = "Loading"
    val generic_error = "Error"
    val generic_minutes_ago: (Long) -> String = { "$it minutes ago" }
    val generic_hours_ago: (Long) -> String = { "$it hours ago" }

    val menu_bar_menu_actions = "Actions"
    val menu_bar_menu_item_synchronize = "Synchronize"

    val tray_tooltip = app_name
    val tray_item_synchronize = "Synchronize"
    val tray_item_show_app = "Show GHD"
    val tray_item_hide_app = "Hide GHD"
    val tray_item_exit = "Exit"

    val top_bar_title_pull_requests = "Pull Requests"
    val top_bar_title_releases = "Releases"
    val top_bar_title_repos_to_check = "Repos"
    val top_bar_title_repos_to_check_edit = "Repos"
    val top_bar_title_settings = "Settings"
    val top_bar_title_about = "About"

    val status_bar_loading = ""
    val status_bar_error = "Error"
    val status_bar_synchronized_at: (Instant?) -> String = { "Synchronized at ${it?.format() ?: "unknown"}" }

    val tab_title_pull_requests = "Pulls"
    val tab_title_releases = "Releases"
    val tab_title_repos_to_check = "Repos"
    val tab_title_settings = "Settings"
    val tab_title_about = "About"

    val screen_repos_to_check_new_repositories_section = "New repositories"
    val screen_repos_to_check_add_new_repository_item = "Add new repository"
    val screen_repos_to_check_repositories_section = "Repositories"

    val screen_edit_repo_to_check_save = "Save"
    val screen_edit_repo_to_check_repository_section = "Repository"
    val screen_edit_repo_to_check_owner_label = "Owner"
    val screen_edit_repo_to_check_name_label = "Name"
    val screen_edit_repo_to_check_group_item = "Group"
    val screen_edit_repo_to_check_group_item_description = "Group name in case you want the releases grouped"
    val screen_edit_repo_to_check_group_name_label = "Group name"
    val screen_edit_repo_to_check_pull_request_section = "Pull request options"
    val screen_edit_repo_to_check_releaes_section = "Releases options"
    val screen_edit_repo_to_check_enable_notifications_item = "Enable notifications"
    val screen_edit_repo_to_check_filter_by_branch_item = "Filter by branch"
    val screen_edit_repo_to_check_filter_by_branch_item_description = "Add a regex if you want to show only the pull requests which matches this regex with the href"
    val screen_edit_repo_to_check_href_branch_regex_label = "Href branch regex"

    val screen_app_settings_saved = "Saved"
    val screen_app_settings_save = "Save"
    val screen_app_settings_synchronization_section = "Synchronization"
    val screen_app_settings_appliation_section = "Application"
    val screen_app_settings_github_token_field_label = "GitHub PAT token"
    val screen_app_settings_github_field_show = "Show GitHub PAT token"
    val screen_app_settings_github_field_hide = "Hide GitHub PAT token"
    val screen_app_settings_github_token_item = "GitHub PAT token"
    val screen_app_settings_github_token_item_description = "The minimum permissions for the PAT token are 'repo' (full). You can create a new PAT token from 'GitHub > Settings > Developer settings > Personal access tokens'"
    val screen_app_settings_repositories_item = "Repositories"
    val screen_app_settings_repositories_item_description: (Int) -> String = { "There are $it which will be checked every time for a new release or pull request status." }
    val screen_app_settings_checkout_timeout_item = "Refresh timeout"
    val screen_app_settings_checkout_timeout_item_description = "The timeout in minutes each time the pull requests and releases are checked"
    val screen_repos_to_check_bulk_import_item = "Bulk import"
    val screen_repos_to_check_export_item = "Export"
    val screen_repos_to_check_export_item_description = "A YML file will be created with all repositories"
    val screen_repos_to_check_bulk_import_item_description = "Choose a file which should contain a list of repositories in YML format"
    val screen_app_settings_theme_item = "Theme"
    val screen_app_settings_theme_item_description = "Select the theme you want to use"
    val screen_app_settings_pull_requests_clean_up_item = "Pull requests clean up"
    val screen_app_settings_pull_requests_clean_up_item_description = "Remove closed and merged pull requests after certain amount of time"

    val screen_login_encrypt_data_field_label = "I want to encrypt my data (recommended)."
    val screen_login_master_password_field_label = "Master Password"
    val screen_login_master_password_field_field_show = "Show master password"
    val screen_login_master_password_field_field_hide = "Hide master password"
    val screen_login_master_password_info = "Encrypted data requires a master password which is needed always to open the app."
    val screen_login_unlock_button = "Open the app"
    val screen_login_about_app_button = "About the app"
    val screen_login_create_new_database_button = "Start using the app"
    val screen_login_fresh_start = "Fresh start"
    val screen_login_fresh_start_confirmation_dialog_title = "Fresh start"
    val screen_login_fresh_start_confirmation_dialog_text = "A fresh start will erase your data, are you sure?"
    val screen_login_fresh_start_confirmation_dialog_yes_button = "Yes"
    val screen_login_fresh_start_confirmation_dialog_no_button = "No"

    val dialog_new_app_version_title = "Update available"
    val dialog_new_app_version_ignore_button = "Ignore"
    val dialog_new_app_version_update_button = "Update now"
    val dialog_new_app_version_text: (String) -> String = { "A new version of GHD ($it) is available for download! Would you like to update it now?" }
    val dialog_new_app_version_current_version = "Current version: ${BuildConfig.APP_VERSION}"
    val dialog_new_app_version_latest_version: (String) -> String = { "Latest version: $it" }

    val app_theme_system_default = "System default"
    val app_theme_dark = "Dark"
    val app_theme_light = "Light"

    val pull_request_updated_at: (Instant) -> String = { "updated ${it.toHRString()}" }
    val pull_request_state_open = "Open"
    val pull_request_state_closed = "Closed"
    val pull_request_state_merged = "Merged"
    val pull_request_state_draft = "Draft"
    val pull_request_state_unknown = "Draft"

    val app_settings_checkout_time_in_minutes: (Long) -> String = { "$it minutes" }
    val app_settings_checkout_time_unknown = "?"
    val app_settings_pr_cleanup_in_hours: (Long) -> String = { "$it hours" }
    val app_settings_pr_cleanup_unknown = "?"

    val file_dialog_choose_file = "Choose a file"
    val file_dialog_save_file = "Save a file"

    val error_unknown = "Unknown error"
    val error_db_wrong_password_format = "Wrong password format"
    val error_db_wrong_user_or_password = "Wrong password or username"
    val error_db_wrong_encryption_password = "Wrong encryption password"
    val error_db_database_not_foung = "Database not found"
    val error_db_database_already_open = "Database already opened"
    val error_db_unknown = "Unknown database error"
}
