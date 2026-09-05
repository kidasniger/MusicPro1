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

class CompactMusicWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val app = context.applicationContext as? MusicProApplication
        val playerManager = app?.container?.playerManager
        val track = playerManager?.activeTrack?.value
        val isPlaying = playerManager?.isPlaying?.value ?: false

        provideContent {
            GlanceTheme {
                CompactWidgetContent(
                    title = track?.title ?: "Aucune lecture",
                    artist = track?.artist ?: "MusicPro",
                    isPlaying = isPlaying
                )
            }
        }
    }

    @Composable
    private fun CompactWidgetContent(
        title: String,
        artist: String,
        isPlaying: Boolean
    ) {
        // Design wCompact : widget minimaliste sombre avec bords arrondis, pochette + infos + play/pause
        Row(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(Color(0xFF111113))
                .cornerRadius(16.dp)
                .padding(8.dp)
                .clickable(actionStartActivity<MainActivity>()),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Pochette miniature / Icône de note cyan
            Box(
                modifier = GlanceModifier
                    .size(48.dp)
                    .background(Color(0xFF1A1A24))
                    .cornerRadius(10.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    provider = ImageProvider(R.drawable.ic_widget_music),
                    contentDescription = "Pochette",
                    modifier = GlanceModifier.size(26.dp)
                )
            }

            Spacer(modifier = GlanceModifier.width(10.dp))

            // Titre & Artiste
            Column(
                modifier = GlanceModifier.defaultWeight()
            ) {
                Text(
                    text = title,
                    style = TextStyle(
                        color = androidx.glance.unit.ColorProvider(Color(0xFFF9FAFB)),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 1
                )
                Spacer(modifier = GlanceModifier.height(2.dp))
                Text(
                    text = artist,
                    style = TextStyle(
                        color = androidx.glance.unit.ColorProvider(Color(0xFF9CA3AF)),
                        fontSize = 11.sp
                    ),
                    maxLines = 1
                )
            }

            Spacer(modifier = GlanceModifier.width(8.dp))

            // Bouton Play/Pause circulaire
            Box(
                modifier = GlanceModifier
                    .size(38.dp)
                    .background(if (isPlaying) Color(0xFF22D3EE) else Color(0xFF27272A))
                    .cornerRadius(19.dp)
                    .clickable(actionRunCallback<PlayPauseActionCallback>()),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    provider = ImageProvider(
                        if (isPlaying) R.drawable.ic_widget_pause else R.drawable.ic_widget_play
                    ),
                    contentDescription = if (isPlaying) "Pause" else "Lecture",
                    modifier = GlanceModifier.size(20.dp)
                )
            }
        }
    }
}

class CompactMusicWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = CompactMusicWidget()
}
