package com.example.ui.screens.system

import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.TrackCoverArt
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.HeartPink
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceGlass
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LockScreenPreview(
    viewModel: MainViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeTrack by viewModel.activeTrack.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val currentPositionMs by viewModel.currentPositionMs.collectAsState()
    val durationMs by viewModel.durationMs.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val repeatMode by viewModel.repeatMode.collectAsState()
    val isShuffleEnabled by viewModel.isShuffleEnabled.collectAsState()

    var showLyricsOnLock by remember { mutableStateOf(true) }

    val displayTrack = activeTrack ?: viewModel.allTracks.collectAsState().value.firstOrNull()

    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val dateFormat = SimpleDateFormat("EEEE d MMMM", Locale.FRENCH)
    val currentTime = timeFormat.format(Date())
    val currentDate = dateFormat.format(Date()).replaceFirstChar { it.uppercase() }

    val safeDuration = if (durationMs > 0) durationMs else 218_000L
    val progress = (currentPositionMs.toFloat() / safeDuration.toFloat()).coerceIn(0f, 1f)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .testTag("lock_screen")
    ) {
        // Fond ambiance flouté avec la pochette du morceau
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(80.dp)
        ) {
            TrackCoverArt(
                gradientStr = displayTrack?.coverGradient ?: "from-[#22D3EE] to-[#A855F7]",
                title = displayTrack?.title ?: "MusicPro",
                modifier = Modifier.fillMaxSize()
            )
        }

        // Overlay sombre pour contraste et lisibilité
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.55f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header discret pour fermer / revenir
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(SurfaceGlass)
                        .testTag("lock_back_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Retour",
                        tint = TextPrimary
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(SurfaceGlass)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = CyanAccent,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Écran Verrouillé",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                }

                Spacer(modifier = Modifier.size(40.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Horloge de l'écran verrouillé
            Text(
                text = currentTime,
                fontSize = 68.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-2).sp,
                color = Color.White
            )

            Text(
                text = currentDate,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // WIDGET MEDIA LOCK SCREEN (Android 13/14 MediaSession)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color.White.copy(alpha = 0.12f))
                    .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(28.dp))
                    .padding(20.dp)
                    .testTag("lock_screen_media_widget")
            ) {
                Column {
                    // Pochette + Infos + Favori
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
                        ) {
                            TrackCoverArt(
                                gradientStr = displayTrack?.coverGradient,
                                title = displayTrack?.title ?: "MusicPro",
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = displayTrack?.title ?: "Titre",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = displayTrack?.artist ?: "Artiste",
                                fontSize = 13.sp,
                                color = Color.White.copy(alpha = 0.75f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${displayTrack?.format ?: "FLAC"} • 24-bit Hi-Res",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = CyanAccent
                            )
                        }

                        IconButton(
                            onClick = {
                                if (displayTrack != null) viewModel.toggleFavorite(displayTrack)
                            }
                        ) {
                            Icon(
                                imageVector = if (displayTrack?.isFavorite == true) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favori",
                                tint = if (displayTrack?.isFavorite == true) HeartPink else Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Visualisation des Paroles synchronisées sur l'écran verrouillé
                    if (showLyricsOnLock) {
                        val activeLyricIndex = uiState.currentLyricIndex
                        val activeLineText = if (uiState.lyrics.isNotEmpty() && activeLyricIndex in uiState.lyrics.indices) {
                            uiState.lyrics[activeLyricIndex].text
                        } else null

                        val nextLineText = if (uiState.lyrics.isNotEmpty() && activeLyricIndex + 1 in uiState.lyrics.indices) {
                            uiState.lyrics[activeLyricIndex + 1].text
                        } else null

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color.Black.copy(alpha = 0.35f))
                                .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(14.dp))
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Mic,
                                            contentDescription = null,
                                            tint = CyanAccent,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "PAROLES EN DIRECT",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = CyanAccent,
                                            letterSpacing = 1.sp
                                        )
                                    }
                                    Text(
                                        text = if (uiState.lyrics.isNotEmpty()) "Sync" else "Recherche...",
                                        fontSize = 9.sp,
                                        color = TextMuted
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                if (activeLineText != null && activeLineText.isNotBlank()) {
                                    Text(
                                        text = activeLineText,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        textAlign = TextAlign.Center,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    if (nextLineText != null && nextLineText.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = nextLineText,
                                            fontSize = 12.sp,
                                            color = Color.White.copy(alpha = 0.45f),
                                            textAlign = TextAlign.Center,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                } else {
                                    Text(
                                        text = if (uiState.lyrics.isEmpty()) "♫ Paroles en cours de synchronisation..." else "♫ Instrumental...",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.White.copy(alpha = 0.6f),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Slider de scrubbing
                    Slider(
                        value = progress,
                        onValueChange = { newProgress ->
                            val targetMs = (newProgress * safeDuration).toLong()
                            viewModel.seekTo(targetMs)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = SliderDefaults.colors(
                            thumbColor = Color.White,
                            activeTrackColor = CyanAccent,
                            inactiveTrackColor = Color.White.copy(alpha = 0.2f)
                        )
                    )

                    // Horodatages
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = formatMs(currentPositionMs),
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        Text(
                            text = formatMs(safeDuration),
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Contrôles média
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { viewModel.toggleShuffle() },
                            modifier = Modifier.testTag("lock_shuffle_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shuffle,
                                contentDescription = "Aléatoire",
                                tint = if (isShuffleEnabled) CyanAccent else Color.White.copy(alpha = 0.6f),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        IconButton(
                            onClick = { viewModel.previous() },
                            modifier = Modifier.testTag("lock_prev_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.SkipPrevious,
                                contentDescription = "Précédent",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        // Bouton Play/Pause
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .clickable { viewModel.togglePlayPause() }
                                .testTag("lock_play_pause_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Lecture",
                                tint = Color.Black,
                                modifier = Modifier.size(30.dp)
                            )
                        }

                        IconButton(
                            onClick = { viewModel.next() },
                            modifier = Modifier.testTag("lock_next_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.SkipNext,
                                contentDescription = "Suivant",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        IconButton(
                            onClick = { viewModel.cycleRepeatMode() },
                            modifier = Modifier.testTag("lock_repeat_button")
                        ) {
                            Icon(
                                imageVector = if (repeatMode == 1) Icons.Default.RepeatOne else Icons.Default.Repeat,
                                contentDescription = "Répéter",
                                tint = when (repeatMode) {
                                    1 -> PurpleAccent
                                    2 -> CyanAccent
                                    else -> Color.White.copy(alpha = 0.6f)
                                },
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Raccourcis bas d'écran verrouillé (Lampe torche, Empreinte, Appareil photo)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.FlashlightOn,
                        contentDescription = "Lampe",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Fingerprint,
                        contentDescription = "Déverrouiller",
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Glisser pour déverrouiller",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = "Appareil photo",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

private fun formatMs(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}
