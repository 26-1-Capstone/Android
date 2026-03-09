package com.example.nutrishare_android.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
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
    onSecondary = SurfaceWhite,
    error = NutriError,
    background = BackgroundLight,
    surface = SurfaceWhite,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    outline = DividerColor
)

@Composable
fun Nutrishare_androidTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = NutriGreen.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = NutriShareColorScheme,
        typography = Typography,
        content = content
    )
}