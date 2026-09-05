package com.example.ui.screens.splash

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.MusicProLogo
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.PurpleDeep
import com.example.ui.theme.SurfacePurpleTint
import com.example.ui.theme.TextMuted
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "splashPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logoPulse"
    )

    val haloAlpha by infiniteTransition.animateFloat(
        initialValue = 0.30f,
        targetValue = 0.50f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "haloAlpha"
    )

    LaunchedEffect(Unit) {
        delay(2200)
        onSplashFinished()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .testTag("splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        // Halo 1: w-[600px] h-[600px] rounded-full blur-[80px] opacity-40 bg-gradient-to-br from-[#22D3EE]/30 via-[#A855F7]/30 to-[#5B21B6]/20
        Box(
            modifier = Modifier
                .size(420.dp)
                .blur(80.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(
                            CyanAccent.copy(alpha = haloAlpha * 0.7f),
                            PurpleAccent.copy(alpha = haloAlpha * 0.8f),
                            PurpleDeep.copy(alpha = haloAlpha * 0.5f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Halo 2: w-[400px] h-[400px] rounded-full blur-[60px] bg-[#150B29]
        Box(
            modifier = Modifier
                .size(280.dp)
                .blur(60.dp)
                .clip(CircleShape)
                .background(SurfacePurpleTint.copy(alpha = 0.85f))
        )

        // Center element: animate-[pulse_2.2s_ease-in-out_infinite] on logo + subtitle + divider
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            Box(
                modifier = Modifier.scale(pulseScale),
                contentAlignment = Alignment.Center
            ) {
                MusicProLogo(
                    size = 96.dp,
                    withText = true
                )
            }

            Text(
                text = "LECTEUR LOCAL PREMIUM",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 4.sp,
                color = TextMuted,
                modifier = Modifier.padding(top = 32.dp)
            )

            // mt-12 w-32 h-[2px] bg-gradient-to-r from-transparent via-[#22D3EE]/50 to-transparent
            Box(
                modifier = Modifier
                    .padding(top = 48.dp)
                    .width(128.dp)
                    .height(2.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color.Transparent,
                                CyanAccent.copy(alpha = 0.50f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }

        // absolute bottom-10 text-[10px] text-[#6B7280] tracking-widest: v2.4.0 • LRCLIB + GROQ
        Text(
            text = "v2.4.0 • LRCLIB + GROQ",
            fontSize = 10.sp,
            fontWeight = FontWeight.Normal,
            letterSpacing = 2.sp,
            color = TextMuted,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 36.dp)
        )
    }
}
