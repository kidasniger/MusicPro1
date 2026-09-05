package com.example.ui.screens.lyrics

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SourceSheetScreen(
    sheetState: SheetState,
    currentSource: String,
    onDismiss: () -> Unit,
    onSelectLrclibAuto: () -> Unit,
    onSelectLrclibManual: () -> Unit,
    onSelectGroqWhisper: () -> Unit,
    onSelectTimingSync: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfaceDark,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 8.dp)
                    .size(width = 36.dp, height = 4.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.20f))
            )
        },
        modifier = modifier.testTag("source_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Source des paroles",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Sélectionne le fournisseur ou l'outil d'IA",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(SurfaceElevated)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Fermer",
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Option 1 : LRCLIB Synchronisation Automatique
            SourceOptionItem(
                icon = Icons.Default.CloudDownload,
                iconColor = CyanAccent,
                title = "LRCLIB Automatique",
                subtitle = "Base communautaire libre • Synchronisé",
                tag = "98% MATCH",
                tagColor = CyanAccent,
                isSelected = currentSource.contains("LRCLIB", ignoreCase = true),
                onClick = {
                    onSelectLrclibAuto()
                    onDismiss()
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Option 2 : Recherche Manuelle LRCLIB
            SourceOptionItem(
                icon = Icons.Default.Search,
                iconColor = PurpleAccent,
                title = "Recherche manuelle LRCLIB",
                subtitle = "Trouve des versions alternatives ou live",
                tag = null,
                tagColor = null,
                isSelected = false,
                onClick = {
                    onDismiss()
                    onSelectLrclibManual()
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Option 3 : Transcription IA Groq Whisper
            SourceOptionItem(
                icon = Icons.Default.AutoAwesome,
                iconColor = AmberAccent,
                title = "Transcription Groq AI",
                subtitle = "Whisper large-v3 ultra-rapide (< 1.5s)",
                tag = "GROQ API",
                tagColor = AmberAccent,
                isSelected = currentSource.contains("Groq", ignoreCase = true),
                onClick = {
                    onDismiss()
                    onSelectGroqWhisper()
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Option 4 : Calibrer le timing / Décalage
            SourceOptionItem(
                icon = Icons.Default.Tune,
                iconColor = Color.White,
                title = "Ajuster la synchronisation",
                subtitle = "Décaler les paroles de ±0.5s ou éditer",
                tag = "TIMING",
                tagColor = TextSecondary,
                isSelected = false,
                onClick = {
                    onDismiss()
                    onSelectTimingSync()
                }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SourceOptionItem(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    subtitle: String,
    tag: String?,
    tagColor: Color?,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) SurfaceElevated else SurfaceDark)
            .border(
                width = 1.dp,
                color = if (isSelected) iconColor.copy(alpha = 0.50f) else BorderSubtle,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icône source
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(iconColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Textes
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                if (tag != null && tagColor != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(tagColor.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = tag,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = tagColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = TextSecondary
            )
        }

        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(iconColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = SurfaceDark,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
