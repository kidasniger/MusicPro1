package com.example.player

import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import android.media.audiofx.Virtualizer
import android.util.Log

class AudioEffectsManager {
    private var equalizer: Equalizer? = null
    private var bassBoost: BassBoost? = null
    private var virtualizer: Virtualizer? = null

    private var currentSessionId: Int = 0
    var isEnabled: Boolean = true
        private set
    var bassBoostStrength: Short = 650 // 0..1000
        private set
    var virtualizerStrength: Short = 400 // 0..1000
        private set

    fun initAudioEffects(audioSessionId: Int) {
        if (audioSessionId <= 0 || audioSessionId == currentSessionId) return
        release()
        currentSessionId = audioSessionId

        try {
            equalizer = Equalizer(0, audioSessionId).apply {
                enabled = isEnabled
            }
        } catch (e: Exception) {
            Log.w("AudioEffectsManager", "Equalizer non disponible: ${e.message}")
        }

        try {
            bassBoost = BassBoost(0, audioSessionId).apply {
                enabled = isEnabled
                if (strengthSupported) {
                    setStrength(bassBoostStrength)
                }
            }
        } catch (e: Exception) {
            Log.w("AudioEffectsManager", "BassBoost non disponible: ${e.message}")
        }

        try {
            virtualizer = Virtualizer(0, audioSessionId).apply {
                enabled = isEnabled
                if (strengthSupported) {
                    setStrength(virtualizerStrength)
                }
            }
        } catch (e: Exception) {
            Log.w("AudioEffectsManager", "Virtualizer non disponible: ${e.message}")
        }
    }

    fun setEnabled(enabled: Boolean) {
        isEnabled = enabled
        try { equalizer?.enabled = enabled } catch (ignored: Exception) {}
        try { bassBoost?.enabled = enabled } catch (ignored: Exception) {}
        try { virtualizer?.enabled = enabled } catch (ignored: Exception) {}
    }

    fun setBandLevel(band: Short, levelMilliBels: Short) {
        try {
            equalizer?.setBandLevel(band, levelMilliBels)
        } catch (ignored: Exception) {}
    }

    fun setBassBoost(strengthPercent: Float) {
        val str = (strengthPercent.coerceIn(0f, 100f) * 10f).toInt().toShort()
        bassBoostStrength = str
        try {
            if (bassBoost?.strengthSupported == true) {
                bassBoost?.setStrength(str)
            }
        } catch (ignored: Exception) {}
    }

    fun setVirtualizer(strengthPercent: Float) {
        val str = (strengthPercent.coerceIn(0f, 100f) * 10f).toInt().toShort()
        virtualizerStrength = str
        try {
            if (virtualizer?.strengthSupported == true) {
                virtualizer?.setStrength(str)
            }
        } catch (ignored: Exception) {}
    }

    fun applyPreset(presetName: String) {
        val curves = when (presetName.lowercase()) {
            "bass boost" -> listOf(600, 400, 100, 0, 0)
            "électro", "electro" -> listOf(500, 300, 0, 200, 400)
            "rock" -> listOf(400, 200, -100, 200, 500)
            "pop" -> listOf(-100, 200, 400, 200, -100)
            "vocal", "voix" -> listOf(-200, 0, 500, 300, 100)
            "jazz" -> listOf(300, 200, -100, 200, 300)
            "classique" -> listOf(400, 200, -200, 300, 300)
            else -> listOf(0, 0, 0, 0, 0)
        }
        try {
            val numBands = equalizer?.numberOfBands ?: 5
            for (i in 0 until minOf(curves.size, numBands.toInt())) {
                equalizer?.setBandLevel(i.toShort(), curves[i].toShort())
            }
        } catch (ignored: Exception) {}
    }

    fun release() {
        try { equalizer?.release() } catch (ignored: Exception) {}
        try { bassBoost?.release() } catch (ignored: Exception) {}
        try { virtualizer?.release() } catch (ignored: Exception) {}
        equalizer = null
        bassBoost = null
        virtualizer = null
    }
}
