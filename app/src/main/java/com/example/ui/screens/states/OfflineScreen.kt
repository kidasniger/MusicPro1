package com.example.ui.screens.states

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.PurpleDeep
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.SurfaceGlass
import com.example.ui.theme.SurfacePurpleTint
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.MainViewModel

/**
 * Écran d'état Hors-ligne dédié (id="offline"), conforme à la maquette.
 * Présente de manière exhaustive le statut réseau, la simulation du mode hors-ligne,
 * les fonctionnalités locales 100% fonctionnelles et les fonctionnalités Cloud désactivées.
 */
@Composable
fun OfflineScreen(
    viewModel: MainViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isOffline by viewModel.isOffline.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .statusBarsPadding()
            .testTag("offline_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(SurfaceGlass)
                            .testTag("offline_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Retour",
                            tint = TextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Mode Hors-ligne",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                        Text(
                            text = "Disponibilité des services MusicPro",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }

                // Badge actuel
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(9999.dp))
                        .background(
                            if (isOffline) AmberAccent.copy(alpha = 0.15f) else SuccessGreen.copy(alpha = 0.15f)
                        )
                        .border(
                            1.dp,
                            if (isOffline) AmberAccent.copy(alpha = 0.40f) else SuccessGreen.copy(alpha = 0.40f),
                            RoundedCornerShape(9999.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isOffline) "HORS-LIGNE" else "EN LIGNE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = if (isOffline) AmberAccent else SuccessGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Carte de statut principal
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(SurfacePurpleTint)
                    .border(
                        1.5.dp,
                        if (isOffline) AmberAccent.copy(alpha = 0.35f) else PurpleDeep.copy(alpha = 0.40f),
                        RoundedCornerShape(24.dp)
                    )
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        if (isOffline) AmberAccent.copy(alpha = 0.20f) else CyanAccent.copy(alpha = 0.20f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isOffline) Icons.Default.WifiOff else Icons.Default.Wifi,
                                    contentDescription = null,
                                    tint = if (isOffline) AmberAccent else CyanAccent,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = if (isOffline) "Statut : Déconnecté" else "Statut : Connecté à Internet",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = if (isOffline)
                                        "Vos fichiers locaux restent 100% lisibles"
                                    else
                                        "Synchronisation LRCLIB et Groq IA actives",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Indicateur de statut réseau réel
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(SurfaceDark)
                            .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isOffline) "Appareil actuellement hors-ligne" else "Connecté au réseau (${viewModel.getNetworkTypeName()})",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isOffline) AmberAccent else SuccessGreen
                            )
                            Text(
                                text = if (isOffline) "La lecture locale fonctionne à 100%. Les services cloud (LRCLIB, IA) nécessitent une connexion." else "Recherche de paroles et IA prêtes.",
                                fontSize = 11.sp,
                                color = TextMuted,
                                lineHeight = 16.sp
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(if (isOffline) AmberAccent else SuccessGreen)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Section 1 : Ce qui fonctionne 100% hors-ligne
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = SuccessGreen,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "FONCTIONS 100% HORS-LIGNE (LOCALES)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.2.sp,
                    color = SuccessGreen
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            OfflineFeatureCard(
                icon = Icons.Default.LibraryMusic,
                title = "Décodage Audio Haute-Fidélité",
                description = "Lecture sans latence de vos fichiers stockés sur le téléphone (FLAC 24-bit, MP3 320kbps, WAV, AAC, OGG).",
                isAvailable = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            OfflineFeatureCard(
                icon = Icons.Default.Equalizer,
                title = "Égaliseur 5 Bandes & Bass Boost",
                description = "Tous les préréglages audio (Rock, Bass, Pop, Jazz) et réverbération spatiale sont traités en local par votre processeur.",
                isAvailable = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            OfflineFeatureCard(
                icon = Icons.Default.QueueMusic,
                title = "Playlists, Favoris & Historique",
                description = "Gestion complète de vos listes de lecture, file d'attente et favoris conservés dans la base SQLite locale.",
                isAvailable = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            OfflineFeatureCard(
                icon = Icons.Default.Subtitles,
                title = "Paroles en Cache Local",
                description = "Toutes les paroles déjà téléchargées ou affichées restent lisibles et synchronisées hors connexion.",
                isAvailable = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Section 2 : Fonctions désactivées hors-ligne
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = AmberAccent,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "SERVICES NÉCESSITANT UNE CONNEXION INTERNET",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.2.sp,
                    color = AmberAccent
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            OfflineFeatureCard(
                icon = Icons.Default.CloudOff,
                title = "Recherche LRCLIB Cloud",
                description = "Le téléchargement automatique de nouvelles paroles synchronisées pour des morceaux jamais scannés est suspendu.",
                isAvailable = false
            )

            Spacer(modifier = Modifier.height(8.dp))

            OfflineFeatureCard(
                icon = Icons.Default.AutoAwesome,
                title = "Transcription Groq Whisper IA",
                description = "L'envoi de segments audio vers l'API Cloud Groq requiert un accès réseau. Vous serez notifié en cas d'appel.",
                isAvailable = false
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Bouton retour à la musique locale
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.horizontalGradient(listOf(CyanAccent, PurpleAccent))
                    )
                    .clickable { onBackClick() }
                    .padding(vertical = 16.dp)
                    .testTag("offline_return_button"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "ACCÉDER À MA MUSIQUE LOCALE",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    color = BackgroundDark
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun OfflineFeatureCard(
    icon: ImageVector,
    title: String,
    description: String,
    isAvailable: Boolean
) {
    val accent = if (isAvailable) SuccessGreen else AmberAccent

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceCard)
            .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(accent.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(20.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = if (isAvailable) "ACTIF" else "DÉSACTIVÉ",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.8.sp,
                    color = accent
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 18.sp
            )
        }
    }
}
