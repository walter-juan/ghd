package com.woowla.ghd.presentation.app

import com.woowla.ghd.BuildConfig

object AppIcons {
    val launcher = if (BuildConfig.DEBUG) {
        "icons/custom/ic_launcher_debug.svg"
    } else {
        "icons/custom/ic_launcher.svg"
    }
    const val placeholder = "icons/custom/placeholder.png"
    val trayIcon = if (BuildConfig.DEBUG) {
        "icons/custom/ic_tray_debug.svg"
    } else {
        "icons/custom/ic_tray.svg"
    }

    const val gitHubPrMerged = "icons/octicons/ic_github_git_merge.xml"
    const val gitHubPrOpen = "icons/octicons/ic_github_git_pull_request.xml"
    const val gitHubPrClosed = "icons/octicons/ic_github_git_pull_request_closed.xml"
    const val gitHubPrDraft = "icons/octicons/ic_github_git_pull_request_draft.xml"
    const val gitHubPrUnknown = "icons/octicons/ic_github_question.xml"

    const val gitPullRequest = "icons/iconoir/ic_git_pull_request.svg"
    const val infoEmpty = "icons/iconoir/ic_info_empty.svg"
    const val packages = "icons/iconoir/ic_packages.svg"
    const val repository = "icons/iconoir/ic_repository.svg"
    const val settings = "icons/iconoir/ic_settings.svg"
    const val checkCircledOutline = "icons/iconoir/ic_check_circled_outline.svg"
    const val warningCircledOutline = "icons/iconoir/ic_warning_circled_outline.svg"
    const val questionMarkCircle = "icons/iconoir/ic_question_mark_circle.svg"
    const val dotCircle = "icons/custom/ic_dot_circle.svg"
}