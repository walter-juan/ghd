package com.woowla.ghd.presentation.app

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * application colors
 */
object AppColors {
    val ColorScheme.gitPrMerged: Color
        get() = Color(0xff745dd0)
    val ColorScheme.gitPrOpen: Color
        get() = Color(0xff679e60)
    val ColorScheme.gitPrClosed: Color
        get() = Color(0xffb04040)
    val ColorScheme.gitPrDraft: Color
        get() = Color(0xff71777f)

    val ColorScheme.info: Color
        get() = light_info
    val ColorScheme.onInfo: Color
        get() = light_onInfo
    val ColorScheme.infoContainer: Color
        get() = light_infoContainer
    val ColorScheme.onInfoContainer: Color
        get() = light_onInfoContainer

    val ColorScheme.success: Color
        get() = light_success
    val ColorScheme.onSuccess: Color
        get() = light_onSuccess
    val ColorScheme.successContainer: Color
        get() = light_successContainer
    val ColorScheme.onSuccessContainer: Color
        get() = light_onSuccessContainer

    val ColorScheme.warning: Color
        get() = light_warning
    val ColorScheme.onWarning: Color
        get() = light_onWarning
    val ColorScheme.warningContainer: Color
        get() = light_warningContainer
    val ColorScheme.onWarningContainer: Color
        get() = light_onWarningContainer

    val light_info = Color(0xFF4F88DE)
    val light_onInfo = Color(0xFFFFFFFF)
    val light_infoContainer = Color(0xFFD8E6FF)
    val light_onInfoContainer = Color(0xFF001E52)

    val light_success = Color(0xFF4EB045)
    val light_onSuccess = Color(0xFFFFFFFF)
    val light_successContainer = Color(0xFFD6FFE1)
    val light_onSuccessContainer = Color(0xFF005216)

    val light_warning = Color(0xFFDEBF50)
    val light_onWarning = Color(0xFFFFFFFF)
    val light_warningContainer = Color(0xFFFFF6D6)
    val light_onWarningContainer = Color(0xFF524000)

    val seed = Color(0xFF6F4EFF)

    // list of random colors
    val colorList = listOf(
        Color(0xFFFFB74D), // Vibrant Orange
        Color(0xFF81C784), // Soft Green
        Color(0xFF64B5F6), // Bright Blue
        Color(0xFFFFD54F), // Golden Yellow
        Color(0xFF4DB6AC), // Teal
        Color(0xFF9575CD), // Lavender
        Color(0xFFF06292), // Rose Pink
        Color(0xFF7986CB), // Medium Blue
        Color(0xFFAED581), // Lime Green
        Color(0xFFBA68C8), // Soft Purple
        Color(0xFFFF8A65), // Coral
        Color(0xFF4FC3F7), // Light Cyan
        Color(0xFFE6EE9C), // Light Yellow-Green
        Color(0xFF81D4FA), // Sky Blue
        Color(0xFFA1887F), // Warm Taupe
        Color(0xFFDCE775), // Chartreuse
        Color(0xFF90A4AE), // Neutral Gray-Blue
        Color(0xFFA5D6A7), // Mint Green
        Color(0xFFCE93D8), // Orchid
        Color(0xFFFFF176), // Sunny Yellow
        Color(0xFF9FA8DA), // Periwinkle
        Color(0xFF80CBC4), // Seafoam Green
        Color(0xFFFFE082), // Peach Yellow
        Color(0xFFB39DDB), // Pastel Purple
        Color(0xFFF48FB1)  // Pastel Pink
    )
}