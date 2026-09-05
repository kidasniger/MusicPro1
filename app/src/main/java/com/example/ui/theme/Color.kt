package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Couleurs de fond principales (Charte MusicPro)
val BackgroundDark = Color(0xFF0A0A0C)       // Fond sombre principal de l'application
val CanvasDark = Color(0xFF050507)           // Fond noir profond (container / viewport)
val SurfaceDark = Color(0xFF111113)          // Surface secondaire (cartes / widgets)
val SurfaceCard = Color(0xFF121214)          // Surface cartes standard
val SurfacePurpleTint = Color(0xFF150B29)    // Fond teinté violet sombre (hero / halos)
val SurfaceElevated = Color(0xFF1A1A1F)      // Éléments surélevés / inputs
val SurfaceAlt = Color(0xFF1C1C1F)           // Surfaces contrastées (widgets)

// Couleurs d'accent signature (Gradient Cyan -> Violet)
val CyanAccent = Color(0xFF22D3EE)           // Cyan signature MusicPro (#22D3EE)
val PurpleAccent = Color(0xFFA855F7)         // Violet vif (#A855F7)
val PurpleDeep = Color(0xFF5B21B6)           // Violet profond (#5B21B6)
val PurpleLight = Color(0xFFC4B5FD)          // Lavande clair (#C4B5FD)

// Accents fonctionnels
val AmberAccent = Color(0xFFF59E0B)          // Groq / avertissements / hors ligne (#F59E0B)
val AmberLight = Color(0xFFFCD34D)           // Amber clair
val HeartPink = Color(0xFFFF4D6D)            // Favoris / like (#FF4D6D)
val ErrorRed = Color(0xFFEF4444)             // Alertes & erreurs (#EF4444)
val SuccessGreen = Color(0xFF10B981)         // Succès / statut connecté (#10B981)

// Couleurs de texte et de contenu
val TextPrimary = Color(0xFFF9FAFB)          // Blanc cassé text principal (#F9FAFB)
val TextSecondary = Color(0xFF9CA3AF)        // Gris moyen (#9CA3AF)
val TextMuted = Color(0xFF6B7280)            // Gris discret / tags / sous-titres (#6B7280)
val PureWhite = Color(0xFFFFFFFF)

// Bordures et transparences
val BorderSubtle = Color(0x0FFFFFFF)         // rgba(255, 255, 255, 0.06)
val BorderHighlight = Color(0x1A22D3EE)      // rgba(34, 211, 238, 0.1)
val SurfaceGlass = Color(0x0AFFFFFF)         // rgba(255, 255, 255, 0.04)

// Pinceaux dégradés signatures
val MusicProGradient = Brush.linearGradient(
    colors = listOf(CyanAccent, PurpleAccent)
)

val MusicProDeepGradient = Brush.linearGradient(
    colors = listOf(SurfacePurpleTint, BackgroundDark)
)

val CyanHaloGradient = Brush.radialGradient(
    colors = listOf(CyanAccent.copy(alpha = 0.35f), Color.Transparent)
)

val PurpleHaloGradient = Brush.radialGradient(
    colors = listOf(PurpleAccent.copy(alpha = 0.35f), Color.Transparent)
)
