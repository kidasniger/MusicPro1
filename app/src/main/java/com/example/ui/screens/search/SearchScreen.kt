package com.example.ui.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.TrackEntity
import com.example.ui.components.EmptyStateComposable
import com.example.ui.components.EmptyStateType
import com.example.ui.components.MiniPlayer
import com.example.ui.screens.home.TrackListItem
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun SearchScreen(
    tracks: List<TrackEntity>,
    activeTrack: TrackEntity?,
    isPlaying: Boolean,
    currentPositionMs: Long,
    durationMs: Long,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onTrackSelected: (TrackEntity) -> Unit,
    onTogglePlayPause: () -> Unit,
    onNext: () -> Unit,
    onToggleFavorite: (TrackEntity) -> Unit,
    onNavigateToPlayer: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf("Tout") }
    val filters = listOf("Tout", "Titres", "Artistes", "Albums", "Paroles")

    val filteredTracks = remember(tracks, searchQuery, selectedFilter) {
        if (searchQuery.isBlank()) {
            tracks
        } else {
            val q = searchQuery.trim().lowercase()
            tracks.filter { track ->
                when (selectedFilter) {
                    "Titres" -> track.title.lowercase().contains(q)
                    "Artistes" -> track.artist.lowercase().contains(q)
                    "Albums" -> track.album.lowercase().contains(q)
                    else -> track.title.lowercase().contains(q) ||
                            track.artist.lowercase().contains(q) ||
                            track.album.lowercase().contains(q)
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .testTag("search_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Barre de recherche en temps réel
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceDark)
                        .testTag("search_text_field"),
                    placeholder = {
                        Text(
                            text = "Titre, artiste, paroles...",
                            color = TextMuted,
                            fontSize = 14.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Recherche",
                            tint = CyanAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Effacer",
                                    tint = TextMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = CyanAccent,
                        unfocusedBorderColor = BorderSubtle,
                        cursorColor = CyanAccent
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
            }

            // Filtres de recherche (Tout, Titres, Artistes, Albums, Paroles)
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                items(filters) { filter ->
                    val isSelected = filter == selectedFilter
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
                            .clickable { selectedFilter = filter }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                            .testTag("search_filter_$filter")
                    ) {
                        Text(
                            text = filter,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                            color = if (isSelected) Color.White else TextSecondary
                        )
                    }
                }
            }

            // Compteur de résultats
            Text(
                text = "${filteredTracks.size} RÉSULTATS DANS LA BIBLIOTHÈQUE LOCALE",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                color = TextMuted,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
            )

            // Liste des résultats
            if (filteredTracks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyStateComposable(
                        type = EmptyStateType.SEARCH,
                        onPrimaryAction = { onSearchQueryChange("") }
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(
                        start = 20.dp,
                        end = 20.dp,
                        top = 8.dp,
                        bottom = 96.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(filteredTracks, key = { it.id }) { track ->
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

        // MiniPlayer flottant
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
