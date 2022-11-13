package com.woowla.ghd.presentation.app

import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.woowla.ghd.utils.MaterialColors

/**
 * application colors
 */
object AppColors {
    val gitPrMerged = Color(0xff745dd0)
    val gitPrOpen = Color(0xff679e60)
    val gitPrClosed = Color(0xffb04040)
    val gitPrDraft = Color(0xff71777f)

    @Composable
    fun materialColors(lightTheme: Boolean): Colors {
        return if (lightTheme) {
            lightColors(
                primary = MaterialColors.Purple500,
                primaryVariant = MaterialColors.Purple700,
                secondary = MaterialColors.Teal200,
                secondaryVariant = MaterialColors.Teal400,
            )
        } else {
            darkColors(
                primary = MaterialColors.Purple200,
                primaryVariant = MaterialColors.Purple700,
                secondary = MaterialColors.Teal200,
                secondaryVariant = MaterialColors.Teal400,
            )
        }
    }

    @Composable
    fun navRailBackground(lightTheme: Boolean = MaterialTheme.colors.isLight): Color {
        return if (lightTheme) {
            MaterialColors.Gray200
        } else {
            MaterialColors.Black
        }
    }

    @Composable
    fun navRailItemUnselectedContentColor(lightTheme: Boolean = MaterialTheme.colors.isLight): Color {
        return if (lightTheme) {
            MaterialColors.Gray700
        } else {
            MaterialColors.Gray500
        }
    }

    @Composable
    fun statusBarBackground(lightTheme: Boolean = MaterialTheme.colors.isLight): Color {
        return if (lightTheme) {
            MaterialColors.Gray200
        } else {
            MaterialColors.Black
        }
    }

    @Composable
    fun topBarBackground(lightTheme: Boolean = MaterialTheme.colors.isLight): Color {
        return if (lightTheme) {
            MaterialColors.Gray200
        } else {
            MaterialColors.Black
        }
    }

    @Composable
    fun topBarContent(lightTheme: Boolean = MaterialTheme.colors.isLight): Color {
        return if (lightTheme) {
            MaterialColors.Gray700
        } else {
            MaterialColors.Gray500
        }
    }
}