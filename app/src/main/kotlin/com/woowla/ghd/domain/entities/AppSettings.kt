package com.woowla.ghd.domain.entities

data class AppSettings(
    val darkTheme: Boolean?,
    val encryptedDatabase: Boolean,
    val featurePreviewNewCards: Boolean?,
    val featurePreviewNewCardsBoldStyle: Boolean?,
)
