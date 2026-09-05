package com.example.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

enum class BottomNavDestination(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val isElevated: Boolean = false
) {
    HOME("home", "Accueil", Icons.Default.Home),
    SEARCH("search", "Recherche", Icons.Default.Search),
    LYRICS("lyrics", "Paroles", Icons.Default.Mic, isElevated = true),
    PLAYLISTS("playlists", "Playlists", Icons.Default.QueueMusic),
    SETTINGS("settings", "Paramètres", Icons.Default.Settings)
}

@Composable
fun MusicProBottomBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .background(BackgroundDark.copy(alpha = 0.95f))
            .testTag("musicpro_bottom_bar")
    ) {
        HorizontalDivider(
            thickness = 1.dp,
            color = BorderSubtle
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavDestination.values().forEach { destination ->
                val isSelected = currentRoute == destination.route

                if (destination.isElevated) {
                    // Bouton central surélevé pour "Paroles"
                    Column(
                        modifier = Modifier
                            .offset(y = (-16).dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onNavigate(destination.route) }
                            .testTag("nav_item_${destination.route}"),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .shadow(
                                    elevation = 16.dp,
                                    shape = CircleShape,
                                    spotColor = PurpleAccent,
                                    ambientColor = CyanAccent
                                )
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(CyanAccent, PurpleAccent)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = destination.icon,
                                contentDescription = destination.label,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Text(
                            text = destination.label,
                            fontSize = 9.sp,
                            color = if (isSelected) TextPrimary else TextMuted,
                            modifier = Modifier.offset(y = 4.dp)
                        )
                    }
                } else {
                    // Boutons standards
                    Column(
                        modifier = Modifier
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onNavigate(destination.route) }
                            .testTag("nav_item_${destination.route}"),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) Color.White.copy(alpha = 0.10f) else Color.Transparent),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = destination.icon,
                                contentDescription = destination.label,
                                tint = if (isSelected) Color.White else TextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Text(
                            text = destination.label,
                            fontSize = 9.sp,
                            color = if (isSelected) Color.White else TextMuted
                        )
                    }
                }
            }
        }
    }
}
