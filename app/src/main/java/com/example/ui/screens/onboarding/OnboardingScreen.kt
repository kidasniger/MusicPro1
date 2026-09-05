package com.example.ui.screens.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.PurpleDeep
import com.example.ui.theme.SurfacePurpleTint
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun OnboardingScreen(
    onOnboardingFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentStep by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp)
            .testTag("onboarding_screen"),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // En-tête : Stepper + Bouton PASSER
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(3) { index ->
                    val isActive = index == currentStep
                    Box(
                        modifier = Modifier
                            .height(4.dp)
                            .width(if (isActive) 32.dp else 24.dp)
                            .clip(CircleShape)
                            .background(
                                if (isActive) {
                                    Brush.horizontalGradient(listOf(CyanAccent, PurpleAccent))
                                } else {
                                    Brush.linearGradient(
                                        listOf(
                                            Color.White.copy(alpha = 0.12f),
                                            Color.White.copy(alpha = 0.12f)
                                        )
                                    )
                                }
                            )
                    )
                }
            }

            Text(
                text = "PASSER",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = TextMuted,
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onOnboardingFinished() }
                    .padding(8.dp)
                    .testTag("onboarding_skip_button")
            )
        }

        // Slide Content animé
        AnimatedContent(
            targetState = currentStep,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "onboardingSlide",
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) { step ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                when (step) {
                    0 -> OnboardingStepOne()
                    1 -> OnboardingStepTwo()
                    2 -> OnboardingStepThree()
                }
            }
        }

        // Bouton Suivant / Commencer
        Button(
            onClick = {
                if (currentStep < 2) {
                    currentStep++
                } else {
                    onOnboardingFinished()
                }
            },
            shape = RoundedCornerShape(9999.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .shadow(
                    elevation = 24.dp,
                    shape = RoundedCornerShape(9999.dp),
                    spotColor = PurpleAccent.copy(alpha = 0.40f)
                )
                .background(
                    Brush.horizontalGradient(listOf(CyanAccent, PurpleAccent)),
                    shape = RoundedCornerShape(9999.dp)
                )
                .testTag("onboarding_next_button")
        ) {
            Text(
                text = if (currentStep < 2) "Suivant" else "Commencer",
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp,
                color = Color.White
            )
        }
    }
}

@Composable
private fun OnboardingStepOne() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.widthIn(max = 320.dp)
    ) {
        // Carte d'illustration
        Box(
            modifier = Modifier
                .size(224.dp)
                .shadow(
                    elevation = 40.dp,
                    shape = RoundedCornerShape(32.dp),
                    spotColor = PurpleAccent.copy(alpha = 0.25f)
                )
                .clip(RoundedCornerShape(32.dp))
                .background(
                    Brush.linearGradient(
                        listOf(SurfacePurpleTint, BackgroundDark)
                    )
                )
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.10f),
                    shape = RoundedCornerShape(32.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Folder,
                contentDescription = null,
                tint = CyanAccent,
                modifier = Modifier.size(64.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Titre
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Ta musique,",
                fontSize = 30.sp,
                fontWeight = FontWeight.Black,
                color = TextPrimary,
                lineHeight = 30.sp,
                letterSpacing = (-0.5).sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = "partout local",
                fontSize = 30.sp,
                fontWeight = FontWeight.Black,
                lineHeight = 30.sp,
                letterSpacing = (-0.5).sp,
                style = TextStyle(
                    brush = Brush.horizontalGradient(
                        listOf(CyanAccent, PurpleAccent)
                    )
                ),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Description
        Text(
            text = "Aucun cloud. Tes fichiers MP3, FLAC, M4A directement depuis le stockage. Ultra rapide.",
            fontSize = 14.sp,
            color = TextSecondary,
            lineHeight = 22.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(max = 280.dp)
        )
    }
}

@Composable
private fun OnboardingStepTwo() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.widthIn(max = 320.dp)
    ) {
        // Carte d'illustration
        Box(
            modifier = Modifier
                .size(224.dp)
                .shadow(
                    elevation = 40.dp,
                    shape = RoundedCornerShape(32.dp),
                    spotColor = CyanAccent.copy(alpha = 0.25f)
                )
                .clip(RoundedCornerShape(32.dp))
                .background(
                    Brush.linearGradient(
                        listOf(SurfacePurpleTint, BackgroundDark)
                    )
                )
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.10f),
                    shape = RoundedCornerShape(32.dp)
                )
        ) {
            // Gradient interne
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            listOf(
                                CyanAccent.copy(alpha = 0.10f),
                                PurpleAccent.copy(alpha = 0.10f)
                            )
                        )
                    )
            )

            // Lignes de paroles simulées
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(start = 24.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Box(
                    modifier = Modifier
                        .height(8.dp)
                        .width(80.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.20f))
                )
                Box(
                    modifier = Modifier
                        .height(12.dp)
                        .width(128.dp)
                        .shadow(12.dp, CircleShape, spotColor = CyanAccent)
                        .clip(CircleShape)
                        .background(Color.White)
                )
                Box(
                    modifier = Modifier
                        .height(8.dp)
                        .width(96.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.30f))
                )
            }

            // Icône micro en bas à droite
            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = null,
                tint = CyanAccent,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp)
                    .size(26.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Titre
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Paroles",
                fontSize = 30.sp,
                fontWeight = FontWeight.Black,
                color = TextPrimary,
                lineHeight = 30.sp,
                letterSpacing = (-0.5).sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = "synchronisées",
                fontSize = 30.sp,
                fontWeight = FontWeight.Black,
                color = CyanAccent,
                lineHeight = 30.sp,
                letterSpacing = (-0.5).sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = "intelligentes",
                fontSize = 30.sp,
                fontWeight = FontWeight.Black,
                color = TextPrimary,
                lineHeight = 30.sp,
                letterSpacing = (-0.5).sp,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Description
        Text(
            text = "Auto-scroll millimétré, ligne active pulsante, traduction conservant les timestamps LRC.",
            fontSize = 14.sp,
            color = TextSecondary,
            lineHeight = 22.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(max = 280.dp)
        )
    }
}

@Composable
private fun OnboardingStepThree() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.widthIn(max = 320.dp)
    ) {
        // Carte d'illustration
        Box(
            modifier = Modifier
                .size(224.dp)
                .shadow(
                    elevation = 40.dp,
                    shape = RoundedCornerShape(32.dp),
                    spotColor = AmberAccent.copy(alpha = 0.20f)
                )
                .clip(RoundedCornerShape(32.dp))
                .background(
                    Brush.linearGradient(
                        listOf(SurfacePurpleTint, BackgroundDark)
                    )
                )
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.10f),
                    shape = RoundedCornerShape(32.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Box 1: LRCLIB (Nuage)
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(PurpleAccent.copy(alpha = 0.20f))
                        .border(1.dp, PurpleAccent.copy(alpha = 0.30f), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Cloud,
                        contentDescription = "LRCLIB",
                        tint = PurpleAccent,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Box 2: Groq (Puce CPU / IA)
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(AmberAccent.copy(alpha = 0.20f))
                        .border(1.dp, AmberAccent.copy(alpha = 0.30f), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Memory,
                        contentDescription = "Groq",
                        tint = AmberAccent,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Titre
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "LRCLIB +",
                fontSize = 30.sp,
                fontWeight = FontWeight.Black,
                color = TextPrimary,
                lineHeight = 30.sp,
                letterSpacing = (-0.5).sp,
                textAlign = TextAlign.Center
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Groq",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Black,
                    color = AmberAccent,
                    lineHeight = 30.sp,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = " Whisper",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary,
                    lineHeight = 30.sp,
                    letterSpacing = (-0.5).sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Description
        Text(
            text = "Récupération automatique LRCLIB, transcription Whisper large-v3 si introuvable, traduction Llama-3.",
            fontSize = 14.sp,
            color = TextSecondary,
            lineHeight = 22.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(max = 280.dp)
        )
    }
}
