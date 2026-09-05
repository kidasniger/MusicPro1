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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shuffle
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
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.HeartPink
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.PurpleDeep
import com.example.ui.theme.PurpleLight
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.SurfacePurpleTint
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

enum class EmptyStateType {
    LIBRARY,
    SEARCH,
    FAVORITES,
    QUEUE,
    PLAYLISTS,
    LYRICS,
    HISTORY,
    CUSTOM
}

/**
 * Composant d'état vide unifié et paramétrable, conforme à la maquette (id="emptyStates").
 * Centralise bibliothèque vide, recherche vide, favoris vide, file vide, paroles vide (lyricsEmpty) et historique vide.
 */
@Composable
fun EmptyStateComposable(
    type: EmptyStateType,
    modifier: Modifier = Modifier,
    customTitle: String? = null,
    customSubtitle: String? = null,
    customTag: String? = null,
    customIcon: ImageVector? = null,
    customAccentColor: Color? = null,
    primaryActionLabel: String? = null,
    onPrimaryAction: (() -> Unit)? = null,
    secondaryActionLabel: String? = null,
    onSecondaryAction: (() -> Unit)? = null,
    tertiaryActionLabel: String? = null,
    onTertiaryAction: (() -> Unit)? = null
) {
    val title = customTitle ?: when (type) {
        EmptyStateType.LIBRARY -> "Bibliothèque musicale vide"
        EmptyStateType.SEARCH -> "Aucun résultat trouvé"
        EmptyStateType.FAVORITES -> "Aucun favori pour le moment"
        EmptyStateType.QUEUE -> "File de lecture vide"
        EmptyStateType.PLAYLISTS -> "Aucune playlist créée"
        EmptyStateType.LYRICS -> "Aucune parole automatique disponible"
        EmptyStateType.HISTORY -> "Aucun historique d'écoute"
        EmptyStateType.CUSTOM -> "Aucun élément"
    }

    val subtitle = customSubtitle ?: when (type) {
        EmptyStateType.LIBRARY ->
            "Aucun fichier audio n'a été détecté dans vos dossiers locaux (MP3, FLAC, AAC, WAV, OGG). Ajoutez de la musique sur votre appareil pour commencer."
        EmptyStateType.SEARCH ->
            "Aucun morceau, artiste ou album ne correspond à votre recherche. Vérifiez l'orthographe ou essayez un mot-clé plus court."
        EmptyStateType.FAVORITES ->
            "Touche l'icône cœur sur n'importe quel morceau dans l'Accueil, la Recherche ou le Lecteur pour le retrouver instantanément ici."
        EmptyStateType.QUEUE ->
            "Aucun morceau n'est actuellement en attente de lecture. Choisissez un titre, un album ou lancez une playlist pour démarrer la lecture."
        EmptyStateType.PLAYLISTS ->
            "Créez votre première liste de lecture personnalisée pour organiser vos morceaux selon vos humeurs ou vos activités."
        EmptyStateType.LYRICS ->
            "Nous n'avons pas trouvé de paroles automatiques synchronisées pour ce morceau sur LRCLIB."
        EmptyStateType.HISTORY ->
            "Les morceaux que vous écoutez apparaîtront ici automatiquement avec leur horodatage et vos statistiques de lecture."
        EmptyStateType.CUSTOM ->
            "La liste est actuellement vide."
    }

    val tagText = customTag ?: when (type) {
        EmptyStateType.LIBRARY -> "STOCKAGE LOCAL"
        EmptyStateType.SEARCH -> "RECHERCHE LOCALE"
        EmptyStateType.FAVORITES -> "COLLECTION PERSONNELLE"
        EmptyStateType.QUEUE -> "LECTEUR EN PAUSE"
        EmptyStateType.PLAYLISTS -> "COLLECTION PLAYLISTS"
        EmptyStateType.LYRICS -> "PAROLES SYNCHRONISÉES"
        EmptyStateType.HISTORY -> "SESSION EN COURS"
        EmptyStateType.CUSTOM -> "VIDE"
    }

    val icon = customIcon ?: when (type) {
        EmptyStateType.LIBRARY -> Icons.Default.LibraryMusic
        EmptyStateType.SEARCH -> Icons.Default.Search
        EmptyStateType.FAVORITES -> Icons.Default.FavoriteBorder
        EmptyStateType.QUEUE -> Icons.Default.QueueMusic
        EmptyStateType.PLAYLISTS -> Icons.Default.PlaylistAdd
        EmptyStateType.LYRICS -> Icons.Default.Mic
        EmptyStateType.HISTORY -> Icons.Default.History
        EmptyStateType.CUSTOM -> Icons.Default.MusicNote
    }

    val accentColor = customAccentColor ?: when (type) {
        EmptyStateType.LIBRARY -> CyanAccent
        EmptyStateType.SEARCH -> TextSecondary
        EmptyStateType.FAVORITES -> HeartPink
        EmptyStateType.QUEUE -> PurpleAccent
        EmptyStateType.PLAYLISTS -> CyanAccent
        EmptyStateType.LYRICS -> PurpleLight
        EmptyStateType.HISTORY -> CyanAccent
        EmptyStateType.CUSTOM -> CyanAccent
    }

    val defaultPrimaryLabel = when (type) {
        EmptyStateType.LIBRARY -> "SCANNER LE STOCKAGE"
        EmptyStateType.SEARCH -> "EFFACER LA RECHERCHE"
        EmptyStateType.FAVORITES -> "EXPLORER LA BIBLIOTHÈQUE"
        EmptyStateType.QUEUE -> "LANCER LA LECTURE ALÉATOIRE"
        EmptyStateType.PLAYLISTS -> "CRÉER UNE PLAYLIST"
        EmptyStateType.LYRICS -> "CHOISIR UNE SOURCE"
        EmptyStateType.HISTORY -> "ÉCOUTER DE LA MUSIQUE"
        EmptyStateType.CUSTOM -> "CONTINUER"
    }
    val effectivePrimaryLabel = primaryActionLabel ?: defaultPrimaryLabel

    val effectiveSecondaryLabel = secondaryActionLabel ?: when (type) {
        EmptyStateType.LYRICS -> "Rechercher manuellement sur LRCLIB"
        else -> null
    }

    val effectiveTertiaryLabel = tertiaryActionLabel ?: when (type) {
        EmptyStateType.LYRICS -> "Configurer Groq Whisper AI"
        else -> null
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 20.dp)
            .testTag("empty_state_${type.name.lowercase()}"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Container icône halo
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(
                    if (type == EmptyStateType.FAVORITES) HeartPink.copy(alpha = 0.12f)
                    else SurfacePurpleTint
                )
                .border(
                    width = 1.5.dp,
                    color = if (type == EmptyStateType.FAVORITES) HeartPink.copy(alpha = 0.40f)
                    else PurpleDeep.copy(alpha = 0.40f),
                    shape = RoundedCornerShape(28.dp)
                ),
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

        // Badge de catégorie
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
                    text = tagText,
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

        // Description
        Text(
            text = subtitle,
            fontSize = 13.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Bouton d'action principal
        if (onPrimaryAction != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        when (type) {
                            EmptyStateType.FAVORITES -> Brush.horizontalGradient(listOf(HeartPink, PurpleAccent))
                            else -> Brush.horizontalGradient(listOf(CyanAccent, PurpleAccent))
                        }
                    )
                    .clickable { onPrimaryAction() }
                    .padding(vertical = 16.dp)
                    .testTag("empty_state_primary_button"),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    when (type) {
                        EmptyStateType.PLAYLISTS -> Icon(Icons.Default.Add, null, tint = BackgroundDark, modifier = Modifier.size(18.dp))
                        EmptyStateType.QUEUE -> Icon(Icons.Default.Shuffle, null, tint = BackgroundDark, modifier = Modifier.size(18.dp))
                        EmptyStateType.SEARCH -> Icon(Icons.Default.Clear, null, tint = BackgroundDark, modifier = Modifier.size(18.dp))
                        else -> {}
                    }
                    Text(
                        text = effectivePrimaryLabel,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        color = BackgroundDark
                    )
                }
            }
        }

        // Action secondaire (ex: LRCLIB recherche manuelle pour les paroles)
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
                    .testTag("empty_state_secondary_button"),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = effectiveSecondaryLabel,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                }
            }
        }

        // Action tertiaire (ex: Groq AI config pour les paroles)
        if (effectiveTertiaryLabel != null && onTertiaryAction != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onTertiaryAction() }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
                    .testTag("empty_state_tertiary_button"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(15.dp)
                )
                Text(
                    text = effectiveTertiaryLabel,
                    fontSize = 12.sp,
                    color = TextMuted
                )
            }
        }
    }
}
