package com.woowla.ghd.domain.requests

data class UpsertAppSettingsRequest(
    val darkTheme: Boolean?,
    val encryptedDatabase: Boolean,
)