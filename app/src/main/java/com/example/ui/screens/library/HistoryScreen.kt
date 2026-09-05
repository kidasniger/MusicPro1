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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.dao.HistoryWithTrack
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    viewModel: MainViewModel,
    onBackClick: () -> Unit,
    onNavigateToPlayer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val historyItems by viewModel.historyWithTracks.collectAsState()
    val activeTrack by viewModel.activeTrack.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val currentPositionMs by viewModel.currentPositionMs.collectAsState()
    val durationMs by viewModel.durationMs.collectAsState()

    var showClearDialog by remember { mutableStateOf(false) }

    // Regroupement par "Aujourd'hui", "Hier", "Cette semaine"
    val groupedHistory = remember(historyItems) {
        groupHistoryByPeriod(historyItems)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .statusBarsPadding()
            .testTag("history_screen")
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
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
                            text = "Historique d'écoute",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "${historyItems.size} écoutes récentes",
                            fontSize = 12.sp,
                            color = CyanAccent
                        )
                    }
                }

                if (historyItems.isNotEmpty()) {
                    IconButton(
                        onClick = { showClearDialog = true },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(SurfaceDark)
                            .border(1.dp, BorderSubtle, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "Effacer l'historique",
                            tint = TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            if (historyItems.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyStateComposable(
                        type = EmptyStateType.HISTORY,
                        onPrimaryAction = onBackClick
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(
                        start = 20.dp,
                        end = 20.dp,
                        top = 8.dp,
                        bottom = 110.dp
                    ),
                    modifier = Modifier.fillMaxSize()
                ) {
                    groupedHistory.forEach { (periodLabel, itemsInPeriod) ->
                        // En-tête de section temporelle ("Aujourd'hui", "Hier", "Cette semaine")
                        item(key = "header_$periodLabel") {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp, bottom = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = periodLabel.uppercase(),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.5.sp,
                                    color = CyanAccent
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(1.dp)
                                        .background(BorderSubtle)
                                )
                            }
                        }

                        // Liste des morceaux pour cette période
                        items(itemsInPeriod, key = { it.id }) { item ->
                            val isActive = activeTrack?.id == item.trackId
                            HistoryTrackItem(
                                item = item,
                                isActive = isActive,
                                isPlaying = isPlaying && isActive,
                                onTrackClick = {
                                    viewModel.playTrackById(item.trackId)
                                }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }

        // Dialogue de confirmation d'effacement
        if (showClearDialog) {
            AlertDialog(
                onDismissRequest = { showClearDialog = false },
                title = {
                    Text(
                        text = "Effacer l'historique ?",
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                },
                text = {
                    Text(
                        text = "Cette action supprimera toutes les écoutes enregistrées sur l'appareil.",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.clearHistory()
                            showClearDialog = false
                        }
                    ) {
                        Text("Effacer", color = Color(0xFFFF5252), fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showClearDialog = false }) {
                        Text("Annuler", color = TextSecondary)
                    }
                },
                containerColor = SurfaceDark,
                shape = RoundedCornerShape(20.dp)
            )
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
private fun HistoryTrackItem(
    item: HistoryWithTrack,
    isActive: Boolean,
    isPlaying: Boolean,
    onTrackClick: () -> Unit
) {
    val timeFormatted = remember(item.playedAt) {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        sdf.format(Date(item.playedAt))
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (isActive) SurfaceElevated else SurfaceCard
            )
            .border(
                width = 1.dp,
                color = if (isActive) CyanAccent.copy(alpha = 0.50f) else BorderSubtle,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onTrackClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Pochette
        Box(
            modifier = Modifier.size(48.dp),
            contentAlignment = Alignment.Center
        ) {
            TrackCoverArt(
                gradientStr = item.coverGradient,
                title = item.title,
                size = 48.dp,
                shape = RoundedCornerShape(10.dp)
            )

            if (isActive) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.Black.copy(alpha = 0.45f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Equalizer else Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = CyanAccent,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Titre & Artiste
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                fontSize = 15.sp,
                fontWeight = if (isActive) FontWeight.Black else FontWeight.SemiBold,
                color = if (isActive) CyanAccent else TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${item.artist} • ${item.album.ifEmpty { item.format }}",
                fontSize = 12.sp,
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Heure d'écoute
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = timeFormatted,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = CyanAccent
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = item.durationFormatted,
                fontSize = 11.sp,
                color = TextMuted
            )
        }
    }
}

private fun groupHistoryByPeriod(items: List<HistoryWithTrack>): Map<String, List<HistoryWithTrack>> {
    val result = linkedMapOf<String, MutableList<HistoryWithTrack>>()

    val now = Calendar.getInstance()
    val today = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    val yesterday = Calendar.getInstance().apply {
        timeInMillis = today.timeInMillis
        add(Calendar.DAY_OF_YEAR, -1)
    }

    val thisWeek = Calendar.getInstance().apply {
        timeInMillis = today.timeInMillis
        add(Calendar.DAY_OF_YEAR, -7)
    }

    items.forEach { item ->
        val itemDate = Calendar.getInstance().apply { timeInMillis = item.playedAt }

        val period = when {
            itemDate.after(today) -> "Aujourd'hui"
            itemDate.after(yesterday) -> "Hier"
            itemDate.after(thisWeek) -> "Cette semaine"
            else -> "Plus ancien"
        }

        result.getOrPut(period) { mutableListOf() }.add(item)
    }

    return result
}
