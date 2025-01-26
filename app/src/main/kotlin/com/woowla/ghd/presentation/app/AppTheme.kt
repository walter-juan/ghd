package com.woowla.ghd.presentation.app

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import com.materialkolor.DynamicMaterialTheme
import com.materialkolor.PaletteStyle
import com.materialkolor.rememberDynamicMaterialThemeState

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val dynamicThemeState = rememberDynamicMaterialThemeState(
        isDark = darkTheme,
        style = PaletteStyle.TonalSpot,
        seedColor = AppColors.seed,
    )

    DynamicMaterialTheme(
        state = dynamicThemeState,
        animate = true,
        content = content,
    )
}