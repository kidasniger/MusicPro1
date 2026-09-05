package com.example.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.updateAll
import com.example.MusicProApplication

class PlayPauseActionCallback : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val app = context.applicationContext as? MusicProApplication
        app?.container?.playerManager?.togglePlayPause()

        // Rafraîchir les widgets Glance
        CompactMusicWidget().updateAll(context)
        StandardMusicWidget().updateAll(context)
        LyricsMusicWidget().updateAll(context)
    }
}

class NextTrackActionCallback : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val app = context.applicationContext as? MusicProApplication
        app?.container?.playerManager?.skipToNext()

        CompactMusicWidget().updateAll(context)
        StandardMusicWidget().updateAll(context)
        LyricsMusicWidget().updateAll(context)
    }
}

class PreviousTrackActionCallback : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val app = context.applicationContext as? MusicProApplication
        app?.container?.playerManager?.skipToPrevious()

        CompactMusicWidget().updateAll(context)
        StandardMusicWidget().updateAll(context)
        LyricsMusicWidget().updateAll(context)
    }
}
