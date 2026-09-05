package com.example.ui.screens.states

import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.EmptyStateComposable
import com.example.ui.components.EmptyStateType
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceGlass
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.MainViewModel

/**
 * Galerie des états vides (id="emptyStates"), conforme à la maquette.
 * Permet de visualiser et tester l'ensemble des états vides :
 * Bibliothèque, Recherche, Favoris, File, Playlists, Paroles (lyricsEmpty), Historique.
 */
@Composable
fun EmptyStatesGalleryScreen(
    viewModel: MainViewModel,
    onBackClick: () -> Unit,
    onNavigateToHome: () -> Unit = {},
    onNavigateToSearch: () -> Unit = {},
    onNavigateToPlaylists: () -> Unit = {},
    onNavigateToGroqConfig: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedType by remember { mutableStateOf(EmptyStateType.LIBRARY) }

    val emptyTypes = listOf(
        Pair("Bibliothèque", EmptyStateType.LIBRARY),
        Pair("Recherche", EmptyStateType.SEARCH),
        Pair("Favoris", EmptyStateType.FAVORITES),
        Pair("File de lecture", EmptyStateType.QUEUE),
        Pair("Playlists", EmptyStateType.PLAYLISTS),
        Pair("Paroles (lyricsEmpty)", EmptyStateType.LYRICS),
        Pair("Historique", EmptyStateType.HISTORY)
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .statusBarsPadding()
            .testTag("empty_states_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                            text = "États Vides",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                        Text(
                            text = "id=\"emptyStates\" • Modèle unifié & paramétrable",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sélecteur de type
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(emptyTypes) { (label, type) ->
                    val isSelected = selectedType == type
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(9999.dp))
                            .background(
                                if (isSelected) {
                                    Brush.horizontalGradient(listOf(CyanAccent, PurpleAccent))
                                } else {
                                    Brush.linearGradient(listOf(SurfaceDark, SurfaceDark))
                                }
                            )
                            .border(
                                width = 1.dp,
                                color = if (isSelected) Color.Transparent else BorderSubtle,
                                shape = RoundedCornerShape(9999.dp)
                            )
                            .clickable { selectedType = type }
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                            .testTag("empty_type_$label")
                    ) {
                        Text(
                            text = label,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                            color = if (isSelected) Color.White else TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Composable d'état vide unifié rendu
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                contentAlignment = Alignment.Center
            ) {
                EmptyStateComposable(
                    type = selectedType,
                    onPrimaryAction = {
                        when (selectedType) {
                            EmptyStateType.LIBRARY -> {
                                viewModel.scanLocalMusic()
                                Toast.makeText(context, "Scan du stockage lancé...", Toast.LENGTH_SHORT).show()
                            }
                            EmptyStateType.SEARCH -> {
                                onNavigateToSearch()
                            }
                            EmptyStateType.FAVORITES -> {
                                onNavigateToHome()
                            }
                            EmptyStateType.QUEUE -> {
                                val tracks = viewModel.allTracks.value
                                if (tracks.isNotEmpty()) {
                                    viewModel.playTrack(tracks.random())
                                    Toast.makeText(context, "Lecture aléatoire démarrée", Toast.LENGTH_SHORT).show()
                                }
                            }
                            EmptyStateType.PLAYLISTS -> {
                                onNavigateToPlaylists()
                            }
                            EmptyStateType.LYRICS -> {
                                Toast.makeText(context, "Sélection de la source...", Toast.LENGTH_SHORT).show()
                            }
                            EmptyStateType.HISTORY -> {
                                onNavigateToHome()
                            }
                            else -> {}
                        }
                    },
                    onSecondaryAction = if (selectedType == EmptyStateType.LYRICS) {
                        {
                            Toast.makeText(context, "Ouverture de la recherche LRCLIB...", Toast.LENGTH_SHORT).show()
                        }
                    } else null,
                    onTertiaryAction = if (selectedType == EmptyStateType.LYRICS) {
                        {
                            onNavigateToGroqConfig()
                        }
                    } else null
                )
            }
        }
    }
}
