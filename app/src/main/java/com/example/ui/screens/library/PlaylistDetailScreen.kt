package com.example.ui.screens.library

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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.PlaylistEntity
import com.example.data.local.entities.TrackEntity
import com.example.ui.components.EmptyStateComposable
import com.example.ui.components.EmptyStateType
import com.example.ui.components.MiniPlayer
import com.example.ui.components.TrackCoverArt
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.SurfaceGlass
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistDetailScreen(
    playlistId: Long,
    viewModel: MainViewModel,
    onBackClick: () -> Unit,
    onNavigateToPlayer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val playlists by viewModel.allPlaylists.collectAsState()
    val allTracks by viewModel.allTracks.collectAsState()
    val activeTrack by viewModel.activeTrack.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val currentPositionMs by viewModel.currentPositionMs.collectAsState()
    val durationMs by viewModel.durationMs.collectAsState()

    val playlist = playlists.find { it.id == playlistId } ?: PlaylistEntity(id = playlistId, name = "Playlist")

    // Pistes de la playlist
    val playlistTracksFlow = remember(playlistId) { viewModel.getTracksForPlaylist(playlistId) }
    val playlistTracks by playlistTracksFlow.collectAsState(initial = emptyList())

    var showAddTrackSheet by remember { mutableStateOf(false) }
    val addTrackSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .statusBarsPadding()
            .testTag("playlist_detail_screen")
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(SurfaceGlass)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Retour",
                        tint = TextPrimary
                    )
                }

                // Bouton Ajouter des morceaux
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(CyanAccent.copy(alpha = 0.15f))
                        .border(1.dp, CyanAccent.copy(alpha = 0.40f), RoundedCornerShape(10.dp))
                        .clickable { showAddTrackSheet = true }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("add_tracks_to_playlist_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = CyanAccent,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Ajouter",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyanAccent
                        )
                    }
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(
                    start = 20.dp,
                    end = 20.dp,
                    top = 8.dp,
                    bottom = 110.dp
                ),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // Bannière d'en-tête de la playlist
                item(key = "playlist_banner") {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(SurfaceDark)
                            .border(1.dp, BorderSubtle, RoundedCornerShape(24.dp))
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(CyanAccent, PurpleAccent)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.QueueMusic,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(44.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = playlist.name,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "${playlistTracks.size} titre${if (playlistTracks.size > 1) "s" else ""}",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )

                        if (playlistTracks.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Bouton Tout lire
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            Brush.horizontalGradient(
                                                listOf(CyanAccent, PurpleAccent)
                                            )
                                        )
                                        .clickable {
                                            viewModel.playTrack(playlistTracks.first())
                                        }
                                        .padding(vertical = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PlayArrow,
                                            contentDescription = null,
                                            tint = BackgroundDark,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            text = "Tout lire",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = BackgroundDark
                                        )
                                    }
                                }

                                // Bouton Aléatoire
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(SurfaceElevated)
                                        .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
                                        .clickable {
                                            viewModel.playTrack(playlistTracks.random())
                                        }
                                        .padding(vertical = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Shuffle,
                                            contentDescription = null,
                                            tint = CyanAccent,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = "Aléatoire",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Titre section liste
                item(key = "track_list_header") {
                    Text(
                        text = "MORCEAUX (${playlistTracks.size})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                        color = TextMuted,
                        modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                    )
                }

                if (playlistTracks.isEmpty()) {
                    item(key = "empty_playlist_tracks") {
                        PlaylistDetailEmptyState(
                            onAddTracksClick = { showAddTrackSheet = true }
                        )
                    }
                } else {
                    itemsIndexed(playlistTracks, key = { _, track -> track.id }) { index, track ->
                        val isActive = activeTrack?.id == track.id
                        ReorderableTrackItem(
                            index = index,
                            totalCount = playlistTracks.size,
                            track = track,
                            isActive = isActive,
                            isPlaying = isPlaying && isActive,
                            onTrackClick = { viewModel.playTrack(track) },
                            onMoveUp = {
                                if (index > 0) {
                                    val reordered = playlistTracks.map { it.id }.toMutableList()
                                    val temp = reordered[index]
                                    reordered[index] = reordered[index - 1]
                                    reordered[index - 1] = temp
                                    viewModel.reorderPlaylistTracks(playlistId, reordered)
                                }
                            },
                            onMoveDown = {
                                if (index < playlistTracks.size - 1) {
                                    val reordered = playlistTracks.map { it.id }.toMutableList()
                                    val temp = reordered[index]
                                    reordered[index] = reordered[index + 1]
                                    reordered[index + 1] = temp
                                    viewModel.reorderPlaylistTracks(playlistId, reordered)
                                }
                            },
                            onRemove = {
                                viewModel.removeTrackFromPlaylist(playlistId, track.id)
                            }
                        )
                    }
                }
            }
        }

        // BottomSheet d'ajout de morceaux
        if (showAddTrackSheet) {
            ModalBottomSheet(
                onDismissRequest = { showAddTrackSheet = false },
                sheetState = addTrackSheetState,
                containerColor = SurfaceDark
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "Ajouter à la playlist",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Sélectionne les morceaux de ta bibliothèque",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    val playlistTrackIds = remember(playlistTracks) { playlistTracks.map { it.id }.toSet() }

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(350.dp)
                    ) {
                        itemsIndexed(allTracks, key = { _, track -> track.id }) { _, track ->
                            val isAlreadyInPlaylist = playlistTrackIds.contains(track.id)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (isAlreadyInPlaylist) SurfaceElevated else SurfaceCard)
                                    .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
                                    .clickable {
                                        if (isAlreadyInPlaylist) {
                                            viewModel.removeTrackFromPlaylist(playlistId, track.id)
                                        } else {
                                            viewModel.addTrackToPlaylist(playlistId, track.id)
                                        }
                                    }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TrackCoverArt(
                                    gradientStr = track.coverGradient,
                                    title = track.title,
                                    size = 40.dp,
                                    shape = RoundedCornerShape(8.dp)
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = track.title,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = track.artist,
                                        fontSize = 12.sp,
                                        color = TextSecondary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(if (isAlreadyInPlaylist) CyanAccent else SurfaceElevated),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (isAlreadyInPlaylist) Icons.Default.Check else Icons.Default.Add,
                                        contentDescription = null,
                                        tint = if (isAlreadyInPlaylist) BackgroundDark else TextSecondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.horizontalGradient(listOf(CyanAccent, PurpleAccent))
                            )
                            .clickable {
                                scope.launch { addTrackSheetState.hide() }.invokeOnCompletion {
                                    showAddTrackSheet = false
                                }
                            }
                            .padding(vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "TERMINÉ",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = BackgroundDark
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
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
                    onTogglePlayPause = { viewModel.togglePlayPause() },
                    onNext = { viewModel.next() },
                    onExpand = onNavigateToPlayer
                )
            }
        }
    }
}

@Composable
private fun ReorderableTrackItem(
    index: Int,
    totalCount: Int,
    track: TrackEntity,
    isActive: Boolean,
    isPlaying: Boolean,
    onTrackClick: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (isActive) SurfaceElevated else SurfaceCard)
            .border(
                1.dp,
                if (isActive) CyanAccent.copy(alpha = 0.50f) else BorderSubtle,
                RoundedCornerShape(16.dp)
            )
            .clickable { onTrackClick() }
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Numéro d'ordre
        Text(
            text = "${index + 1}",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (isActive) CyanAccent else TextMuted,
            modifier = Modifier.width(24.dp),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.width(6.dp))

        // Pochette
        Box(
            modifier = Modifier.size(44.dp),
            contentAlignment = Alignment.Center
        ) {
            TrackCoverArt(
                gradientStr = track.coverGradient,
                title = track.title,
                size = 44.dp,
                shape = RoundedCornerShape(8.dp)
            )

            if (isActive) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.45f)),
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

        Spacer(modifier = Modifier.width(10.dp))

        // Titre et artiste
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = track.title,
                fontSize = 14.sp,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.SemiBold,
                color = if (isActive) CyanAccent else TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = track.artist,
                fontSize = 12.sp,
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Flèches réordonnancement (Monter / Descendre)
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onMoveUp,
                enabled = index > 0,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowUpward,
                    contentDescription = "Monter",
                    tint = if (index > 0) TextSecondary else TextMuted.copy(alpha = 0.3f),
                    modifier = Modifier.size(16.dp)
                )
            }

            IconButton(
                onClick = onMoveDown,
                enabled = index < totalCount - 1,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowDownward,
                    contentDescription = "Descendre",
                    tint = if (index < totalCount - 1) TextSecondary else TextMuted.copy(alpha = 0.3f),
                    modifier = Modifier.size(16.dp)
                )
            }

            // Supprimer de la playlist
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Retirer",
                    tint = TextMuted,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun PlaylistDetailEmptyState(
    onAddTracksClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    EmptyStateComposable(
        type = EmptyStateType.PLAYLISTS,
        customTitle = "Playlist vide",
        customSubtitle = "Ajoute tes premiers morceaux pour commencer à écouter cette playlist.",
        primaryActionLabel = "Ajouter des morceaux",
        onPrimaryAction = onAddTracksClick,
        modifier = modifier
    )
}
