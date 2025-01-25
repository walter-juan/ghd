package com.woowla.ghd.extensions

import androidx.compose.ui.graphics.Color
import com.materialkolor.ktx.lighten
import com.woowla.ghd.presentation.app.AppColors
import kotlin.math.absoluteValue


/**
 * Return a color from string text, each call from the same string will return the same color
 */
fun String.toColor(): Color {
    return AppColors.colorList[this.hashCode().absoluteValue % AppColors.colorList.size].lighten(0.65F)
}
