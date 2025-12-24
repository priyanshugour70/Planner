package com.lssgoo.planner.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.lssgoo.planner.features.settings.models.ThemeMode

private val DarkColorScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    error = errorDark,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    error = errorLight,
    onError = Color.White
)

private val OceanColorScheme = lightColorScheme(
    primary = oceanPrimary,
    onPrimary = Color.White,
    secondary = oceanSecondary,
    tertiary = oceanTertiary,
    background = oceanBackground,
    surface = oceanSurface,
    onSurface = oceanOnSurface
)

private val SunsetColorScheme = lightColorScheme(
    primary = sunsetPrimary,
    onPrimary = Color.White,
    secondary = sunsetSecondary,
    tertiary = sunsetTertiary,
    background = sunsetBackground,
    surface = sunsetSurface,
    onSurface = sunsetOnSurface
)

private val ForestColorScheme = lightColorScheme(
    primary = forestPrimary,
    onPrimary = Color.White,
    secondary = forestSecondary,
    tertiary = forestTertiary,
    background = forestBackground,
    surface = forestSurface,
    onSurface = forestOnSurface
)

private val MidnightColorScheme = darkColorScheme(
    primary = midnightPrimary,
    onPrimary = Color.White,
    secondary = midnightSecondary,
    tertiary = midnightTertiary,
    background = midnightBackground,
    surface = midnightSurface,
    onSurface = midnightOnSurface
)

// New Premium Themes

private val RoseGoldColorScheme = lightColorScheme(
    primary = roseGoldPrimary,
    onPrimary = Color.White,
    secondary = roseGoldSecondary,
    tertiary = roseGoldTertiary,
    background = roseGoldBackground,
    surface = roseGoldSurface,
    onSurface = roseGoldOnSurface,
    surfaceVariant = Color(0xFFFFE4E4),
    onSurfaceVariant = Color(0xFF7D5454)
)

private val NordColorScheme = darkColorScheme(
    primary = nordPrimary,
    onPrimary = Color(0xFF2E3440),
    secondary = nordSecondary,
    tertiary = nordTertiary,
    background = nordBackground,
    surface = nordSurface,
    onSurface = nordOnSurface,
    surfaceVariant = Color(0xFF434C5E),
    onSurfaceVariant = Color(0xFFD8DEE9)
)

private val SolarizedColorScheme = darkColorScheme(
    primary = solarizedPrimary,
    onPrimary = Color(0xFF002B36),
    secondary = solarizedSecondary,
    tertiary = solarizedTertiary,
    background = solarizedBackground,
    surface = solarizedSurface,
    onSurface = solarizedOnSurface,
    surfaceVariant = Color(0xFF0A4A58),
    onSurfaceVariant = Color(0xFF93A1A1)
)

private val LavenderColorScheme = lightColorScheme(
    primary = lavenderPrimary,
    onPrimary = Color.White,
    secondary = lavenderSecondary,
    tertiary = lavenderTertiary,
    background = lavenderBackground,
    surface = lavenderSurface,
    onSurface = lavenderOnSurface,
    surfaceVariant = Color(0xFFE8E1FF),
    onSurfaceVariant = Color(0xFF5D4E7D)
)

private val MochaColorScheme = darkColorScheme(
    primary = mochaPrimary,
    onPrimary = Color(0xFF1E1E2E),
    secondary = mochaSecondary,
    tertiary = mochaTertiary,
    background = mochaBackground,
    surface = mochaSurface,
    onSurface = mochaOnSurface,
    surfaceVariant = Color(0xFF45475A),
    onSurfaceVariant = Color(0xFFBAC2DE)
)

@Composable
fun PlannerTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val systemDarkTheme = isSystemInDarkTheme()
    val colorScheme = when (themeMode) {
        ThemeMode.LIGHT -> LightColorScheme
        ThemeMode.DARK -> DarkColorScheme
        ThemeMode.OCEAN -> OceanColorScheme
        ThemeMode.SUNSET -> SunsetColorScheme
        ThemeMode.FOREST -> ForestColorScheme
        ThemeMode.MIDNIGHT -> MidnightColorScheme
        ThemeMode.ROSE_GOLD -> RoseGoldColorScheme
        ThemeMode.NORD -> NordColorScheme
        ThemeMode.SOLARIZED -> SolarizedColorScheme
        ThemeMode.LAVENDER -> LavenderColorScheme
        ThemeMode.MOCHA -> MochaColorScheme
        ThemeMode.SYSTEM -> if (systemDarkTheme) DarkColorScheme else LightColorScheme
    }
    
    val isDark = when (themeMode) {
        ThemeMode.DARK, ThemeMode.MIDNIGHT, ThemeMode.NORD, ThemeMode.SOLARIZED, ThemeMode.MOCHA -> true
        ThemeMode.LIGHT, ThemeMode.OCEAN, ThemeMode.SUNSET, ThemeMode.FOREST, ThemeMode.ROSE_GOLD, ThemeMode.LAVENDER -> false
        ThemeMode.SYSTEM -> systemDarkTheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Set status bar and navigation bar to match theme colors
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            // Light icons for dark theme, dark icons for light theme
            val isLightStatusBar = !isDark
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = isLightStatusBar
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = isLightStatusBar
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}