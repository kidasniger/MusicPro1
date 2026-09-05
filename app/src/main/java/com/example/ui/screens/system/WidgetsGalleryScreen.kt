package com.example.ui.screens.system

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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.TrackCoverArt
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.PurpleDeep
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.MainViewModel

@Composable
fun WidgetsGalleryScreen(
    viewModel: MainViewModel,
    initialTab: String = "compact", // "compact", "standard", "lyrics"
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember {
        mutableStateOf(
            when (initialTab) {
                "standard" -> 1
                "lyrics" -> 2
                else -> 0
            }
        )
    }

    val activeTrack by viewModel.activeTrack.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val currentPositionMs by viewModel.currentPositionMs.collectAsState()
    val durationMs by viewModel.durationMs.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    val title = activeTrack?.title ?: "Aucun titre sélectionné"
    val artist = activeTrack?.artist ?: "MusicPro"
    val album = activeTrack?.album ?: ""
    val currentLyric = if (uiState.lyrics.isNotEmpty() && uiState.currentLyricIndex in uiState.lyrics.indices) {
        uiState.lyrics[uiState.currentLyricIndex].text
    } else {
        "Aucune parole synchronisée active"
    }

    val tabs = listOf("wCompact", "wStandard", "wLyrics")
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .statusBarsPadding()
            .verticalScroll(scrollState)
            .padding(bottom = 32.dp)
            .testTag("widgets_gallery_screen")
    ) {
        // En-tête
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.testTag("widgets_back_button")
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Retour",
                    tint = TextPrimary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Widgets MusicPro",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "App Widgets Android (Glance Compose)",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }

        // Onglets de navigation entre les 3 widgets
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = BackgroundDark,
            contentColor = CyanAccent,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = CyanAccent
                )
            }
        ) {
            tabs.forEachIndexed { index, name ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = name,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == index) CyanAccent else TextSecondary,
                            fontSize = 14.sp
                        )
                    },
                    modifier = Modifier.testTag("widget_tab_$name")
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Contenu selon l'onglet
        when (selectedTab) {
            0 -> CompactWidgetSection(
                title = title,
                artist = artist,
                coverGradient = activeTrack?.coverGradient,
                isPlaying = isPlaying,
                onTogglePlay = { viewModel.togglePlayPause() }
            )
            1 -> StandardWidgetSection(
                title = title,
                artist = artist,
                album = album,
                coverGradient = activeTrack?.coverGradient,
                isPlaying = isPlaying,
                currentPositionMs = currentPositionMs,
                durationMs = durationMs,
                onTogglePlay = { viewModel.togglePlayPause() },
                onNext = { viewModel.next() },
                onPrev = { viewModel.previous() }
            )
            2 -> LyricsWidgetSection(
                title = title,
                artist = artist,
                coverGradient = activeTrack?.coverGradient,
                lyric = currentLyric,
                isPlaying = isPlaying,
                onTogglePlay = { viewModel.togglePlayPause() },
                onNext = { viewModel.next() },
                onPrev = { viewModel.previous() }
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Guide d'installation sur l'écran d'accueil Android
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Widgets,
                        contentDescription = null,
                        tint = PurpleAccent,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Comment l'ajouter à votre écran d'accueil",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "1. Maintenez un appui long sur un espace vide de votre écran d'accueil Android.\n2. Sélectionnez 'Widgets' dans le menu pop-up.\n3. Cherchez 'MusicPro' et faites glisser l'un des 3 widgets Glance : Compact, Standard ou Paroles.",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun CompactWidgetSection(
    title: String,
    artist: String,
    coverGradient: String? = null,
    isPlaying: Boolean,
    onTogglePlay: () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text(
            text = "ÉCRAN WIDGET COMPACT (id=\"wCompact\")",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextMuted,
            letterSpacing = 1.sp
        )
        Text(
            text = "Taille minimale (2x1) • Pochette miniature + Titre + Bouton Play/Pause instantané.",
            fontSize = 13.sp,
            color = TextSecondary,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        // Aperçu en taille réelle sur fond de bureau Android
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF0D1117))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            // Widget compact
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceDark)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
                    .padding(10.dp)
                    .testTag("preview_wCompact"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Pochette
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(10.dp))
                ) {
                    TrackCoverArt(
                        gradientStr = coverGradient,
                        title = title,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Titre et artiste
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = artist,
                        fontSize = 12.sp,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Bouton Play/Pause
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (isPlaying) CyanAccent else Color(0xFF27272A))
                        .clickable { onTogglePlay() }
                        .testTag("wcompact_play_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Lecture",
                        tint = if (isPlaying) Color.Black else Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun StandardWidgetSection(
    title: String,
    artist: String,
    album: String,
    coverGradient: String? = null,
    isPlaying: Boolean,
    currentPositionMs: Long,
    durationMs: Long,
    onTogglePlay: () -> Unit,
    onNext: () -> Unit,
    onPrev: () -> Unit
) {
    val total = if (durationMs > 0) durationMs else 218_000L
    val progress = (currentPositionMs.toFloat() / total).coerceIn(0f, 1f)

    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text(
            text = "ÉCRAN WIDGET STANDARD (id=\"wStandard\")",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextMuted,
            letterSpacing = 1.sp
        )
        Text(
            text = "Taille moyenne (4x2) • Pochette enrichie, métadonnées complètes, barre de défilement, boutons précédent / play / suivant.",
            fontSize = 13.sp,
            color = TextSecondary,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        // Aperçu du widget standard
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF0D1117))
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(SurfaceDark)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(20.dp))
                    .padding(16.dp)
                    .testTag("preview_wStandard")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        TrackCoverArt(
                            gradientStr = coverGradient,
                            title = title,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = artist,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = CyanAccent,
                            maxLines = 1
                        )
                        Text(
                            text = album,
                            fontSize = 11.sp,
                            color = TextMuted,
                            maxLines = 1
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Barre de progression
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0xFF27272A))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Brush.horizontalGradient(listOf(CyanAccent, PurpleAccent)))
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Contrôles complets
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1C1C20))
                            .clickable { onPrev() }
                            .testTag("wstandard_prev_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipPrevious,
                            contentDescription = "Précédent",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(20.dp))

                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(if (isPlaying) CyanAccent else PurpleAccent)
                            .clickable { onTogglePlay() }
                            .testTag("wstandard_play_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Lecture",
                            tint = if (isPlaying) Color.Black else Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(20.dp))

                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1C1C20))
                            .clickable { onNext() }
                            .testTag("wstandard_next_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipNext,
                            contentDescription = "Suivant",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LyricsWidgetSection(
    title: String,
    artist: String,
    coverGradient: String? = null,
    lyric: String,
    isPlaying: Boolean,
    onTogglePlay: () -> Unit,
    onNext: () -> Unit = {},
    onPrev: () -> Unit = {}
) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text(
            text = "ÉCRAN WIDGET PAROLES (id=\"wLyrics\")",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextMuted,
            letterSpacing = 1.sp
        )
        Text(
            text = "Taille moyenne (4x2) • Focus sur les paroles synchronisées (LRCLIB/Whisper), affiche la strophe active au rythme de la musique.",
            fontSize = 13.sp,
            color = TextSecondary,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        // Aperçu du widget paroles
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF0D1117))
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF140D24))
                    .border(1.dp, PurpleAccent.copy(alpha = 0.25f), RoundedCornerShape(20.dp))
                    .padding(16.dp)
                    .testTag("preview_wLyrics")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                    ) {
                        TrackCoverArt(
                            gradientStr = coverGradient,
                            title = title,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "$artist • Paroles en direct",
                            fontSize = 11.sp,
                            color = CyanAccent,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Contrôles média
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onPrev, modifier = Modifier.size(32.dp)) {
                            Icon(
                                imageVector = Icons.Default.SkipPrevious,
                                contentDescription = "Précédent",
                                tint = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (isPlaying) CyanAccent else PurpleAccent)
                                .clickable { onTogglePlay() }
                                .testTag("wlyrics_play_btn"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Lecture",
                                tint = if (isPlaying) Color.Black else Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        IconButton(onClick = onNext, modifier = Modifier.size(32.dp)) {
                            Icon(
                                imageVector = Icons.Default.SkipNext,
                                contentDescription = "Suivant",
                                tint = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Bloc de la strophe active
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1F1436))
                        .border(1.dp, PurpleAccent.copy(alpha = 0.20f), RoundedCornerShape(12.dp))
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "“ $lyric ”",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFC4B5FD),
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}
