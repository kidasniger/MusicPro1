package com.example.ui.screens.lyrics

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.lyrics.LyricLine
import com.example.ui.components.EmptyStateComposable
import com.example.ui.components.EmptyStateType
import com.example.ui.components.ErrorStateComposable
import com.example.ui.components.ErrorVariant
import com.example.ui.components.TrackCoverArt
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.SurfaceGlass
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.MainViewModel

@Composable
fun LyricsScreen(
    viewModel: MainViewModel,
    onBackClick: () -> Unit = {},
    onOpenSourceSheet: () -> Unit = {},
    onOpenTranslate: () -> Unit = {},
    onOpenSync: () -> Unit = {},
    onOpenLrclibSearch: () -> Unit = {},
    onOpenGroqConfig: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val activeTrack by viewModel.activeTrack.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    val currentLyricIndex = uiState.currentLyricIndex
    val lyrics = uiState.lyrics
    val isTranslated = uiState.isTranslated
    val listState = rememberLazyListState()

    // Défilement automatique centré sur la ligne active
    LaunchedEffect(currentLyricIndex) {
        if (lyrics.isNotEmpty() && currentLyricIndex in lyrics.indices) {
            val targetScroll = (currentLyricIndex - 2).coerceAtLeast(0)
            listState.animateScrollToItem(targetScroll)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .testTag("lyrics_screen")
    ) {
        // Fond ambiance lumineuse tamisée selon le morceau actif
        if (activeTrack != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.18f)
                    .blur(70.dp)
            ) {
                TrackCoverArt(
                    gradientStr = activeTrack?.coverGradient ?: "from-[#22D3EE] to-[#A855F7]",
                    title = activeTrack?.title ?: "",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(SurfaceGlass)
                        .testTag("lyrics_back_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Retour",
                        tint = TextPrimary
                    )
                }

                // Titre du morceau actif et source
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
                ) {
                    Text(
                        text = activeTrack?.title ?: "Paroles",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = activeTrack?.artist ?: "MusicPro Player",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Bouton Source Sheet
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(SurfaceElevated)
                        .border(1.dp, BorderSubtle, RoundedCornerShape(20.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onOpenSourceSheet() }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("lyrics_source_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = null,
                            tint = CyanAccent,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "SOURCE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyanAccent
                        )
                    }
                }
            }

            // Badge source info & boutons d'outils rapides (Sync, Traduction, Recherche LRCLIB)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Info match
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(if (lyrics.isNotEmpty()) CyanAccent else TextMuted)
                    )
                    Text(
                        text = if (uiState.isLyricsLoading) "Recherche en cours..." else uiState.lyricsSource.ifEmpty { "Aucune source active" },
                        fontSize = 11.sp,
                        color = TextMuted,
                        maxLines = 1
                    )
                }

                // Boutons d'actions rapides (Traduction, Sync, Recherche)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Traduire
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(if (isTranslated) CyanAccent.copy(alpha = 0.20f) else SurfaceGlass)
                            .border(1.dp, if (isTranslated) CyanAccent else BorderSubtle, CircleShape)
                            .clickable { onOpenTranslate() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Translate,
                            contentDescription = "Traduire",
                            tint = if (isTranslated) CyanAccent else TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Sync Offset
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(if (uiState.lyricsOffsetMs != 0L) PurpleAccent.copy(alpha = 0.20f) else SurfaceGlass)
                            .border(1.dp, if (uiState.lyricsOffsetMs != 0L) PurpleAccent else BorderSubtle, CircleShape)
                            .clickable { onOpenSync() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sync,
                            contentDescription = "Ajuster la synchronisation",
                            tint = if (uiState.lyricsOffsetMs != 0L) PurpleAccent else TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Recherche manuelle LRCLIB
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(SurfaceGlass)
                            .border(1.dp, BorderSubtle, CircleShape)
                            .clickable { onOpenLrclibSearch() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Rechercher sur LRCLIB",
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Intégrer les paroles trouvées directement dans le fichier audio
                    val context = androidx.compose.ui.platform.LocalContext.current
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(SurfaceGlass)
                            .border(1.dp, if (uiState.lyricsSource.contains("Fichier audio")) CyanAccent else BorderSubtle, CircleShape)
                            .clickable {
                                if (lyrics.isNotEmpty()) {
                                    viewModel.embedLyricsToActiveTrack(context) { success ->
                                        if (success) {
                                            android.widget.Toast.makeText(context, "Paroles intégrées avec succès au fichier audio !", android.widget.Toast.LENGTH_SHORT).show()
                                        } else {
                                            android.widget.Toast.makeText(context, "Impossible d'intégrer les paroles.", android.widget.Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                } else {
                                    android.widget.Toast.makeText(context, "Aucune parole à intégrer pour ce morceau.", android.widget.Toast.LENGTH_SHORT).show()
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = "Intégrer les paroles dans le fichier audio",
                            tint = if (uiState.lyricsSource.contains("Fichier audio")) CyanAccent else TextPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Contenu principal : Paroles synchronisées OU Erreur OU État vide
            if (uiState.isLyricsLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = CyanAccent, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Recherche des paroles en cours...",
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                    }
                }
            } else if (uiState.lyricsError != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    val isGroqError = uiState.lyricsError?.contains("Groq", ignoreCase = true) == true
                    ErrorStateComposable(
                        variant = if (isGroqError) ErrorVariant.GROQ_UNAVAILABLE else ErrorVariant.NETWORK_LRCLIB,
                        customMessage = uiState.lyricsError,
                        onRetry = {
                            activeTrack?.let { viewModel.loadLyricsForTrack(it) }
                        },
                        onSecondaryAction = if (isGroqError) onOpenGroqConfig else onOpenSourceSheet,
                        secondaryActionLabel = if (isGroqError) "Configurer Groq IA" else "Changer de source"
                    )
                }
            } else if (lyrics.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyStateComposable(
                        type = EmptyStateType.LYRICS,
                        customSubtitle = "Nous n'avons pas trouvé de paroles automatiques pour « ${activeTrack?.title ?: "ce morceau"} ».",
                        onPrimaryAction = onOpenSourceSheet,
                        onSecondaryAction = onOpenLrclibSearch,
                        onTertiaryAction = onOpenGroqConfig
                    )
                }
            } else {
                // Affichage des paroles synchronisées ligne par ligne
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(top = 40.dp, bottom = 120.dp, start = 24.dp, end = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .testTag("lyrics_list")
                ) {
                    itemsIndexed(lyrics) { index, line ->
                        val isActive = index == currentLyricIndex
                        val isPast = index < currentLyricIndex

                        LyricLineItem(
                            line = line,
                            isActive = isActive,
                            isPast = isPast,
                            showTranslation = isTranslated,
                            onClick = {
                                // Saut direct au timestamp du morceau
                                viewModel.seekTo(line.timeMs)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LyricLineItem(
    line: LyricLine,
    isActive: Boolean,
    isPast: Boolean,
    showTranslation: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isActive) 1.05f else 1.0f,
        animationSpec = tween(durationMillis = 250),
        label = "lyricScale"
    )

    val alpha by animateFloatAsState(
        targetValue = when {
            isActive -> 1.0f
            isPast -> 0.40f
            else -> 0.25f
        },
        animationSpec = tween(durationMillis = 250),
        label = "lyricAlpha"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .alpha(alpha)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.Start
    ) {
        // Ligne principale de la chanson
        Text(
            text = line.text,
            fontSize = if (isActive) 24.sp else 20.sp,
            fontWeight = if (isActive) FontWeight.ExtraBold else FontWeight.Medium,
            color = if (isActive) PureWhiteText() else TextPrimary,
            lineHeight = if (isActive) 32.sp else 28.sp
        )

        // Traduction synchronisée si activée
        AnimatedVisibility(visible = showTranslation && !line.translatedText.isNullOrBlank()) {
            Text(
                text = line.translatedText ?: "",
                fontSize = if (isActive) 16.sp else 14.sp,
                fontWeight = FontWeight.Normal,
                color = if (isActive) CyanAccent else TextMuted,
                lineHeight = 22.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun PureWhiteText(): Color = Color.White

