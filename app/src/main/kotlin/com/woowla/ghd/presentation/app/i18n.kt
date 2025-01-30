package com.woowla.ghd.presentation.app

import com.woowla.ghd.BuildConfig
import com.woowla.ghd.extensions.format
import com.woowla.ghd.extensions.toRelativeString
import kotlinx.datetime.Instant

object i18n {
    val app_name = if (BuildConfig.DEBUG) {
        "DEBUG // GHD - GitHub Dashboard"
    } else {
        "GHD - GitHub Dashboard"
    }
    val githubRepoLink = "https://github.com/walter-juan/ghd"
    val tablerIconsRepoLink = "https://github.com/tabler/tabler-icons"
    val generic_loading = "Loading"
    val generic_error = "Error"
    val generic_saved = "Saved"
    val generic_now =  "now"
    val generic_delete =  "Delete"
    val generic_in_seconds: (Long) -> String = { "in $it seconds" }
    val generic_in_minutes: (Long) -> String = { "in $it minutes" }
    val generic_in_hours: (Long) -> String = { "in $it hours" }
    val generic_in_days: (Long) -> String = { "in $it days" }
    val generic_seconds_ago: (Long) -> String = { "$it seconds ago" }
    val generic_minutes_ago: (Long) -> String = { "$it minutes ago" }
    val generic_hours_ago: (Long) -> String = { "$it hours ago" }
    val generic_days_ago: (Long) -> String = { "$it days ago" }
    val generic_date_format: (String, String, String, String) -> String = { month, day, year, hour -> "$month $day, $year at $hour" }
    val generic_unknown = "unknown"
    val generic_enabled = "Enabled"
    val generic_disabled = "Disabled"

    val menu_bar_menu_actions = "Actions"
    val menu_bar_menu_item_synchronize = "Synchronize"

    val tray_tooltip = app_name
    val tray_item_synchronize = "Synchronize"
    val tray_item_show_app = "Show"
    val tray_item_hide_app = "Hide in tray"
    val tray_item_exit = "Exit"
    val tray_linux_primary_action_label = "Open Application"

    val top_bar_title_pull_requests = "Pull Requests"
    val top_bar_title_releases = "Releases"
    val top_bar_title_repos_to_check = "Repos"
    val top_bar_title_repos_to_check_edit = "Repos"
    val top_bar_title_repos_to_check_bulk = "Bulk import/export"
    val top_bar_title_repos_to_check_bulk_sample = "Bulk file sample"
    val top_bar_title_settings = "Settings"
    val top_bar_title_synchronization_results = "Synchronization results"
    val top_bar_title_synchronization_result_entries = "Synchronization result entries"
    val top_bar_subtitle_synchronization_result_entries: (errorPercentage: Int, total: Int) -> String = { errorPercentage, total -> "$errorPercentage% errors out of a total of $total" }
    val top_bar_title_about = "About"
    val top_bar_title_notifications = "Notifications"

    val status_bar_loading = "Loading..."
    val status_bar_error = "Error"
    val status_bar_synchronized_at_unknown = "Synchronized at unknown"

    val tab_title_pull_requests = "Pulls"
    val tab_title_releases = "Releases"
    val tab_title_repos_to_check = "Repos"
    val tab_title_notifications = "Notifications"
    val tab_title_settings = "Settings"
    val tab_title_about = "About"

    val screen_repos_to_check_new_repositories_section = "New repositories"
    val screen_repos_to_check_add_new_repository_item = "Add new repository"
    val screen_repos_to_check_add_new_repository_item_description = "Add manually a new repository"
    val screen_repos_to_check_repositories_section = "Repositories"

    val screen_edit_repo_to_check_save = "Save"
    val screen_edit_repo_to_check_repository_section = "Repository"
    val screen_edit_repo_to_check_owner_label = "Owner"
    val screen_edit_repo_to_check_name_label = "Name"
    val screen_edit_repo_to_check_group_item = "Group"
    val screen_edit_repo_to_check_group_item_description = "Join several repositories in the same group"
    val screen_edit_repo_to_check_group_name_label = "Group name"
    val screen_edit_repo_to_check_pull_request_section = "Sync pull requests"
    val screen_edit_repo_to_check_releaes_section = "Sync releases"
    val screen_edit_repo_to_check_filter_by_branch_item = "Filter by branch"
    val screen_edit_repo_to_check_filter_by_branch_item_description = "Add a regex if you want to show only the pull requests which matches this regex with the href"
    val screen_edit_repo_to_check_href_branch_regex_label = "Href branch regex"
    val screen_edit_repo_to_no_group = "(no group)"

    val screen_pull_requests_can_be_merged = "Ready to merge"
    val screen_pull_requests_code_changed = "Code changed since list time"

    val screen_app_settings_saved = "Saved"
    val screen_app_settings_save = "Save"
    val screen_app_settings_synchronization_section = "Synchronization"
    val screen_app_settings_appliation_section = "Application"
    val screen_app_settings_pull_requests_notifications_section = "Pull requests notifications"
    val screen_app_settings_releases_notifications_section = "Releases notifications"
    val screen_app_settings_notifications_pr_filter_out_title = "Filter notifications by state"
    val screen_app_settings_notifications_pr_filter_out_description = "Enable notifications only for specific states."
    val screen_app_settings_notifications_pr_state_title = "State changes notifications"
    val screen_app_settings_notifications_pr_state_description = "Enable to receive notifications when the sate of a pull request changes (e.g., Draft → Open), new pull requests are considered as changes."
    val screen_app_settings_notifications_pr_state_checkbox_label = "State changes"
    val screen_app_settings_notifications_pr_activity_title = "Activity notifications"
    val screen_app_settings_notifications_pr_activity_description = "Enable to receive notifications for activity updates to pull requests. You will receive notifications for new or changed reviews, re-review requested, checks and when a pull request is ready to be merged (mergeable)."
    val screen_app_settings_notifications_pr_activity_checkbox_label = "Activity"
    val screen_app_settings_notifications_new_release_title = "Created notifications"
    val screen_app_settings_notifications_new_release_description = "Enable to receive notifications when a new release is created."
    val screen_app_settings_notifications_update_release_title = "Release updated notifications"
    val screen_app_settings_notifications_update_release_description = "Enable to receive notifications when an existing release is updated."
    val screen_app_settings_github_token_field_label = "Token"
    val screen_app_settings_github_field_show = "Show token"
    val screen_app_settings_github_field_hide = "Hide token"
    val screen_app_settings_github_token_item = "Personal Access Token"
    val screen_app_settings_github_token_item_description = "Configure your GitHub Personal Access Token for authentication. The minimum permissions for the PAT token are 'repo' (full)."
    val screen_app_settings_repositories_item = "Repositories"
    val screen_app_settings_repositories_item_description: (Int) -> String = { "You have a total of $it repositories." }
    val screen_app_settings_checkout_timeout_item = "Sync Interval"
    val screen_app_settings_checkout_timeout_item_description = "How often to check for updates"

    val screen_repos_to_check_bulk_item = "Bulk import/export"
    val screen_repos_to_check_bulk_item_description = "Import or export a list of repositories in YML format"
    val screen_repos_to_check_bulk_example = "Example"
    val screen_repos_to_check_bulk_import = "Import"
    val screen_repos_to_check_bulk_export = "Export"

    val screen_repos_to_check_bulk_sample_sample_file = """
#
# The only required values are the owner and name 
# and by default the pull requests and releases synchronization 
# is enabled but not the notifications
# 
#
repositories:
- owner: "owner-1"
  name: "project-1"
  group: "my group"
  pulls:
    enabled: true
    branch-regex: ""
    notifications-enabled: true
  releases:
    enabled: true
    notifications-enabled: false
- owner: "owner-1"
  name: "project-2"
- owner: "owner-2"
  name: "project-1"
    """.trimIndent()

    val screen_app_settings_theme_item = "Theme"
    val screen_app_settings_theme_item_description = "Select the theme you want to use"
    val screen_app_settings_pull_requests_clean_up_item = "Pull requests clean up"
    val screen_app_settings_pull_requests_clean_up_item_description = "Remove closed and merged pull requests after certain amount of time"
    val screen_app_settings_last_synchronization_results_item = "Latest synchronization results"
    val screen_app_settings_last_synchronization_results_button = "See the latest synchronization results"

    val screen_login_unlock_button = "Open the app"
    val screen_login_about_app_button = "About"
    val screen_login_fresh_start = "Fresh start"
    val screen_login_fresh_start_confirmation_dialog_title = "Fresh start"
    val screen_login_fresh_start_confirmation_dialog_text = "A fresh start will erase your data, are you sure?"
    val screen_login_fresh_start_confirmation_dialog_yes_button = "Yes"
    val screen_login_fresh_start_confirmation_dialog_no_button = "No"

    val screen_sync_results_in_progress = "Sync in progress"
    val screen_sync_results_took_seconds: (seconds: Double) -> String = { "$it seconds" }
    val screen_sync_results_start_at: (instant: Instant) -> String = { "Synchronization started at ${it.format()}" }
    val screen_sync_results_end_at: (emoji: String, percentage: Int, total: Int) -> String = { emoji, percentage, total ->  "$emoji ${percentage}% errors out of a total of $total" }

    val screen_sync_result_entries_took_seconds: (seconds: Double) -> String = { "Took $it seconds" }

    val dialog_new_app_version_title = "Update available"
    val dialog_new_app_version_ignore_button = "Ignore"
    val dialog_new_app_version_update_button = "Update now"
    val dialog_new_app_version_text: (String) -> String = { "A new version of GHD ($it) is available for download! Would you like to update it now?" }
    val dialog_new_app_version_current_version = "Current version: ${BuildConfig.APP_VERSION}"
    val dialog_new_app_version_latest_version: (String) -> String = { "Latest version: $it" }

    val app_theme_system_default = "System default"
    val app_theme_dark = "Dark"
    val app_theme_light = "Light"

    val pull_request_state_open = "Open"
    val pull_request_state_closed = "Closed"
    val pull_request_state_merged = "Merged"
    val pull_request_state_draft = "Draft"
    val pull_request_state_unknown = "Unknown"
    val pull_request_comments: (Long) -> String = {
        when(it) {
            1L -> "$it comment"
            else -> "$it comments"
        }
    }
    val pull_request_opened_by = "opened by"
    val pull_request_on = "on"
    val pull_request_updated: (Instant) -> String = { "Updated ${it.toRelativeString()}" }

    val review_submitted: (Instant) -> String = { "Submitted ${it.toRelativeString()}" }

    val release_tag = "Tag"
    val release_on = "on"
    val release_published: (Instant) -> String = { "Published ${it.toRelativeString()}" }

    val app_settings_checkout_time_in_minutes: (Long) -> String = { "$it minutes" }
    val app_settings_checkout_time_unknown = "?"
    val app_settings_pr_cleanup_in_hours: (Long) -> String = { "$it hours" }
    val app_settings_pr_cleanup_unknown = "?"


    val sync_result_title_old: (syncAt: Instant, emoji: String, rateLimitPercentageUsed: Int?, rateLimitResetAt: Instant?) -> String = { instant, emoji, rateLimitPercentageUsed, rateLimitResetAt ->
        "$emoji Synchronized at ${instant.format()} ($rateLimitPercentageUsed% API limit used, resets ${rateLimitResetAt?.toRelativeString()})"
    }
    val sync_result_title: (syncAt: Instant, rateLimitPercentageUsed: Int?, rateLimitResetAt: Instant?) -> String = { instant, rateLimitPercentageUsed, rateLimitResetAt ->
        "Synchronized at ${instant.format()} ($rateLimitPercentageUsed% API limit used, resets ${rateLimitResetAt?.toRelativeString()})"
    }


    val file_dialog_choose_file = "Choose a file"
    val file_dialog_save_file = "Save a file"
}
