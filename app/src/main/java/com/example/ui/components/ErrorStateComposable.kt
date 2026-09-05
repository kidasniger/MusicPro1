package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

enum class ErrorVariant {
    NETWORK_LRCLIB,
    GROQ_UNAVAILABLE,
    AUDIO_PLAYBACK,
    GENERIC
}

/**
 * Composant d'état d'erreur réutilisable, conforme à la maquette (id="errorStates").
 * Variantes d'erreurs réseau (LRCLIB, Groq indisponible), erreur de lecture fichier, etc. avec bouton "Réessayer".
 */
@Composable
fun ErrorStateComposable(
    variant: ErrorVariant,
    modifier: Modifier = Modifier,
    customTitle: String? = null,
    customMessage: String? = null,
    onRetry: () -> Unit = {},
    secondaryActionLabel: String? = null,
    onSecondaryAction: (() -> Unit)? = null
) {
    val title = customTitle ?: when (variant) {
        ErrorVariant.NETWORK_LRCLIB -> "Connexion à LRCLIB impossible"
        ErrorVariant.GROQ_UNAVAILABLE -> "Service Groq IA indisponible"
        ErrorVariant.AUDIO_PLAYBACK -> "Erreur de lecture audio"
        ErrorVariant.GENERIC -> "Une erreur inattendue est survenue"
    }

    val message = customMessage ?: when (variant) {
        ErrorVariant.NETWORK_LRCLIB ->
            "Impossible de joindre la base de données de paroles synchronisées. Vérifiez votre connexion internet ou réessayez dans un instant."
        ErrorVariant.GROQ_UNAVAILABLE ->
            "La transcription audio par IA n'a pas pu aboutir. Vérifiez la configuration de votre clé API Groq ou vos quotas d'accès."
        ErrorVariant.AUDIO_PLAYBACK ->
            "Impossible de décoder ce fichier audio. Le fichier est peut-être déplacé, corrompu ou son format n'est pas supporté par le lecteur."
        ErrorVariant.GENERIC ->
            "L'opération demandée a échoué. Veuillez vérifier l'état du système et réessayer."
    }

    val icon: ImageVector = when (variant) {
        ErrorVariant.NETWORK_LRCLIB -> Icons.Default.CloudOff
        ErrorVariant.GROQ_UNAVAILABLE -> Icons.Default.AutoAwesome
        ErrorVariant.AUDIO_PLAYBACK -> Icons.Default.ErrorOutline
        ErrorVariant.GENERIC -> Icons.Default.Warning
    }

    val accentColor = when (variant) {
        ErrorVariant.NETWORK_LRCLIB -> AmberAccent
        ErrorVariant.GROQ_UNAVAILABLE -> AmberAccent
        ErrorVariant.AUDIO_PLAYBACK -> ErrorRed
        ErrorVariant.GENERIC -> ErrorRed
    }

    val badgeLabel = when (variant) {
        ErrorVariant.NETWORK_LRCLIB -> "SERVEUR DISTANT • TIMEOUT"
        ErrorVariant.GROQ_UNAVAILABLE -> "GROQ WHISPER • ÉCHEC API"
        ErrorVariant.AUDIO_PLAYBACK -> "DÉCODEUR • FICHIER ILLISIBLE"
        ErrorVariant.GENERIC -> "ERREUR SYSTÈME"
    }

    val effectiveSecondaryLabel = secondaryActionLabel ?: when (variant) {
        ErrorVariant.NETWORK_LRCLIB -> "Basculer en mode hors-ligne"
        ErrorVariant.GROQ_UNAVAILABLE -> "Configurer la clé API Groq"
        ErrorVariant.AUDIO_PLAYBACK -> "Passer au morceau suivant"
        ErrorVariant.GENERIC -> null
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 20.dp)
            .testTag("error_state_${variant.name.lowercase()}"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icône dans container lumineux
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(accentColor.copy(alpha = 0.12f))
                .border(1.5.dp, accentColor.copy(alpha = 0.40f), RoundedCornerShape(28.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(46.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Badge
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(9999.dp))
                .background(accentColor.copy(alpha = 0.12f))
                .border(1.dp, accentColor.copy(alpha = 0.30f), RoundedCornerShape(9999.dp))
                .padding(horizontal = 12.dp, vertical = 5.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(accentColor)
                )
                Text(
                    text = badgeLabel,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = accentColor
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Titre
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            color = TextPrimary,
            textAlign = TextAlign.Center,
            letterSpacing = (-0.3).sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Message
        Text(
            text = message,
            fontSize = 13.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Bouton Réessayer (Principal)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(accentColor, PurpleAccent)
                    )
                )
                .clickable { onRetry() }
                .padding(vertical = 16.dp)
                .testTag("error_retry_button"),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    tint = BackgroundDark,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "RÉESSAYER",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    color = BackgroundDark
                )
            }
        }

        // Action secondaire si spécifiée ou par défaut
        if (effectiveSecondaryLabel != null && onSecondaryAction != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceElevated)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
                    .clickable { onSecondaryAction() }
                    .padding(vertical = 14.dp)
                    .testTag("error_secondary_button"),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    when (variant) {
                        ErrorVariant.GROQ_UNAVAILABLE -> {
                            Icon(
                                imageVector = Icons.Default.Key,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        ErrorVariant.AUDIO_PLAYBACK -> {
                            Icon(
                                imageVector = Icons.Default.SkipNext,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        ErrorVariant.NETWORK_LRCLIB -> {
                            Icon(
                                imageVector = Icons.Default.WifiOff,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        else -> {}
                    }
                    Text(
                        text = effectiveSecondaryLabel,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                }
            }
        }
    }
}
