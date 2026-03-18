package com.example.nutrishare_android.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val NutriShareColorScheme = lightColorScheme(
    primary = NutriGreen,
    onPrimary = SurfaceWhite,
    primaryContainer = NutriGreenContainer,
    onPrimaryContainer = NutriGreenDark,
    secondary = NutriOrange,
    onSecondary = TextPrimary,
    secondaryContainer = NutriOrangeContainer,
    onSecondaryContainer = TextPrimary,
    tertiary = NutriGreenLight,
    onTertiary = TextPrimary,
    tertiaryContainer = NutriAccentSoft,
    onTertiaryContainer = TextPrimary,
    error = NutriError,
    background = BackgroundLight,
    surface = SurfaceWhite,
    surfaceBright = SurfaceWhite,
    surfaceContainer = SurfaceMuted,
    surfaceContainerHigh = SurfaceMuted,
    surfaceContainerHighest = NutriAccentSoft,
    surfaceVariant = SurfaceMuted,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    outline = DividerColor,
    outlineVariant = DividerColor,
    surfaceTint = NutriGreen
)

@Composable
fun Nutrishare_androidTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = SurfaceWhite.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = NutriShareColorScheme,
        typography = Typography,
        content = content
    )
}
