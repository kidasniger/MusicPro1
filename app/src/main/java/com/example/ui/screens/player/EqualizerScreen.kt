package com.example.ui.screens.player

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
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

@Composable
fun EqualizerScreen(
    viewModel: MainViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeTrack by viewModel.activeTrack.collectAsState()
    val scrollState = rememberScrollState()

    var isEqEnabled by remember { mutableStateOf(true) }
    var selectedPreset by remember { mutableStateOf("Bass Boost") }

    val bandFrequencies = remember { listOf("60 Hz", "230 Hz", "910 Hz", "3.6 kHz", "14 kHz") }
    val bandLevels = remember { mutableStateListOf(5f, 3f, 0f, 2f, 4f) }

    var bassBoostLevel by remember { mutableFloatStateOf(65f) }
    var virtualizerLevel by remember { mutableFloatStateOf(40f) }

    val presets = listOf(
        "Plat" to listOf(0f, 0f, 0f, 0f, 0f),
        "Bass Boost" to listOf(6f, 4f, 0f, 1f, 3f),
        "Rock" to listOf(4f, 2f, -1f, 3f, 5f),
        "Pop" to listOf(-1f, 2f, 5f, 2f, -1f),
        "Électro" to listOf(5f, 3f, 0f, 2f, 5f),
        "Vocal" to listOf(-2f, 1f, 4f, 3f, 1f),
        "Jazz" to listOf(3f, 1f, 2f, 2f, 4f)
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("equalizer_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(scrollState)
        ) {
            // Header Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
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
                            text = "Égaliseur Audio (id=\"eq\")",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Moteur DSP 5 Bandes + Bass Boost",
                            fontSize = 12.sp,
                            color = CyanAccent
                        )
                    }
                }

                Switch(
                    checked = isEqEnabled,
                    onCheckedChange = { isEqEnabled = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = CyanAccent,
                        checkedTrackColor = CyanAccent.copy(alpha = 0.3f),
                        uncheckedThumbColor = TextMuted,
                        uncheckedTrackColor = SurfaceElevated
                    )
                )
            }

            // Morceau actif en cours de traitement DSP
            activeTrack?.let { track ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(SurfaceCard)
                        .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Equalizer,
                        contentDescription = null,
                        tint = CyanAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Profil actif pour : ${track.title}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${track.format} • ${track.bitrate} • 44.1 kHz Hi-Res",
                            fontSize = 10.sp,
                            color = TextSecondary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Presets horizontaux
            Text(
                text = "PRÉSÉLECTIONS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(presets) { (name, values) ->
                    val isSelected = selectedPreset == name
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                if (isSelected) CyanAccent.copy(alpha = 0.18f) else SurfaceCard
                            )
                            .border(
                                width = 1.dp,
                                color = if (isSelected) CyanAccent else BorderSubtle,
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clickable {
                                selectedPreset = name
                                values.forEachIndexed { i, v -> bandLevels[i] = v }
                            }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = name,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) CyanAccent else TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 5 Bandes Fréquentielles
            Text(
                text = "COURBE D'ÉGALISATION (dB)",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(SurfaceCard)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(20.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                bandFrequencies.forEachIndexed { index, freq ->
                    val level = bandLevels[index]
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = freq, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                            Text(
                                text = "${if (level > 0) "+" else ""}${level.toInt()} dB",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (level > 0) CyanAccent else if (level < 0) PurpleAccent else TextMuted
                            )
                        }

                        Slider(
                            value = level,
                            onValueChange = {
                                if (isEqEnabled) {
                                    bandLevels[index] = it
                                    selectedPreset = "Personnalisé"
                                }
                            },
                            valueRange = -10f..10f,
                            enabled = isEqEnabled,
                            colors = SliderDefaults.colors(
                                thumbColor = CyanAccent,
                                activeTrackColor = CyanAccent,
                                inactiveTrackColor = SurfaceElevated
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Effets Spéciaux : Bass Boost & Virtualiseur 3D
            Text(
                text = "EFFETS D'AMPLIFICATION",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Bass Boost Card
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceCard)
                        .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    Text(text = "Bass Boost", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(text = "${bassBoostLevel.toInt()}%", fontSize = 11.sp, color = CyanAccent)
                    Slider(
                        value = bassBoostLevel,
                        onValueChange = { bassBoostLevel = it },
                        valueRange = 0f..100f,
                        enabled = isEqEnabled,
                        colors = SliderDefaults.colors(
                            thumbColor = CyanAccent,
                            activeTrackColor = CyanAccent
                        )
                    )
                }

                // Virtualizer Card
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceCard)
                        .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    Text(text = "Spatial 3D", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(text = "${virtualizerLevel.toInt()}%", fontSize = 11.sp, color = PurpleAccent)
                    Slider(
                        value = virtualizerLevel,
                        onValueChange = { virtualizerLevel = it },
                        valueRange = 0f..100f,
                        enabled = isEqEnabled,
                        colors = SliderDefaults.colors(
                            thumbColor = PurpleAccent,
                            activeTrackColor = PurpleAccent
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
