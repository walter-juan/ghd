package com.woowla.ghd.domain.entities

data class PullRequestDiff(
    val stateChanged: Boolean,
    val commentAdded: Boolean,
    val reviewsChanged: Boolean,
    val checkStatusChanged: Boolean,
    val codeChanged: Boolean,
)