package com.example.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.TrackEntity
import com.example.ui.components.EmptyStateComposable
import com.example.ui.components.EmptyStateType
import com.example.ui.components.MiniPlayer
import com.example.ui.components.MusicProLogo
import com.example.ui.components.OfflineBanner
import com.example.ui.components.TrackCoverArt
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.HeartPink
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun HomeScreen(
    tracks: List<TrackEntity>,
    activeTrack: TrackEntity?,
    isPlaying: Boolean,
    currentPositionMs: Long,
    durationMs: Long,
    onTrackSelected: (TrackEntity) -> Unit,
    onTogglePlayPause: () -> Unit,
    onNext: () -> Unit,
    onToggleFavorite: (TrackEntity) -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToPlayer: () -> Unit,
    onNavigateToFavorites: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {},
    onNavigateToBluetooth: () -> Unit = {},
    onNavigateToArtists: () -> Unit = {},
    onNavigateToAlbums: () -> Unit = {},
    isOffline: Boolean = false,
    onNavigateToOffline: () -> Unit = {},
    onRescanLibrary: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf("Titres") }
    val categories = listOf("Titres", "Artistes", "Albums", "Dossiers", "Favoris")

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .testTag("home_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // En-tête : Logo MusicPro + actions (Recherche, Rafraîchir)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MusicProLogo(
                    size = 36.dp,
                    withText = true
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Bouton Favoris
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(SurfaceDark)
                            .border(1.dp, BorderSubtle, CircleShape)
                            .clickable { onNavigateToFavorites() }
                            .testTag("home_fav_icon_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Favoris",
                            tint = HeartPink,
                            modifier = Modifier.size(17.dp)
                        )
                    }

                    // Bouton Bluetooth / Casque
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(SurfaceDark)
                            .border(1.dp, BorderSubtle, CircleShape)
                            .clickable { onNavigateToBluetooth() }
                            .testTag("home_bt_icon_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bluetooth,
                            contentDescription = "Casque Bluetooth",
                            tint = PurpleAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Bouton Historique
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(SurfaceDark)
                            .border(1.dp, BorderSubtle, CircleShape)
                            .clickable { onNavigateToHistory() }
                            .testTag("home_history_icon_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "Historique",
                            tint = CyanAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Bouton recherche rapide
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(SurfaceDark)
                            .border(1.dp, BorderSubtle, CircleShape)
                            .clickable { onNavigateToSearch() }
                            .testTag("home_search_icon_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Recherche",
                            tint = Color.White.copy(alpha = 0.85f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Bannière Mode Hors-ligne si actif
            OfflineBanner(
                isOffline = isOffline,
                onBannerClick = onNavigateToOffline,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 2.dp)
            )

            // Onglets / Filtres de catégories (Titres, Artistes, Albums...)
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                items(categories) { cat ->
                    val isSelected = cat == selectedCategory
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(9999.dp))
                            .background(
                                if (isSelected) {
                                    Brush.horizontalGradient(listOf(CyanAccent, PurpleAccent))
                                } else {
                                    Brush.linearGradient(listOf(SurfaceDark, SurfaceDark))
                                }
                            )
                            .border(
                                width = 1.dp,
                                color = if (isSelected) Color.Transparent else BorderSubtle,
                                shape = RoundedCornerShape(9999.dp)
                            )
                            .clickable {
                                when (cat) {
                                    "Artistes" -> onNavigateToArtists()
                                    "Albums" -> onNavigateToAlbums()
                                    else -> selectedCategory = cat
                                }
                            }
                            .padding(horizontal = 16.dp, vertical = 7.dp)
                            .testTag("category_tab_$cat")
                    ) {
                        Text(
                            text = cat,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                            color = if (isSelected) Color.White else TextSecondary
                        )
                    }
                }
            }

            // Bannière d'action rapide : Nombre de pistes + Lecture Aléatoire
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${tracks.size} TITRES • FLAC / MP3",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    color = TextMuted
                )

                // Bouton Aléatoire
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(SurfaceDark)
                        .border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                        .clickable {
                            if (tracks.isNotEmpty()) {
                                onTrackSelected(tracks.random())
                            }
                        }
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                        .testTag("shuffle_all_button"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Shuffle,
                        contentDescription = "Lecture aléatoire",
                        tint = CyanAccent,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Aléatoire",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            // Liste principale des morceaux ou État vide partagé
            val displayedTracks = remember(tracks, selectedCategory) {
                when (selectedCategory) {
                    "Favoris" -> tracks.filter { it.isFavorite }
                    else -> tracks
                }
            }

            if (tracks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyStateComposable(
                        type = EmptyStateType.LIBRARY,
                        onPrimaryAction = onRescanLibrary
                    )
                }
            } else if (displayedTracks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyStateComposable(
                        type = if (selectedCategory == "Favoris") EmptyStateType.FAVORITES else EmptyStateType.SEARCH,
                        customSubtitle = "Aucun titre trouvé dans la catégorie « $selectedCategory ».",
                        onPrimaryAction = { selectedCategory = "Titres" }
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(
                        start = 20.dp,
                        end = 20.dp,
                        top = 4.dp,
                        bottom = 96.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .testTag("tracks_list")
                ) {
                    items(displayedTracks, key = { it.id }) { track ->
                        val isActive = activeTrack?.id == track.id
                        TrackListItem(
                            track = track,
                            isActive = isActive,
                            isPlaying = isPlaying && isActive,
                            onTrackClick = { onTrackSelected(track) },
                            onToggleFavorite = { onToggleFavorite(track) }
                        )
                    }
                }
            }
        }

        // MiniPlayer flottant au-dessus de la BottomBar
        if (activeTrack != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 6.dp)
            ) {
                MiniPlayer(
                    activeTrack = activeTrack,
                    isPlaying = isPlaying,
                    currentPositionMs = currentPositionMs,
                    durationMs = durationMs,
                    onTogglePlayPause = onTogglePlayPause,
                    onNext = onNext,
                    onExpand = onNavigateToPlayer
                )
            }
        }
    }
}

@Composable
fun TrackListItem(
    track: TrackEntity,
    isActive: Boolean,
    isPlaying: Boolean,
    onTrackClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(
                if (isActive) SurfaceElevated else SurfaceCard
            )
            .border(
                width = 1.dp,
                color = if (isActive) CyanAccent.copy(alpha = 0.40f) else BorderSubtle,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onTrackClick() }
            .padding(10.dp)
            .testTag("track_item_${track.id}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Pochette avec gradient
        Box(contentAlignment = Alignment.Center) {
            TrackCoverArt(
                gradientStr = track.coverGradient,
                title = track.title,
                coverArtUri = track.coverArtUri,
                size = 46.dp,
                shape = RoundedCornerShape(10.dp)
            )
            if (isActive) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.Black.copy(alpha = 0.35f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Equalizer else Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = CyanAccent,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Informations Titre & Artiste & Badges
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = track.title,
                fontSize = 14.sp,
                fontWeight = if (isActive) FontWeight.Black else FontWeight.Bold,
                color = if (isActive) CyanAccent else TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(top = 3.dp)
            ) {
                Text(
                    text = track.artist,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "•",
                    fontSize = 10.sp,
                    color = TextMuted
                )

                // Format & Bitrate
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.White.copy(alpha = 0.06f))
                        .padding(horizontal = 5.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = track.format,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (track.format == "FLAC") PurpleAccent else TextMuted
                    )
                }
            }
        }

        // Durée
        Text(
            text = track.durationFormatted,
            fontSize = 12.sp,
            color = TextMuted,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        // Bouton favori
        IconButton(
            onClick = onToggleFavorite,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = if (track.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = "Favori",
                tint = if (track.isFavorite) HeartPink else TextMuted,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
