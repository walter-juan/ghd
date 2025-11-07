package com.woowla.ghd.domain.entities

data class Author(
    val login: String?,
    val url: String?,
    val avatarUrl: String?,
) {
    companion object {
        val copilotReviewerLogin = "copilot-pull-request-reviewer"
        val copilotReviewerLoginShort = "copilot"
    }

    val isCopilotReviewer: Boolean
        get() = login == copilotReviewerLogin
}
