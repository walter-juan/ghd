package com.woowla.ghd.core.extensions

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.luminance

val ColorScheme.isLight: Boolean
    get() = this.background.luminance() > 0.5