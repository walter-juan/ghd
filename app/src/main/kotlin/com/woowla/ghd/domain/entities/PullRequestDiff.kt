package com.woowla.ghd.domain.entities

data class PullRequestDiff(
    val stateChanged: Boolean,
    val commentAdded: Boolean,
    val reviewAdded: Boolean,
    val checkStatusChanged: Boolean,
)