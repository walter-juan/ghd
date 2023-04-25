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

    val md_theme_light_primary = Color(0xFF724C9E)
    val md_theme_light_onPrimary = Color(0xFFFFFFFF)
    val md_theme_light_primaryContainer = Color(0xFFEEDBFF)
    val md_theme_light_onPrimaryContainer = Color(0xFF2A0053)
    val md_theme_light_secondary = Color(0xFF006684)
    val md_theme_light_onSecondary = Color(0xFFFFFFFF)
    val md_theme_light_secondaryContainer = Color(0xFFBEE9FF)
    val md_theme_light_onSecondaryContainer = Color(0xFF001F2A)
    val md_theme_light_tertiary = Color(0xFF795900)
    val md_theme_light_onTertiary = Color(0xFFFFFFFF)
    val md_theme_light_tertiaryContainer = Color(0xFFFFDEA1)
    val md_theme_light_onTertiaryContainer = Color(0xFF261900)
    val md_theme_light_error = Color(0xFFBA1A1A)
    val md_theme_light_errorContainer = Color(0xFFFFDAD6)
    val md_theme_light_onError = Color(0xFFFFFFFF)
    val md_theme_light_onErrorContainer = Color(0xFF410002)
    val md_theme_light_background = Color(0xFFFAFCFF)
    val md_theme_light_onBackground = Color(0xFF001F2A)
    val md_theme_light_surface = Color(0xFFFAFCFF)
    val md_theme_light_onSurface = Color(0xFF001F2A)
    val md_theme_light_surfaceVariant = Color(0xFFE8E0EB)
    val md_theme_light_onSurfaceVariant = Color(0xFF4A454E)
    val md_theme_light_outline = Color(0xFF7B757F)
    val md_theme_light_inverseOnSurface = Color(0xFFE1F4FF)
    val md_theme_light_inverseSurface = Color(0xFF003547)
    val md_theme_light_inversePrimary = Color(0xFFDAB9FF)
    val md_theme_light_shadow = Color(0xFF000000)
    val md_theme_light_surfaceTint = Color(0xFF724C9E)
    val md_theme_light_outlineVariant = Color(0xFFCCC4CF)
    val md_theme_light_scrim = Color(0xFF000000)

    val md_theme_dark_primary = Color(0xFFDAB9FF)
    val md_theme_dark_onPrimary = Color(0xFF411A6C)
    val md_theme_dark_primaryContainer = Color(0xFF593485)
    val md_theme_dark_onPrimaryContainer = Color(0xFFEEDBFF)
    val md_theme_dark_secondary = Color(0xFF68D3FF)
    val md_theme_dark_onSecondary = Color(0xFF003546)
    val md_theme_dark_secondaryContainer = Color(0xFF004D64)
    val md_theme_dark_onSecondaryContainer = Color(0xFFBEE9FF)
    val md_theme_dark_tertiary = Color(0xFFF3BF48)
    val md_theme_dark_onTertiary = Color(0xFF402D00)
    val md_theme_dark_tertiaryContainer = Color(0xFF5C4300)
    val md_theme_dark_onTertiaryContainer = Color(0xFFFFDEA1)
    val md_theme_dark_error = Color(0xFFFFB4AB)
    val md_theme_dark_errorContainer = Color(0xFF93000A)
    val md_theme_dark_onError = Color(0xFF690005)
    val md_theme_dark_onErrorContainer = Color(0xFFFFDAD6)
    val md_theme_dark_background = Color(0xFF001F2A)
    val md_theme_dark_onBackground = Color(0xFFBFE9FF)
    val md_theme_dark_surface = Color(0xFF001F2A)
    val md_theme_dark_onSurface = Color(0xFFBFE9FF)
    val md_theme_dark_surfaceVariant = Color(0xFF4A454E)
    val md_theme_dark_onSurfaceVariant = Color(0xFFCCC4CF)
    val md_theme_dark_outline = Color(0xFF958E98)
    val md_theme_dark_inverseOnSurface = Color(0xFF001F2A)
    val md_theme_dark_inverseSurface = Color(0xFFBFE9FF)
    val md_theme_dark_inversePrimary = Color(0xFF724C9E)
    val md_theme_dark_shadow = Color(0xFF000000)
    val md_theme_dark_surfaceTint = Color(0xFFDAB9FF)
    val md_theme_dark_outlineVariant = Color(0xFF4A454E)
    val md_theme_dark_scrim = Color(0xFF000000)

    val seed = Color(0xFF85709C)

    val LightColors = lightColorScheme(
        primary = md_theme_light_primary,
        onPrimary = md_theme_light_onPrimary,
        primaryContainer = md_theme_light_primaryContainer,
        onPrimaryContainer = md_theme_light_onPrimaryContainer,
        secondary = md_theme_light_secondary,
        onSecondary = md_theme_light_onSecondary,
        secondaryContainer = md_theme_light_secondaryContainer,
        onSecondaryContainer = md_theme_light_onSecondaryContainer,
        tertiary = md_theme_light_tertiary,
        onTertiary = md_theme_light_onTertiary,
        tertiaryContainer = md_theme_light_tertiaryContainer,
        onTertiaryContainer = md_theme_light_onTertiaryContainer,
        error = md_theme_light_error,
        errorContainer = md_theme_light_errorContainer,
        onError = md_theme_light_onError,
        onErrorContainer = md_theme_light_onErrorContainer,
        background = md_theme_light_background,
        onBackground = md_theme_light_onBackground,
        surface = md_theme_light_surface,
        onSurface = md_theme_light_onSurface,
        surfaceVariant = md_theme_light_surfaceVariant,
        onSurfaceVariant = md_theme_light_onSurfaceVariant,
        outline = md_theme_light_outline,
        inverseOnSurface = md_theme_light_inverseOnSurface,
        inverseSurface = md_theme_light_inverseSurface,
        inversePrimary = md_theme_light_inversePrimary,
        surfaceTint = md_theme_light_surfaceTint,
        outlineVariant = md_theme_light_outlineVariant,
        scrim = md_theme_light_scrim,
    )

    val DarkColors = darkColorScheme(
        primary = md_theme_dark_primary,
        onPrimary = md_theme_dark_onPrimary,
        primaryContainer = md_theme_dark_primaryContainer,
        onPrimaryContainer = md_theme_dark_onPrimaryContainer,
        secondary = md_theme_dark_secondary,
        onSecondary = md_theme_dark_onSecondary,
        secondaryContainer = md_theme_dark_secondaryContainer,
        onSecondaryContainer = md_theme_dark_onSecondaryContainer,
        tertiary = md_theme_dark_tertiary,
        onTertiary = md_theme_dark_onTertiary,
        tertiaryContainer = md_theme_dark_tertiaryContainer,
        onTertiaryContainer = md_theme_dark_onTertiaryContainer,
        error = md_theme_dark_error,
        errorContainer = md_theme_dark_errorContainer,
        onError = md_theme_dark_onError,
        onErrorContainer = md_theme_dark_onErrorContainer,
        background = md_theme_dark_background,
        onBackground = md_theme_dark_onBackground,
        surface = md_theme_dark_surface,
        onSurface = md_theme_dark_onSurface,
        surfaceVariant = md_theme_dark_surfaceVariant,
        onSurfaceVariant = md_theme_dark_onSurfaceVariant,
        outline = md_theme_dark_outline,
        inverseOnSurface = md_theme_dark_inverseOnSurface,
        inverseSurface = md_theme_dark_inverseSurface,
        inversePrimary = md_theme_dark_inversePrimary,
        surfaceTint = md_theme_dark_surfaceTint,
        outlineVariant = md_theme_dark_outlineVariant,
        scrim = md_theme_dark_scrim,
    )
}