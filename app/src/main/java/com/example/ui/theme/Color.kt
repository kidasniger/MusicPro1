package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Couleurs de référence par défaut
val DefaultBackgroundDark = Color(0xFF0A0A0C)
val DefaultCanvasDark = Color(0xFF050507)
val DefaultSurfaceDark = Color(0xFF111113)
val DefaultSurfaceCard = Color(0xFF121214)
val DefaultSurfacePurpleTint = Color(0xFF150B29)
val DefaultSurfaceElevated = Color(0xFF1A1A1F)
val DefaultSurfaceAlt = Color(0xFF1C1C1F)

val DefaultCyanAccent = Color(0xFF22D3EE)
val DefaultPurpleAccent = Color(0xFFA855F7)
val DefaultPurpleDeep = Color(0xFF5B21B6)
val DefaultPurpleLight = Color(0xFFC4B5FD)

val DefaultTextPrimary = Color(0xFFF9FAFB)
val DefaultTextSecondary = Color(0xFF9CA3AF)
val DefaultTextMuted = Color(0xFF6B7280)
val DefaultBorderSubtle = Color(0x14FFFFFF)

// Couleurs dynamiques selon le thème Material 3 actif
val BackgroundDark: Color
    @Composable get() = MaterialTheme.colorScheme.background

val CanvasDark: Color
    @Composable get() = MaterialTheme.colorScheme.background

val SurfaceDark: Color
    @Composable get() = MaterialTheme.colorScheme.surfaceVariant

val SurfaceCard: Color
    @Composable get() = MaterialTheme.colorScheme.surface

val SurfacePurpleTint: Color
    @Composable get() = MaterialTheme.colorScheme.secondaryContainer

val SurfaceElevated: Color
    @Composable get() = MaterialTheme.colorScheme.surfaceVariant

val SurfaceAlt: Color
    @Composable get() = MaterialTheme.colorScheme.surface

// Couleurs d'accent signature
val CyanAccent: Color
    @Composable get() = MaterialTheme.colorScheme.primary

val PurpleAccent: Color
    @Composable get() = MaterialTheme.colorScheme.secondary

val PurpleDeep: Color
    @Composable get() = MaterialTheme.colorScheme.primaryContainer

val PurpleLight: Color
    @Composable get() = MaterialTheme.colorScheme.onPrimaryContainer

// Accents fonctionnels
val AmberAccent = Color(0xFFF59E0B)
val AmberLight = Color(0xFFFCD34D)
val HeartPink = Color(0xFFFF4D6D)
val ErrorRed = Color(0xFFEF4444)
val SuccessGreen = Color(0xFF10B981)

// Couleurs de texte et de contenu
val TextPrimary: Color
    @Composable get() = MaterialTheme.colorScheme.onBackground

val TextSecondary: Color
    @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant

val TextMuted: Color
    @Composable get() = MaterialTheme.colorScheme.outlineVariant

val PureWhite = Color(0xFFFFFFFF)

// Bordures et transparences
val BorderSubtle: Color
    @Composable get() = MaterialTheme.colorScheme.outline

val BorderHighlight: Color
    @Composable get() = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)

val SurfaceGlass: Color
    @Composable get() = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)

// Pinceaux dégradés signatures
val MusicProGradient: Brush
    @Composable get() = Brush.linearGradient(
        colors = listOf(CyanAccent, PurpleAccent)
    )

val MusicProDeepGradient: Brush
    @Composable get() = Brush.linearGradient(
        colors = listOf(SurfacePurpleTint, BackgroundDark)
    )

val CyanHaloGradient: Brush
    @Composable get() = Brush.radialGradient(
        colors = listOf(CyanAccent.copy(alpha = 0.35f), Color.Transparent)
    )

val PurpleHaloGradient: Brush
    @Composable get() = Brush.radialGradient(
        colors = listOf(PurpleAccent.copy(alpha = 0.35f), Color.Transparent)
    )
