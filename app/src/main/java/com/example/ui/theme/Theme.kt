package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// 1. Thème Dark Cyberpunk (Signature MusicPro)
private val CyberpunkDarkColorScheme = darkColorScheme(
    primary = DefaultCyanAccent,
    onPrimary = Color.Black,
    primaryContainer = DefaultPurpleDeep,
    onPrimaryContainer = DefaultPurpleLight,
    secondary = DefaultPurpleAccent,
    onSecondary = Color.White,
    secondaryContainer = DefaultSurfacePurpleTint,
    onSecondaryContainer = DefaultPurpleLight,
    tertiary = AmberAccent,
    onTertiary = Color.Black,
    background = DefaultBackgroundDark,
    onBackground = DefaultTextPrimary,
    surface = DefaultSurfaceCard,
    onSurface = DefaultTextPrimary,
    surfaceVariant = DefaultSurfaceElevated,
    onSurfaceVariant = DefaultTextSecondary,
    surfaceTint = DefaultCyanAccent,
    outline = DefaultBorderSubtle,
    outlineVariant = DefaultTextMuted.copy(alpha = 0.3f),
    error = ErrorRed,
    onError = Color.White
)

// 2. Thème OLED Pure Black (Noir pur 100% contraste et économie d'énergie)
private val OledPureBlackColorScheme = darkColorScheme(
    primary = Color(0xFF00F5FF),
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF111111),
    onPrimaryContainer = Color(0xFFE0E0E0),
    secondary = Color(0xFF10B981),
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF161616),
    onSecondaryContainer = Color(0xFFA7F3D0),
    tertiary = AmberAccent,
    onTertiary = Color.Black,
    background = Color(0xFF000000),
    onBackground = Color(0xFFFFFFFF),
    surface = Color(0xFF080808),
    onSurface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFF121212),
    onSurfaceVariant = Color(0xFF9E9E9E),
    surfaceTint = Color(0xFF00F5FF),
    outline = Color(0x1FFFFFFF),
    outlineVariant = Color(0x33FFFFFF),
    error = ErrorRed,
    onError = Color.White
)

// 3. Thème Deep Astral Purple (Cosmique violet profond)
private val DeepAstralPurpleColorScheme = darkColorScheme(
    primary = Color(0xFFC084FC),
    onPrimary = Color(0xFF1E0E38),
    primaryContainer = Color(0xFF3B166A),
    onPrimaryContainer = Color(0xFFF3E8FF),
    secondary = Color(0xFF818CF8),
    onSecondary = Color(0xFF0E1338),
    secondaryContainer = Color(0xFF26184C),
    onSecondaryContainer = Color(0xFFE0E7FF),
    tertiary = Color(0xFFF472B6),
    onTertiary = Color.Black,
    background = Color(0xFF0D0718),
    onBackground = Color(0xFFF5F3FF),
    surface = Color(0xFF180F2B),
    onSurface = Color(0xFFF5F3FF),
    surfaceVariant = Color(0xFF24163E),
    onSurfaceVariant = Color(0xFFC4B5FD),
    surfaceTint = Color(0xFFC084FC),
    outline = Color(0x29C084FC),
    outlineVariant = Color(0x40C084FC),
    error = ErrorRed,
    onError = Color.White
)

@Composable
fun MusicProTheme(
    themeMode: String = "Dark Cyberpunk",
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeMode) {
        "OLED Pure Black" -> OledPureBlackColorScheme
        "Deep Astral Purple" -> DeepAstralPurpleColorScheme
        else -> CyberpunkDarkColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
