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

class LyricsMusicWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val app = context.applicationContext as? MusicProApplication
        val playerManager = app?.container?.playerManager
        val track = playerManager?.activeTrack?.value
        val isPlaying = playerManager?.isPlaying?.value ?: false
        val currentLyric = "The city is my church, it wraps in the blinding twilight"

        provideContent {
            GlanceTheme {
                LyricsWidgetContent(
                    title = track?.title ?: "Midnight City",
                    artist = track?.artist ?: "M83",
                    lyricLine = currentLyric,
                    isPlaying = isPlaying
                )
            }
        }
    }

    @Composable
    private fun LyricsWidgetContent(
        title: String,
        artist: String,
        lyricLine: String,
        isPlaying: Boolean
    ) {
        // Design wLyrics : met en valeur la ligne de paroles en cours, avec le titre/artiste et contrôles rapides
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(Color(0xFF130D20)) // Teinte violet sombre élégante
                .cornerRadius(20.dp)
                .padding(14.dp)
                .clickable(actionStartActivity<MainActivity>()),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // En-tête : Titre & Artiste + Badge Paroles LRCLIB
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = GlanceModifier
                        .size(32.dp)
                        .background(Color(0xFF2A1B4E))
                        .cornerRadius(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        provider = ImageProvider(R.drawable.ic_widget_lyrics),
                        contentDescription = "Paroles",
                        modifier = GlanceModifier.size(18.dp)
                    )
                }

                Spacer(modifier = GlanceModifier.width(8.dp))

                Column(modifier = GlanceModifier.defaultWeight()) {
                    Text(
                        text = title,
                        style = TextStyle(
                            color = androidx.glance.unit.ColorProvider(Color(0xFFF9FAFB)),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        maxLines = 1
                    )
                    Text(
                        text = artist,
                        style = TextStyle(
                            color = androidx.glance.unit.ColorProvider(Color(0xFF9CA3AF)),
                            fontSize = 11.sp
                        ),
                        maxLines = 1
                    )
                }

                // Bouton lecture rapide
                Box(
                    modifier = GlanceModifier
                        .size(34.dp)
                        .background(if (isPlaying) Color(0xFF22D3EE) else Color(0xFFA855F7))
                        .cornerRadius(17.dp)
                        .clickable(actionRunCallback<PlayPauseActionCallback>()),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        provider = ImageProvider(
                            if (isPlaying) R.drawable.ic_widget_pause else R.drawable.ic_widget_play
                        ),
                        contentDescription = if (isPlaying) "Pause" else "Lecture",
                        modifier = GlanceModifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = GlanceModifier.height(10.dp))

            // Cartouche centrale affichant la ligne de parole en cours de lecture
            Box(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .defaultWeight()
                    .background(Color(0xFF1B142D))
                    .cornerRadius(12.dp)
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "“ $lyricLine ”",
                    style = TextStyle(
                        color = androidx.glance.unit.ColorProvider(Color(0xFFC4B5FD)), // Lavande clair
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 3
                )
            }
        }
    }
}

class LyricsMusicWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = LyricsMusicWidget()
}
