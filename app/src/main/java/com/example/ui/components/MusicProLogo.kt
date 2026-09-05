package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.SurfacePurpleTint
import com.example.ui.theme.TextPrimary

@Composable
fun MusicProLogo(
    size: Dp = 48.dp,
    withText: Boolean = true,
    variant: String = "default",
    modifier: Modifier = Modifier
) {
    val cornerRadius = if (variant == "mono") 12.dp else (size.value * 0.32f).dp
    val shape = RoundedCornerShape(cornerRadius)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        // Logo Badge
        Box(
            modifier = Modifier
                .size(size)
                .shadow(
                    elevation = if (variant == "mono") 0.dp else (size.value * 0.3f).dp,
                    shape = shape,
                    spotColor = PurpleAccent,
                    ambientColor = CyanAccent
                )
                .clip(shape)
                .background(
                    if (variant == "mono") {
                        Brush.linearGradient(listOf(Color.White, Color.White))
                    } else {
                        Brush.linearGradient(
                            listOf(CyanAccent, PurpleAccent)
                        )
                    }
                )
                .padding(2.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape((cornerRadius.value - 2).coerceAtLeast(4f).dp))
                    .background(SurfacePurpleTint),
                contentAlignment = Alignment.Center
            ) {
                // Subtle gradient overlay inside the squircle
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    CyanAccent.copy(alpha = 0.20f),
                                    PurpleAccent.copy(alpha = 0.20f)
                                )
                            )
                        )
                )

                // Letter M + cyan dot
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "M",
                        fontSize = (size.value * 0.42f).sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = (-0.5).sp
                    )

                    // Signature cyan dot above the right bar of M
                    val dotSize = (size.value * 0.09f).coerceAtLeast(3f).dp
                    val dotOffsetX = (size.value * 0.21f).dp
                    val dotOffsetY = (-size.value * 0.13f).dp

                    Box(
                        modifier = Modifier
                            .offset(x = dotOffsetX, y = dotOffsetY)
                            .size(dotSize)
                            .shadow(6.dp, CircleShape, spotColor = CyanAccent)
                            .clip(CircleShape)
                            .background(CyanAccent)
                    )
                }

                // Small decorative music icon in bottom right (Gr in prototype)
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.80f),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding((size.value * 0.12f).dp)
                        .size((size.value * 0.22f).dp)
                )
            }
        }

        // Text "MUSIC PRO"
        if (withText) {
            Row(
                modifier = Modifier.padding(start = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "MUSIC",
                    fontSize = (size.value * 0.52f).sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-1.5).sp,
                    color = TextPrimary
                )
                Text(
                    text = "PRO",
                    fontSize = (size.value * 0.52f).sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-1.5).sp,
                    style = TextStyle(
                        brush = Brush.horizontalGradient(
                            listOf(CyanAccent, PurpleAccent)
                        )
                    )
                )
            }
        }
    }
}
