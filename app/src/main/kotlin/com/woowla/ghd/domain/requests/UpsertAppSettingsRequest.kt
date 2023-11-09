package com.woowla.ghd.domain.requests

data class UpsertAppSettingsRequest(
    val darkTheme: Boolean?,
    val encryptedDatabase: Boolean,
    val featurePreviewNewCards: Boolean?,
    val featurePreviewNewCardsBoldStyle: Boolean?,
)