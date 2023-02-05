package com.woowla.ghd.presentation.app

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {

    val colors = if (darkTheme) {
        AppColors.DarkColors
    } else {
        AppColors.LightColors
    }

    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}