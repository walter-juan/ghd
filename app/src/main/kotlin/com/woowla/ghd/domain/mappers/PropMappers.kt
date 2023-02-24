package com.woowla.ghd.domain.mappers

import com.woowla.ghd.data.local.prop.entities.PropAppSettings
import com.woowla.ghd.domain.entities.AppSettings

fun PropAppSettings.toAppSettings(): AppSettings {
    return AppSettings(
        darkTheme = darkTheme,
        featurePreviewNewCards = featurePreviewNewCards,
        featurePreviewNewCardsBoldStyle = featurePreviewNewCardsBoldStyle
    )
}