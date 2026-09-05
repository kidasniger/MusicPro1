package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.PurpleDeep
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.SurfacePurpleTint
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

enum class PermissionVariant {
    TO_REQUEST,
    GRANTED,
    DENIED,
    PERMANENTLY_DENIED
}

/**
 * Composant d'état de permission réutilisable, conforme à la maquette (id="permStates").
 * Couvre les variantes : accordée / refusée / à redemander.
 */
@Composable
fun PermissionStateComposable(
    variant: PermissionVariant,
    modifier: Modifier = Modifier,
    onRequestPermission: () -> Unit = {},
    onOpenSettings: (() -> Unit)? = null,
    onContinue: () -> Unit = {},
    customTitle: String? = null,
    customDescription: String? = null
) {
    val context = LocalContext.current

    val title = customTitle ?: when (variant) {
        PermissionVariant.TO_REQUEST -> "Accès aux fichiers audio requis"
        PermissionVariant.GRANTED -> "Accès aux fichiers accordé"
        PermissionVariant.DENIED -> "Permission d'accès refusée"
        PermissionVariant.PERMANENTLY_DENIED -> "Autorisation bloquée dans les réglages"
    }

    val description = customDescription ?: when (variant) {
        PermissionVariant.TO_REQUEST ->
            "MusicPro a besoin de l'autorisation d'accès au stockage pour analyser et lire votre musique locale (MP3, FLAC, AAC, WAV, OGG)."
        PermissionVariant.GRANTED ->
            "Votre bibliothèque musicale est synchronisée avec l'appareil. Prêt pour l'écoute locale en haute fidélité."
        PermissionVariant.DENIED ->
            "L'accès au stockage a été refusé. Pour profiter de vos morceaux locaux, activez l'autorisation pour permettre l'indexation audio."
        PermissionVariant.PERMANENTLY_DENIED ->
            "L'accès est actuellement bloqué par le système Android. Ouvrez les Paramètres de l'application pour activer manuellement les autorisations multimédia."
    }

    val badgeText = when (variant) {
        PermissionVariant.TO_REQUEST -> "STOCKAGE LOCAL • REQUIS"
        PermissionVariant.GRANTED -> "SYNCHRONISÉ • ACTIF"
        PermissionVariant.DENIED -> "ACCÈS REFUSÉ"
        PermissionVariant.PERMANENTLY_DENIED -> "ACTION SYSTÈME REQUISE"
    }

    val badgeColor = when (variant) {
        PermissionVariant.TO_REQUEST -> CyanAccent
        PermissionVariant.GRANTED -> SuccessGreen
        PermissionVariant.DENIED, PermissionVariant.PERMANENTLY_DENIED -> ErrorRed
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 20.dp)
            .testTag("permission_state_${variant.name.lowercase()}"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Halo & Container Icône
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(
                    when (variant) {
                        PermissionVariant.TO_REQUEST -> SurfacePurpleTint
                        PermissionVariant.GRANTED -> SuccessGreen.copy(alpha = 0.12f)
                        PermissionVariant.DENIED, PermissionVariant.PERMANENTLY_DENIED -> ErrorRed.copy(alpha = 0.12f)
                    }
                )
                .border(
                    width = 1.5.dp,
                    color = when (variant) {
                        PermissionVariant.TO_REQUEST -> PurpleDeep.copy(alpha = 0.50f)
                        PermissionVariant.GRANTED -> SuccessGreen.copy(alpha = 0.40f)
                        PermissionVariant.DENIED, PermissionVariant.PERMANENTLY_DENIED -> ErrorRed.copy(alpha = 0.40f)
                    },
                    shape = RoundedCornerShape(28.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            when (variant) {
                PermissionVariant.TO_REQUEST -> {
                    Icon(
                        imageVector = Icons.Default.AudioFile,
                        contentDescription = null,
                        tint = CyanAccent,
                        modifier = Modifier.size(46.dp)
                    )
                }
                PermissionVariant.GRANTED -> {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = SuccessGreen,
                        modifier = Modifier.size(46.dp)
                    )
                }
                PermissionVariant.DENIED, PermissionVariant.PERMANENTLY_DENIED -> {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = ErrorRed,
                        modifier = Modifier.size(46.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Badge d'état
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(9999.dp))
                .background(badgeColor.copy(alpha = 0.12f))
                .border(1.dp, badgeColor.copy(alpha = 0.30f), RoundedCornerShape(9999.dp))
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
                        .background(badgeColor)
                )
                Text(
                    text = badgeText,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = badgeColor
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

        // Description
        Text(
            text = description,
            fontSize = 13.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Actions
        when (variant) {
            PermissionVariant.TO_REQUEST -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.horizontalGradient(listOf(CyanAccent, PurpleAccent))
                        )
                        .clickable { onRequestPermission() }
                        .padding(vertical = 16.dp)
                        .testTag("perm_grant_action_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "AUTORISER L'ACCÈS",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        color = BackgroundDark
                    )
                }
            }

            PermissionVariant.GRANTED -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.horizontalGradient(listOf(SuccessGreen, CyanAccent))
                        )
                        .clickable { onContinue() }
                        .padding(vertical = 16.dp)
                        .testTag("perm_continue_action_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "PARCOURIR LA MUSIQUE",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        color = BackgroundDark
                    )
                }
            }

            PermissionVariant.DENIED, PermissionVariant.PERMANENTLY_DENIED -> {
                // Bouton Paramètres Système
                val effectiveOpenSettings = onOpenSettings ?: {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(intent)
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.horizontalGradient(listOf(ErrorRed, PurpleAccent))
                        )
                        .clickable { effectiveOpenSettings() }
                        .padding(vertical = 16.dp)
                        .testTag("perm_open_settings_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "OUVRIR LES PARAMÈTRES ANDROID",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Bouton Réessayer
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceElevated)
                        .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
                        .clickable { onRequestPermission() }
                        .padding(vertical = 14.dp)
                        .testTag("perm_retry_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Réessayer la demande",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary
                        )
                    }
                }
            }
        }
    }
}
