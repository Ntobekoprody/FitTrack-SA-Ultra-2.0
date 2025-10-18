package com.fittracksa.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Lime,
    onPrimary = Black,
    secondary = Lime,
    onSecondary = Black,
    background = Black,
    onBackground = Lime,
    surface = Black,
    onSurface = Lime,
    primaryContainer = Lime,
    onPrimaryContainer = Black,
    secondaryContainer = Lime,
    onSecondaryContainer = Black
)

private val LightColorScheme = lightColorScheme(
    primary = Black,
    onPrimary = Lime,
    secondary = Black,
    onSecondary = Lime,
    background = White,
    onBackground = Black,
    surface = White,
    onSurface = Black,
    primaryContainer = Black,
    onPrimaryContainer = Lime,
    secondaryContainer = Black,
    onSecondaryContainer = Lime
)

@Composable
fun FitTrackTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
