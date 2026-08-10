package com.example.codebox.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun CodeboxTheme(content: @Composable () -> Unit) {
    val colorScheme = darkColorScheme(
        background = BackgroundDark,
        surface = SurfaceDark,
        surfaceVariant = PanelDark,
        onSurface = TextPrimary,
        onSurfaceVariant = TextSecondary,
        primary = TerminalGreen,
        onPrimary = PureBlack,
        outline = OutlineDark,
        error = TerminalGreen
    )
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}