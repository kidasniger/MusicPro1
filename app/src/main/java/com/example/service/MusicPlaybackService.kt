package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.example.MainActivity
import com.example.MusicProApplication
import com.example.data.local.entities.TrackEntity
import com.example.player.MusicPlayerManager

@UnstableApi
class MusicPlaybackService : MediaSessionService() {

    private var mediaSession: MediaSession? = null
    private lateinit var playerManager: MusicPlayerManager

    companion object {
        const val CHANNEL_ID = "musicpro_playback_channel"
        const val NOTIFICATION_ID = 1001

        const val ACTION_PLAY = "com.example.musicpro.ACTION_PLAY"
        const val ACTION_PAUSE = "com.example.musicpro.ACTION_PAUSE"
        const val ACTION_NEXT = "com.example.musicpro.ACTION_NEXT"
        const val ACTION_PREVIOUS = "com.example.musicpro.ACTION_PREVIOUS"
    }

    override fun onCreate() {
        super.onCreate()
        val app = applicationContext as MusicProApplication
        playerManager = app.container.playerManager

        createNotificationChannel()

        val sessionActivityPendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        mediaSession = MediaSession.Builder(this, playerManager.exoPlayer)
            .setSessionActivity(sessionActivityPendingIntent)
            .build()

        playerManager.exoPlayer.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                updateNotification()
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                updateNotification()
            }
        })
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PLAY -> playerManager.exoPlayer.play()
            ACTION_PAUSE -> playerManager.exoPlayer.pause()
            ACTION_NEXT -> playerManager.skipToNext()
            ACTION_PREVIOUS -> playerManager.skipToPrevious()
        }
        updateNotification()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "MusicPro Lecture Audio"
            val descriptionText = "Contrôles de lecture et écran verrouillé"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                setShowBadge(false)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun updateNotification() {
        val activeTrack = playerManager.activeTrack.value
        val isPlaying = playerManager.isPlaying.value

        if (activeTrack == null) {
            return
        }

        val notification = buildMediaNotification(activeTrack, isPlaying)
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun buildMediaNotification(track: TrackEntity, isPlaying: Boolean): Notification {
        val openAppIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val contentPendingIntent = PendingIntent.getActivity(
            this,
            0,
            openAppIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Actions
        val prevIntent = Intent(this, MusicPlaybackService::class.java).apply { action = ACTION_PREVIOUS }
        val prevPendingIntent = PendingIntent.getService(
            this, 1, prevIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val playPauseIntent = Intent(this, MusicPlaybackService::class.java).apply {
            action = if (isPlaying) ACTION_PAUSE else ACTION_PLAY
        }
        val playPausePendingIntent = PendingIntent.getService(
            this, 2, playPauseIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val nextIntent = Intent(this, MusicPlaybackService::class.java).apply { action = ACTION_NEXT }
        val nextPendingIntent = PendingIntent.getService(
            this, 3, nextIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val largeIcon = generateArtBitmap(track.coverGradient, track.title)

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setLargeIcon(largeIcon)
            .setContentTitle(track.title)
            .setContentText("${track.artist} • ${track.album.ifEmpty { "MusicPro" }}")
            .setSubText("${track.format} • ${track.bitrate} kbps")
            .setContentIntent(contentPendingIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(isPlaying)
            .addAction(android.R.drawable.ic_media_previous, "Précédent", prevPendingIntent)
            .addAction(
                if (isPlaying) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play,
                if (isPlaying) "Pause" else "Lecture",
                playPausePendingIntent
            )
            .addAction(android.R.drawable.ic_media_next, "Suivant", nextPendingIntent)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("${track.artist}\n${track.album.ifEmpty { "MusicPro" }} • ${track.format} ${track.bitrate} kbps")
            )

        return builder.build()
    }

    private fun generateArtBitmap(gradientStr: String?, title: String): Bitmap {
        val size = 256
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        val (c1, c2) = when {
            gradientStr?.contains("#ff3a3a") == true -> Pair(0xFFFF3A3A.toInt(), 0xFF7A0A0A.toInt())
            gradientStr?.contains("#22D3EE") == true -> Pair(0xFF22D3EE.toInt(), 0xFFA855F7.toInt())
            gradientStr?.contains("#f472b6") == true -> Pair(0xFFF472B6.toInt(), 0xFF5B21B6.toInt())
            gradientStr?.contains("#facc15") == true -> Pair(0xFFFACC15.toInt(), 0xFFEA580C.toInt())
            gradientStr?.contains("#a78bfa") == true -> Pair(0xFFA78BFA.toInt(), 0xFF1E1B4B.toInt())
            gradientStr?.contains("#10b981") == true -> Pair(0xFF10B981.toInt(), 0xFF064E3B.toInt())
            else -> Pair(0xFF22D3EE.toInt(), 0xFF5B21B6.toInt())
        }

        paint.shader = LinearGradient(0f, 0f, size.toFloat(), size.toFloat(), c1, c2, Shader.TileMode.CLAMP)
        canvas.drawRect(0f, 0f, size.toFloat(), size.toFloat(), paint)

        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFFFFFFFF.toInt()
            textSize = 96f
            textAlign = Paint.Align.CENTER
            isFakeBoldText = true
        }
        val letter = if (title.isNotBlank()) title.take(1).uppercase() else "M"
        val yPos = (canvas.height / 2 - (textPaint.descent() + textPaint.ascent()) / 2)
        canvas.drawText(letter, canvas.width / 2f, yPos, textPaint)

        return bitmap
    }

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }
}
