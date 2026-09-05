package com.example.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.example.MainActivity
import com.example.MusicProApplication
import com.example.R

class StandardMusicWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val app = context.applicationContext as? MusicProApplication
        val playerManager = app?.container?.playerManager
        val track = playerManager?.activeTrack?.value
        val isPlaying = playerManager?.isPlaying?.value ?: false
        val currentPos = playerManager?.currentPositionMs?.value ?: 0L
        val duration = (playerManager?.durationMs?.value ?: 200_000L).coerceAtLeast(1L)
        val progressRatio = (currentPos.toFloat() / duration).coerceIn(0f, 1f)

        provideContent {
            GlanceTheme {
                StandardWidgetContent(
                    title = track?.title ?: "Aucune lecture",
                    artist = track?.artist ?: "MusicPro",
                    album = track?.album ?: "",
                    isPlaying = isPlaying,
                    progressRatio = progressRatio
                )
            }
        }
    }

    @Composable
    private fun StandardWidgetContent(
        title: String,
        artist: String,
        album: String,
        isPlaying: Boolean,
        progressRatio: Float
    ) {
        // Design wStandard : plus grand, titre, artiste, album, barre de progression visuelle, contrôles complets (prev, play/pause, next)
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(Color(0xFF111113))
                .cornerRadius(20.dp)
                .padding(14.dp)
                .clickable(actionStartActivity<MainActivity>()),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Pochette
                Box(
                    modifier = GlanceModifier
                        .size(62.dp)
                        .background(Color(0xFF1E1430))
                        .cornerRadius(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        provider = ImageProvider(R.drawable.ic_widget_music),
                        contentDescription = "Pochette",
                        modifier = GlanceModifier.size(32.dp)
                    )
                }

                Spacer(modifier = GlanceModifier.width(12.dp))

                // Métadonnées
                Column(
                    modifier = GlanceModifier.defaultWeight()
                ) {
                    Text(
                        text = title,
                        style = TextStyle(
                            color = androidx.glance.unit.ColorProvider(Color(0xFFF9FAFB)),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        maxLines = 1
                    )
                    Spacer(modifier = GlanceModifier.height(2.dp))
                    Text(
                        text = artist,
                        style = TextStyle(
                            color = androidx.glance.unit.ColorProvider(Color(0xFF22D3EE)),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        maxLines = 1
                    )
                    Spacer(modifier = GlanceModifier.height(2.dp))
                    Text(
                        text = album,
                        style = TextStyle(
                            color = androidx.glance.unit.ColorProvider(Color(0xFF6B7280)),
                            fontSize = 10.sp
                        ),
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = GlanceModifier.height(10.dp))

            // Ligne de progression épurée
            Box(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(Color(0xFF27272A))
                    .cornerRadius(2.dp)
            ) {
                Spacer(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(Color(0xFF22D3EE))
                        .cornerRadius(2.dp)
                )
            }

            Spacer(modifier = GlanceModifier.height(10.dp))

            // Contrôles complets : Précédent, Lecture/Pause, Suivant
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Précédent
                Box(
                    modifier = GlanceModifier
                        .size(36.dp)
                        .background(Color(0xFF1C1C20))
                        .cornerRadius(18.dp)
                        .clickable(actionRunCallback<PreviousTrackActionCallback>()),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        provider = ImageProvider(R.drawable.ic_widget_prev),
                        contentDescription = "Précédent",
                        modifier = GlanceModifier.size(18.dp)
                    )
                }

                Spacer(modifier = GlanceModifier.width(18.dp))

                // Play / Pause central
                Box(
                    modifier = GlanceModifier
                        .size(46.dp)
                        .background(if (isPlaying) Color(0xFF22D3EE) else Color(0xFFA855F7))
                        .cornerRadius(23.dp)
                        .clickable(actionRunCallback<PlayPauseActionCallback>()),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        provider = ImageProvider(
                            if (isPlaying) R.drawable.ic_widget_pause else R.drawable.ic_widget_play
                        ),
                        contentDescription = if (isPlaying) "Pause" else "Lecture",
                        modifier = GlanceModifier.size(24.dp)
                    )
                }

                Spacer(modifier = GlanceModifier.width(18.dp))

                // Suivant
                Box(
                    modifier = GlanceModifier
                        .size(36.dp)
                        .background(Color(0xFF1C1C20))
                        .cornerRadius(18.dp)
                        .clickable(actionRunCallback<NextTrackActionCallback>()),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        provider = ImageProvider(R.drawable.ic_widget_next),
                        contentDescription = "Suivant",
                        modifier = GlanceModifier.size(18.dp)
                    )
                }
            }
        }
    }
}

class StandardMusicWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = StandardMusicWidget()
}
