package com.example.trama.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
private val CustomDarkColorScheme = darkColorScheme(
    primary = FrambuesaPrimary,
    secondary = RosaPastel,
    background = DarkBackground,
    surface = SurfaceCard,
    onPrimary = Color.White,
    onSecondary = DarkBackground,
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun TramaTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = CustomDarkColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}