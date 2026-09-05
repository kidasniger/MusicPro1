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
import com.example.ui.components.ErrorStateComposable
import com.example.ui.components.ErrorVariant
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
 * Galerie des états d'erreur (id="errorStates"), conforme à la maquette.
 * Permet de visualiser et tester les différentes variantes d'erreur
 * (réseau LRCLIB, Groq indisponible, erreur de lecture fichier, générique).
 */
@Composable
fun ErrorStatesGalleryScreen(
    viewModel: MainViewModel,
    onBackClick: () -> Unit,
    onNavigateToGroqConfig: () -> Unit = {},
    onNavigateToOffline: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedVariant by remember { mutableStateOf(ErrorVariant.NETWORK_LRCLIB) }

    val errorVariants = listOf(
        Pair("LRCLIB Réseau", ErrorVariant.NETWORK_LRCLIB),
        Pair("Groq Whisper IA", ErrorVariant.GROQ_UNAVAILABLE),
        Pair("Lecture Audio", ErrorVariant.AUDIO_PLAYBACK),
        Pair("Générique", ErrorVariant.GENERIC)
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .statusBarsPadding()
            .testTag("error_states_screen")
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
                            text = "États d'Erreur",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                        Text(
                            text = "id=\"errorStates\" • Réseau, IA & Média",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sélecteur de variante
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(errorVariants) { (label, variant) ->
                    val isSelected = selectedVariant == variant
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
                            .clickable { selectedVariant = variant }
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                            .testTag("error_variant_$label")
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

            // Composable d'erreur rendu
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                contentAlignment = Alignment.Center
            ) {
                ErrorStateComposable(
                    variant = selectedVariant,
                    onRetry = {
                        Toast.makeText(context, "Nouvelle tentative en cours...", Toast.LENGTH_SHORT).show()
                    },
                    onSecondaryAction = {
                        when (selectedVariant) {
                            ErrorVariant.GROQ_UNAVAILABLE -> onNavigateToGroqConfig()
                            ErrorVariant.NETWORK_LRCLIB -> onNavigateToOffline()
                            ErrorVariant.AUDIO_PLAYBACK -> {
                                viewModel.next()
                                Toast.makeText(context, "Passage au morceau suivant", Toast.LENGTH_SHORT).show()
                            }
                            else -> {}
                        }
                    }
                )
            }
        }
    }
}
