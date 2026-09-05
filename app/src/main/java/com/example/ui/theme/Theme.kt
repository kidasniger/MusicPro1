package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Thème sombre exclusif MusicPro (conforme à la maquette)
private val MusicProDarkColorScheme = darkColorScheme(
    primary = CyanAccent,
    onPrimary = Color.Black,
    primaryContainer = PurpleDeep,
    onPrimaryContainer = PurpleLight,
    secondary = PurpleAccent,
    onSecondary = Color.White,
    secondaryContainer = SurfacePurpleTint,
    onSecondaryContainer = PurpleLight,
    tertiary = AmberAccent,
    onTertiary = Color.Black,
    background = BackgroundDark,
    onBackground = TextPrimary,
    surface = SurfaceCard,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceElevated,
    onSurfaceVariant = TextSecondary,
    surfaceTint = CyanAccent,
    outline = BorderSubtle,
    outlineVariant = TextMuted.copy(alpha = 0.3f),
    error = ErrorRed,
    onError = Color.White
)

@Composable
fun MusicProTheme(
    content: @Composable () -> Unit
) {
    // Force le thème sombre exclusif MusicPro
    MaterialTheme(
        colorScheme = MusicProDarkColorScheme,
        typography = Typography,
        content = content
    )
}
