package com.example.ui.screens.lyrics

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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.lyrics.LyricLine
import com.example.ui.theme.AmberAccent
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun GroqTransScreen(
    viewModel: MainViewModel,
    onBackClick: () -> Unit,
    onOpenGroqConfig: () -> Unit,
    onTranscriptionComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeTrack by viewModel.activeTrack.collectAsState()
    val groqApiKey by viewModel.groqApiKey.collectAsState()
    val groqModel by viewModel.groqModel.collectAsState()

    val context = LocalContext.current
    var isProcessing by remember { mutableStateOf(false) }
    var currentProgressText by remember { mutableStateOf("Prêt pour la transcription") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var transcriptionFinished by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val infiniteTransition = rememberInfiniteTransition(label = "pulseGroq")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "groqScale"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .statusBarsPadding()
            .testTag("groq_trans_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
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

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Transcription Groq AI",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = activeTrack?.title ?: "Fichier local",
                        fontSize = 12.sp,
                        color = AmberAccent
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Statut de la clé API
            if (groqApiKey.isBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceDark)
                        .border(1.dp, AmberAccent.copy(alpha = 0.40f), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Clé API non configurée",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = AmberAccent
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Ajoute ta clé API Groq pour lancer Whisper AI.",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(AmberAccent)
                                .clickable { onOpenGroqConfig() }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "CONFIGURER",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = BackgroundDark
                            )
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceDark)
                        .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Groq Whisper Prêt",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Modèle sélectionné : $groqModel",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }

                        IconButton(onClick = { onOpenGroqConfig() }) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Paramètres",
                                tint = TextSecondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Visuel central de transcription
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .scale(if (isProcessing) pulseScale else 1f)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    AmberAccent.copy(alpha = if (isProcessing) 0.35f else 0.15f),
                                    Color.Transparent
                                )
                            )
                        )
                        .border(
                            width = 2.dp,
                            color = if (isProcessing) AmberAccent else BorderSubtle,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = AmberAccent,
                        modifier = Modifier.size(56.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = if (isProcessing) currentProgressText
                           else if (transcriptionFinished) "Transcription terminée avec succès !"
                           else "Extraction et transcription des voix",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (isProcessing) "Génération des repères temporels LRC [mm:ss.xx]..."
                           else "Vitesse moyenne d'inférence Groq : ~1.2s",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )

                if (isProcessing) {
                    Spacer(modifier = Modifier.height(24.dp))
                    CircularProgressIndicator(color = AmberAccent, modifier = Modifier.size(28.dp))
                }

                errorMessage?.let { err ->
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF33151A))
                            .border(1.dp, Color(0xFFEF4444).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(
                                text = err,
                                fontSize = 12.sp,
                                color = Color(0xFFFCA5A5)
                            )
                            if (groqApiKey.isBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "👉 Ouvrir les paramètres pour renseigner la clé",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AmberAccent,
                                    modifier = Modifier.clickable { onOpenGroqConfig() }
                                )
                            }
                        }
                    }
                }
            }

            // Bouton Lancer la transcription
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .then(
                        if (isProcessing) {
                            Modifier.background(SurfaceElevated)
                        } else {
                            Modifier.background(
                                Brush.horizontalGradient(listOf(AmberAccent, PurpleAccent))
                            )
                        }
                    )
                    .clickable(enabled = !isProcessing) {
                        val track = activeTrack
                        if (track == null) {
                            errorMessage = "Aucun morceau sélectionné."
                            return@clickable
                        }
                        if (groqApiKey.isBlank()) {
                            errorMessage = "Clé API Groq non configurée. Rendez-vous dans les Paramètres pour entrer votre clé API Groq."
                            return@clickable
                        }

                        errorMessage = null
                        isProcessing = true
                        viewModel.transcribeTrackWithGroq(
                            track = track,
                            context = context,
                            onStatusUpdate = { status ->
                                currentProgressText = status
                            },
                            onSuccess = {
                                isProcessing = false
                                transcriptionFinished = true
                                onTranscriptionComplete()
                            },
                            onError = { err ->
                                isProcessing = false
                                errorMessage = err
                            }
                        )
                    }
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isProcessing) "TRANSCRIPTION EN COURS..." else "LANCER LA TRANSCRIPTION GROQ",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isProcessing) TextMuted else BackgroundDark
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
