package com.example.ui.screens.player

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

data class EqualizerPreset(
    val name: String,
    val gains: List<Float> // 5 bandes en dB (-10 dB à +10 dB)
)

val DEFAULT_PRESETS = listOf(
    EqualizerPreset("Plat", listOf(0f, 0f, 0f, 0f, 0f)),
    EqualizerPreset("Bass Boost", listOf(6f, 4f, 1f, 0f, -1f)),
    EqualizerPreset("Vocal", listOf(-1f, 1f, 5f, 3f, 1f)),
    EqualizerPreset("Pop", listOf(2f, 4f, 5f, 2f, 1f)),
    EqualizerPreset("Rock", listOf(5f, 3f, -1f, 2f, 4f)),
    EqualizerPreset("Électro", listOf(6f, 3f, 0f, 3f, 5f)),
    EqualizerPreset("Jazz", listOf(3f, 2f, 1f, 2f, 3f))
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EqualizerSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isEnabled by remember { mutableStateOf(true) }
    var selectedPreset by remember { mutableStateOf("Plat") }
    var bassBoostLevel by remember { mutableFloatStateOf(40f) } // 0 à 100%
    var virtualizerLevel by remember { mutableFloatStateOf(25f) } // 0 à 100%

    // 5 bandes de fréquences : 60Hz, 230Hz, 910Hz, 3.6kHz, 14kHz
    val bandLabels = listOf("60 Hz", "230 Hz", "910 Hz", "3.6 kHz", "14 kHz")
    val bandGains = remember { mutableStateListOf(0f, 0f, 0f, 0f, 0f) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfaceDark,
        modifier = modifier.testTag("equalizer_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Header avec Switch On/Off
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(PurpleAccent.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Equalizer,
                            contentDescription = null,
                            tint = PurpleAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "ÉGALISEUR AUDIO",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = if (isEnabled) "5 bandes actives • Profil $selectedPreset" else "Désactivé (Signal Direct)",
                            fontSize = 11.sp,
                            color = if (isEnabled) CyanAccent else TextMuted
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(
                        checked = isEnabled,
                        onCheckedChange = { isEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = CyanAccent,
                            uncheckedThumbColor = TextMuted,
                            uncheckedTrackColor = SurfaceCard
                        )
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fermer",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Liste horizontale des Préréglages (Presets)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(DEFAULT_PRESETS) { preset ->
                    val isSelected = selectedPreset == preset.name
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isSelected) Brush.horizontalGradient(listOf(CyanAccent, PurpleAccent))
                                else Brush.linearGradient(listOf(SurfaceCard, SurfaceCard))
                            )
                            .border(
                                1.dp,
                                if (isSelected) Color.Transparent else BorderSubtle,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable(enabled = isEnabled) {
                                selectedPreset = preset.name
                                preset.gains.forEachIndexed { i, g ->
                                    if (i in bandGains.indices) bandGains[i] = g
                                }
                            }
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = preset.name,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Panneau des 5 bandes verticales
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceCard)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    bandLabels.forEachIndexed { index, label ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            val gain = bandGains.getOrElse(index) { 0f }

                            // Valeur en dB
                            Text(
                                text = "${if (gain > 0) "+" else ""}${gain.toInt()} dB",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isEnabled) CyanAccent else TextMuted
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            // Curseur Slider Vertical
                            Slider(
                                value = gain,
                                onValueChange = { newGain ->
                                    if (isEnabled) {
                                        bandGains[index] = newGain
                                        selectedPreset = "Personnalisé"
                                    }
                                },
                                valueRange = -10f..10f,
                                enabled = isEnabled,
                                colors = SliderDefaults.colors(
                                    thumbColor = CyanAccent,
                                    activeTrackColor = CyanAccent,
                                    inactiveTrackColor = SurfaceElevated
                                ),
                                modifier = Modifier.height(110.dp)
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            // Fréquence de la bande
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                color = TextMuted,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Effets additionnels : Bass Boost & Virtualiseur 3D
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Bass Boost Card
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(SurfaceCard)
                        .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Bass Boost",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "${bassBoostLevel.toInt()}%",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyanAccent
                        )
                    }

                    Slider(
                        value = bassBoostLevel,
                        onValueChange = { bassBoostLevel = it },
                        valueRange = 0f..100f,
                        enabled = isEnabled,
                        colors = SliderDefaults.colors(
                            thumbColor = CyanAccent,
                            activeTrackColor = CyanAccent,
                            inactiveTrackColor = SurfaceElevated
                        ),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                // Spatial / Virtualizer Card
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(SurfaceCard)
                        .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Surround 3D",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "${virtualizerLevel.toInt()}%",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PurpleAccent
                        )
                    }

                    Slider(
                        value = virtualizerLevel,
                        onValueChange = { virtualizerLevel = it },
                        valueRange = 0f..100f,
                        enabled = isEnabled,
                        colors = SliderDefaults.colors(
                            thumbColor = PurpleAccent,
                            activeTrackColor = PurpleAccent,
                            inactiveTrackColor = SurfaceElevated
                        ),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}
