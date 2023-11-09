package com.woowla.ghd.domain.mappers

import com.woowla.ghd.data.local.prop.entities.PropAppSettings
import com.woowla.ghd.domain.entities.AppSettings

fun PropAppSettings.toAppSettings(): AppSettings {
    return AppSettings(
        darkTheme = darkTheme,
        encryptedDatabase = encryptedDatabase ?: false,
        featurePreviewNewCards = featurePreviewNewCards,
        featurePreviewNewCardsBoldStyle = featurePreviewNewCardsBoldStyle,
    )
}