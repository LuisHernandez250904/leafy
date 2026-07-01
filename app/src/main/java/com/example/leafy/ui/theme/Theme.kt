package com.example.leafy.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = ColorPrimary,
    onPrimary = ColorWhite,
    secondary = ColorLight,
    background = ColorSand,
    onBackground = ColorDark,
    surface = ColorWhite,
)

private val DarkColors = darkColorScheme(
    primary = ColorLight,
    onPrimary = ColorDark
)

@Composable
fun LeafyTheme(darkTheme: Boolean = false, content: @Composable () -> Unit) {
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        typography = AppTypography,
        content = content
    )
}
