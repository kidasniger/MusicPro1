package com.example.ui.screens.player

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.TrackEntity
import com.example.ui.components.TrackCoverArt
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.HeartPink
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.PurpleDeep
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfacePurpleTint
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

fun formatTime(ms: Long): String {
    val totalSeconds = (ms / 1000).coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%d:%02d", minutes, seconds)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    activeTrack: TrackEntity?,
    isPlaying: Boolean,
    currentPositionMs: Long,
    durationMs: Long,
    queue: List<TrackEntity>,
    repeatMode: Int = 0,
    isShuffleEnabled: Boolean = false,
    playerVisualVariant: String = "VINYL",
    sleepRemainingSeconds: Long? = null,
    isEndOfTrackSleepActive: Boolean = false,
    onTogglePlayPause: () -> Unit,
    onSeekTo: (Long) -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onToggleFavorite: (TrackEntity) -> Unit,
    onCycleRepeatMode: () -> Unit = {},
    onToggleRepeatOne: () -> Unit = {},
    onToggleShuffle: () -> Unit = {},
    onSetPlayerVisualVariant: (String) -> Unit = {},
    onTrackFromQueueSelected: (TrackEntity) -> Unit,
    onMoveQueueItem: (fromIndex: Int, toIndex: Int) -> Unit = { _, _ -> },
    onRemoveQueueItem: (index: Int) -> Unit = {},
    onClearQueue: () -> Unit = {},
    onSetSleepTimerMinutes: (minutes: Int) -> Unit = {},
    onSetSleepAtEndOfTrack: () -> Unit = {},
    onCancelSleepTimer: () -> Unit = {},
    onNavigateBack: () -> Unit,
    onNavigateToLyrics: () -> Unit,
    onNavigateToVariants: () -> Unit = {},
    onNavigateToEqualizer: () -> Unit = {},
    onNavigateToQueue: () -> Unit = {},
    onNavigateToTrackInfo: () -> Unit = {},
    onNavigateToLockScreen: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    if (activeTrack == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(BackgroundDark),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Aucun morceau sélectionné", color = TextMuted)
        }
        return
    }

    var isQueueSheetVisible by remember { mutableStateOf(false) }
    var isSleepTimerSheetVisible by remember { mutableStateOf(false) }
    var isEqualizerSheetVisible by remember { mutableStateOf(false) }
    var isTrackInfoSheetVisible by remember { mutableStateOf(false) }
    var showVariantDialog by remember { mutableStateOf(false) }

    val queueSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val sleepSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val eqSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val infoSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var showOptionsMenu by remember { mutableStateOf(false) }
    var localIsFavorite by remember(activeTrack.id, activeTrack.isFavorite) {
        mutableStateOf(activeTrack.isFavorite)
    }

    // Animation de rotation continue du vinyle quand la musique joue
    val infiniteTransition = rememberInfiniteTransition(label = "vinylRotation")
    val vinylRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "vinylAngle"
    )

    val currentProgress = if (durationMs > 0) {
        (currentPositionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f)
    } else 0f

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .testTag("player_screen")
    ) {
        // Halos lumineux d'ambiance en arrière-plan
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 80.dp)
                .size(340.dp)
                .blur(90.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(
                            PurpleAccent.copy(alpha = 0.35f),
                            CyanAccent.copy(alpha = 0.25f),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Barre supérieure : Fermer + Titre "LECTEUR" + Menu options
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.testTag("player_collapse_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Réduire",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "EN COURS DE LECTURE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        color = TextMuted
                    )
                    Text(
                        text = activeTrack.album.ifEmpty { "Album Local" },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onNavigateToLockScreen,
                        modifier = Modifier.testTag("player_lock_screen_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Écran de verrouillage",
                            tint = CyanAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = { showVariantDialog = true },
                        modifier = Modifier.testTag("player_variant_top_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Variantes du lecteur",
                            tint = PurpleAccent,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Box {
                        IconButton(
                            onClick = { showOptionsMenu = true },
                            modifier = Modifier.testTag("player_options_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Options",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = showOptionsMenu,
                            onDismissRequest = { showOptionsMenu = false },
                            modifier = Modifier.background(SurfaceDark)
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        if (localIsFavorite) "Retirer des favoris" else "Ajouter aux favoris",
                                        color = TextPrimary
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = if (localIsFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                        contentDescription = null,
                                        tint = HeartPink,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                onClick = {
                                    showOptionsMenu = false
                                    val newFav = !localIsFavorite
                                    localIsFavorite = newFav
                                    onToggleFavorite(activeTrack.copy(isFavorite = newFav))
                                }
                            )

                        DropdownMenuItem(
                            text = { Text("Voir les paroles", color = TextPrimary) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = null,
                                    tint = CyanAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            onClick = {
                                showOptionsMenu = false
                                onNavigateToLyrics()
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("Infos du morceau", color = TextPrimary) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = CyanAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            onClick = {
                                showOptionsMenu = false
                                isTrackInfoSheetVisible = true
                            }
                        )

                        DropdownMenuItem(
                            text = {
                                Text(
                                    if (repeatMode == 1) "Boucle 1 morceau (Actif)" else "Lecture d'un seul morceau",
                                    color = if (repeatMode == 1) PurpleAccent else TextPrimary
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.RepeatOne,
                                    contentDescription = null,
                                    tint = if (repeatMode == 1) PurpleAccent else CyanAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            onClick = {
                                showOptionsMenu = false
                                onToggleRepeatOne()
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("Variantes du lecteur", color = TextPrimary) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = null,
                                    tint = PurpleAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            onClick = {
                                showOptionsMenu = false
                                showVariantDialog = true
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("Égaliseur plein écran", color = TextPrimary) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Equalizer,
                                    contentDescription = null,
                                    tint = CyanAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            onClick = {
                                showOptionsMenu = false
                                onNavigateToEqualizer()
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("Écran de verrouillage", color = TextPrimary) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = CyanAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            onClick = {
                                showOptionsMenu = false
                                onNavigateToLockScreen()
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("File d'attente complète", color = TextPrimary) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.QueueMusic,
                                    contentDescription = null,
                                    tint = CyanAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            onClick = {
                                showOptionsMenu = false
                                onNavigateToQueue()
                            }
                        )
                    }
                }
            }
        }

            // Visuel central selon la variante sélectionnée
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                when (playerVisualVariant) {
                    "WAVEFORM" -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(0.92f)
                                    .height(180.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(SurfaceCard)
                                    .border(1.dp, BorderSubtle, RoundedCornerShape(20.dp))
                                    .padding(horizontal = 14.dp, vertical = 20.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val barCount = 28
                                for (i in 0 until barCount) {
                                    val factor = (Math.sin((i + currentPositionMs / 200.0) * 0.4) + 1.2).toFloat()
                                    val barHeight = if (isPlaying) (28 * factor).coerceIn(12f, 130f) else 16f
                                    Box(
                                        modifier = Modifier
                                            .width(5.dp)
                                            .height(barHeight.dp)
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(
                                                Brush.verticalGradient(
                                                    listOf(CyanAccent, PurpleAccent)
                                                )
                                            )
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "ANALYSE SPECTRALE EN TEMPS RÉEL (44.1 kHz)",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyanAccent,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                    "COVER_ART" -> {
                        Box(
                            modifier = Modifier
                                .size(260.dp)
                                .shadow(
                                    elevation = 32.dp,
                                    shape = RoundedCornerShape(24.dp),
                                    spotColor = CyanAccent.copy(alpha = 0.35f)
                                )
                                .clip(RoundedCornerShape(24.dp))
                                .border(1.dp, BorderSubtle, RoundedCornerShape(24.dp))
                        ) {
                            TrackCoverArt(
                                gradientStr = activeTrack.coverGradient,
                                title = activeTrack.title,
                                coverArtUri = activeTrack.coverArtUri,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                    else -> {
                        // VINYL 33T
                        Box(
                            modifier = Modifier
                                .size(270.dp)
                                .shadow(
                                    elevation = 48.dp,
                                    shape = CircleShape,
                                    spotColor = PurpleAccent.copy(alpha = 0.50f),
                                    ambientColor = CyanAccent.copy(alpha = 0.30f)
                                )
                                .clip(CircleShape)
                                .rotate(if (isPlaying) vinylRotation else 0f)
                        ) {
                            TrackCoverArt(
                                gradientStr = activeTrack.coverGradient,
                                title = activeTrack.title,
                                coverArtUri = activeTrack.coverArtUri,
                                size = 270.dp,
                                isVinyl = true
                            )
                        }
                    }
                }
            }

            // Section Métadonnées & Like
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = activeTrack.title,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = activeTrack.artist,
                            fontSize = 15.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(top = 2.dp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Bouton Like / Favori
                    IconButton(
                        onClick = {
                            val newFav = !localIsFavorite
                            localIsFavorite = newFav
                            onToggleFavorite(activeTrack.copy(isFavorite = newFav))
                        },
                        modifier = Modifier.testTag("player_favorite_button")
                    ) {
                        Icon(
                            imageVector = if (localIsFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favori",
                            tint = if (localIsFavorite) HeartPink else Color.White.copy(alpha = 0.70f),
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }

                // Badges techniques audio (Format, Bitrate, Fréquence)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (activeTrack.format == "FLAC") PurpleDeep.copy(alpha = 0.5f)
                                else Color.White.copy(alpha = 0.08f)
                            )
                            .border(
                                1.dp,
                                if (activeTrack.format == "FLAC") PurpleAccent.copy(alpha = 0.6f)
                                else BorderSubtle,
                                RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${activeTrack.format} • ${activeTrack.bitrate}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (activeTrack.format == "FLAC") PurpleAccent else TextMuted
                        )
                    }

                    Text(
                        text = "44.1kHz • Stéréo",
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Barre de progression (Seekbar) avec timestamps
            Column(modifier = Modifier.fillMaxWidth()) {
                Slider(
                    value = currentProgress,
                    onValueChange = { fraction ->
                        val targetMs = (fraction * durationMs).toLong()
                        onSeekTo(targetMs)
                    },
                    colors = SliderDefaults.colors(
                        thumbColor = Color.White,
                        activeTrackColor = CyanAccent,
                        inactiveTrackColor = Color.White.copy(alpha = 0.12f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("player_progress_slider")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = formatTime(currentPositionMs),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = TextMuted
                    )
                    Text(
                        text = formatTime(durationMs),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Contrôles principaux de lecture (Précédent, Play/Pause, Suivant, Shuffle, Repeat)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Shuffle
                IconButton(
                    onClick = onToggleShuffle,
                    modifier = Modifier.testTag("player_shuffle_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Shuffle,
                        contentDescription = "Aléatoire",
                        tint = if (isShuffleEnabled) CyanAccent else Color.White.copy(alpha = 0.60f),
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Previous
                IconButton(
                    onClick = onPrevious,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("player_previous_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "Précédent",
                        tint = Color.White,
                        modifier = Modifier.size(34.dp)
                    )
                }

                // Play / Pause géant
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .shadow(
                            elevation = 24.dp,
                            shape = CircleShape,
                            spotColor = PurpleAccent.copy(alpha = 0.50f)
                        )
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(CyanAccent, PurpleAccent)
                            )
                        )
                        .clickable { onTogglePlayPause() }
                        .testTag("player_play_pause_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Lecture",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Next
                IconButton(
                    onClick = onNext,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("player_next_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Suivant",
                        tint = Color.White,
                        modifier = Modifier.size(34.dp)
                    )
                }

                // Repeat
                IconButton(
                    onClick = onCycleRepeatMode,
                    modifier = Modifier.testTag("player_repeat_button")
                ) {
                    Icon(
                        imageVector = if (repeatMode == 1) Icons.Default.RepeatOne else Icons.Default.Repeat,
                        contentDescription = when (repeatMode) {
                            1 -> "Répéter un seul morceau"
                            2 -> "Tout répéter"
                            else -> "Répétition désactivée"
                        },
                        tint = when (repeatMode) {
                            1 -> PurpleAccent
                            2 -> CyanAccent
                            else -> Color.White.copy(alpha = 0.60f)
                        },
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Boutons d'actions secondaires : File d'attente, Paroles, Égaliseur, Minuteur
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Paroles synchronisées
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { onNavigateToLyrics() }
                        .padding(8.dp)
                        .testTag("player_lyrics_shortcut")
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Paroles",
                        tint = CyanAccent,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = "Paroles",
                        fontSize = 10.sp,
                        color = TextMuted,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                // File d'attente (Queue)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { isQueueSheetVisible = true }
                        .padding(8.dp)
                        .testTag("player_queue_shortcut")
                ) {
                    Icon(
                        imageVector = Icons.Default.QueueMusic,
                        contentDescription = "File d'attente",
                        tint = Color.White.copy(alpha = 0.75f),
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = "File (${queue.size})",
                        fontSize = 10.sp,
                        color = TextMuted,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                // Égaliseur
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { isEqualizerSheetVisible = true }
                        .padding(8.dp)
                        .testTag("player_eq_shortcut")
                ) {
                    Icon(
                        imageVector = Icons.Default.Equalizer,
                        contentDescription = "Égaliseur",
                        tint = Color.White.copy(alpha = 0.75f),
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = "EQ 5-Band",
                        fontSize = 10.sp,
                        color = TextMuted,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                // Minuteur de sommeil
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { isSleepTimerSheetVisible = true }
                        .padding(8.dp)
                        .testTag("player_sleep_shortcut")
                ) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = "Minuteur",
                        tint = if (sleepRemainingSeconds != null || isEndOfTrackSleepActive) CyanAccent else Color.White.copy(alpha = 0.75f),
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = if (sleepRemainingSeconds != null) {
                            "${sleepRemainingSeconds / 60}m"
                        } else if (isEndOfTrackSleepActive) {
                            "Fin titre"
                        } else {
                            "Sommeil"
                        },
                        fontSize = 10.sp,
                        color = if (sleepRemainingSeconds != null || isEndOfTrackSleepActive) CyanAccent else TextMuted,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        // 1. Bottom Sheet : File d'attente (id="queue", réordonnable et modifiable)
        if (isQueueSheetVisible) {
            QueueSheet(
                queue = queue,
                activeTrack = activeTrack,
                isPlaying = isPlaying,
                sheetState = queueSheetState,
                onDismiss = { isQueueSheetVisible = false },
                onTrackSelect = { track ->
                    onTrackFromQueueSelected(track)
                    isQueueSheetVisible = false
                },
                onMoveTrackUp = { index -> onMoveQueueItem(index, index - 1) },
                onMoveTrackDown = { index -> onMoveQueueItem(index, index + 1) },
                onRemoveTrack = { index -> onRemoveQueueItem(index) },
                onClearQueue = { onClearQueue() }
            )
        }

        // 2. Bottom Sheet : Minuteur de sommeil (id="sleep", 5m, 10m, 15m, 30m, 45m, 60m, 90m + Fin de morceau)
        if (isSleepTimerSheetVisible) {
            SleepTimerSheet(
                sheetState = sleepSheetState,
                remainingSeconds = sleepRemainingSeconds,
                isEndOfTrackActive = isEndOfTrackSleepActive,
                onSetTimerMinutes = { minutes -> onSetSleepTimerMinutes(minutes) },
                onSetEndOfTrack = { onSetSleepAtEndOfTrack() },
                onCancelTimer = { onCancelSleepTimer() },
                onDismiss = { isSleepTimerSheetVisible = false }
            )
        }

        // 3. Bottom Sheet : Égaliseur (id="eq", 5 bandes, presets, Bass Boost & 3D Surround)
        if (isEqualizerSheetVisible) {
            EqualizerSheet(
                sheetState = eqSheetState,
                onDismiss = { isEqualizerSheetVisible = false }
            )
        }

        // 4. Bottom Sheet : Informations du morceau (id="infos", format, bitrate, durée, taille, chemin)
        if (isTrackInfoSheetVisible) {
            TrackInfoSheet(
                track = activeTrack,
                sheetState = infoSheetState,
                onDismiss = { isTrackInfoSheetVisible = false }
            )
        }

        // 5. Dialogue de choix de la variante visuelle du lecteur
        if (showVariantDialog) {
            AlertDialog(
                onDismissRequest = { showVariantDialog = false },
                title = {
                    Text(
                        text = "Variante d'affichage du lecteur",
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        listOf(
                            Triple("VINYL", "Vinyle 33T en rotation", Icons.Default.Radio),
                            Triple("WAVEFORM", "Spectre audio Waveform", Icons.Default.GraphicEq),
                            Triple("COVER_ART", "Pochette d'album pure", Icons.Default.Image)
                        ).forEach { (variantKey, label, icon) ->
                            val isCurrent = playerVisualVariant == variantKey
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isCurrent) CyanAccent.copy(alpha = 0.18f) else SurfaceCard)
                                    .border(1.dp, if (isCurrent) CyanAccent else BorderSubtle, RoundedCornerShape(12.dp))
                                    .clickable {
                                        onSetPlayerVisualVariant(variantKey)
                                        showVariantDialog = false
                                    }
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = if (isCurrent) CyanAccent else TextMuted,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = label,
                                    color = if (isCurrent) CyanAccent else TextPrimary,
                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    Text(
                        text = "Fermer",
                        color = CyanAccent,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable { showVariantDialog = false }
                            .padding(8.dp)
                    )
                },
                containerColor = SurfaceDark
            )
        }
    }
}
