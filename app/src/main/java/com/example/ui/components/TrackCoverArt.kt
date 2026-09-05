package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.PurpleDeep
import com.example.ui.theme.SurfacePurpleTint

fun parseGradientString(gradientStr: String?): Brush {
    if (gradientStr == null) {
        return Brush.linearGradient(listOf(CyanAccent, PurpleAccent))
    }
    return when {
        gradientStr.contains("#ff3a3a") -> Brush.linearGradient(
            listOf(Color(0xFFFF3A3A), Color(0xFF7A0A0A))
        )
        gradientStr.contains("#22D3EE") -> Brush.linearGradient(
            listOf(Color(0xFF22D3EE), Color(0xFFA855F7))
        )
        gradientStr.contains("#f472b6") -> Brush.linearGradient(
            listOf(Color(0xFFF472B6), Color(0xFF5B21B6))
        )
        gradientStr.contains("#facc15") -> Brush.linearGradient(
            listOf(Color(0xFFFACC15), Color(0xFFEA580C))
        )
        gradientStr.contains("#a78bfa") -> Brush.linearGradient(
            listOf(Color(0xFFA78BFA), Color(0xFF1E1B4B))
        )
        gradientStr.contains("#10b981") -> Brush.linearGradient(
            listOf(Color(0xFF10B981), Color(0xFF064E3B))
        )
        else -> Brush.linearGradient(listOf(CyanAccent, PurpleDeep))
    }
}

@Composable
fun TrackCoverArt(
    gradientStr: String?,
    title: String,
    size: Dp = 48.dp,
    shape: Shape = RoundedCornerShape(12.dp),
    isVinyl: Boolean = false,
    modifier: Modifier = Modifier
) {
    val brush = parseGradientString(gradientStr)

    if (isVinyl) {
        // Vinyle en forme de cercle avec sillon et trou central
        Box(
            modifier = modifier
                .size(size)
                .clip(CircleShape)
                .background(brush)
                .border(2.dp, Color.White.copy(alpha = 0.20f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            // Sillon décoratif externe
            Box(
                modifier = Modifier
                    .size(size * 0.70f)
                    .clip(CircleShape)
                    .border(1.dp, Color.Black.copy(alpha = 0.35f), CircleShape)
            )
            // Trou central du vinyle
            Box(
                modifier = Modifier
                    .size(size * 0.28f)
                    .clip(CircleShape)
                    .background(SurfacePurpleTint)
                    .border(1.dp, Color.White.copy(alpha = 0.40f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(size * 0.10f)
                        .clip(CircleShape)
                        .background(Color.White)
                )
            }
        }
    } else {
        // Pochette carrée avec angles arrondis
        Box(
            modifier = modifier
                .size(size)
                .clip(shape)
                .background(brush)
                .border(1.dp, Color.White.copy(alpha = 0.12f), shape),
            contentAlignment = Alignment.Center
        ) {
            val initial = title.firstOrNull()?.uppercaseChar()?.toString() ?: "M"
            Text(
                text = initial,
                color = Color.White.copy(alpha = 0.90f),
                fontWeight = FontWeight.Black,
                fontSize = (size.value * 0.42f).sp
            )
            Icon(
                imageVector = Icons.Default.MusicNote,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.40f),
                modifier = Modifier
                    .size(size * 0.28f)
                    .align(Alignment.BottomEnd)
            )
        }
    }
}
