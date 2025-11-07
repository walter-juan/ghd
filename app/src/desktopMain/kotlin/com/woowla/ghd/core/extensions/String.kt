package com.woowla.ghd.core.extensions

import androidx.compose.ui.graphics.Color
import com.materialkolor.ktx.lighten
import kotlin.math.absoluteValue

// list of random colors
private val colorList = listOf(
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
    Color(0xFFF48FB1) // Pastel Pink
)

/**
 * Return a color from string text, each call from the same string will return the same color
 */
fun String.toColor(): Color {
    return colorList[this.hashCode().absoluteValue % colorList.size].lighten(0.65F)
}
