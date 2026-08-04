package com.example.codebox.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val RetroDarkColorScheme = darkColorScheme(
    primary = TerminalGreen,
    onPrimary = BackgroundDark,
    primaryContainer = TerminalGreenDark,
    onPrimaryContainer = TerminalGreen,
    secondary = BlueTag,
    onSecondary = BackgroundDark,
    secondaryContainer = BlueTag.copy(alpha = 0.15f),
    onSecondaryContainer = BlueTag,
    tertiary = PurpleTag,
    onTertiary = BackgroundDark,
    tertiaryContainer = PurpleTag.copy(alpha = 0.15f),
    onTertiaryContainer = PurpleTag,
    background = BackgroundDark,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = PanelDark,
    onSurfaceVariant = TextSecondary,
    surfaceTint = TerminalGreen,
    inverseSurface = ChromeDark,
    inverseOnSurface = TextPrimary,
    error = RedWin,
    onError = BackgroundDark,
    errorContainer = RedWin.copy(alpha = 0.15f),
    onErrorContainer = RedWin,
    outline = OutlineMuted,
    outlineVariant = OutlineDark,
    scrim = BackgroundDark.copy(alpha = 0.8f)
)

@Composable
fun CodeboxTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = RetroDarkColorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}