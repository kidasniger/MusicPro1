package com.example.player

import android.content.Context
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Build
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.data.local.entities.TrackEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MusicPlayerManager(context: Context) {
    private val appContext = context.applicationContext
    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private var progressJob: Job? = null

    val audioEffectsManager = AudioEffectsManager()

    val exoPlayer: ExoPlayer = ExoPlayer.Builder(appContext).build().apply {
        repeatMode = Player.REPEAT_MODE_ALL
    }

    private val _repeatMode = MutableStateFlow(Player.REPEAT_MODE_ALL)
    val repeatMode: StateFlow<Int> = _repeatMode.asStateFlow()

    private val _isShuffleEnabled = MutableStateFlow(false)
    val isShuffleEnabled: StateFlow<Boolean> = _isShuffleEnabled.asStateFlow()

    private var crossfadeDurationMs: Long = 0L

    private val _activeTrack = MutableStateFlow<TrackEntity?>(null)
    val activeTrack: StateFlow<TrackEntity?> = _activeTrack.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPositionMs = MutableStateFlow(0L)
    val currentPositionMs: StateFlow<Long> = _currentPositionMs.asStateFlow()

    private val _durationMs = MutableStateFlow(0L)
    val durationMs: StateFlow<Long> = _durationMs.asStateFlow()

    private val _queue = MutableStateFlow<List<TrackEntity>>(emptyList())
    val queue: StateFlow<List<TrackEntity>> = _queue.asStateFlow()

    private var currentIndex = 0
    private var sleepTimerJob: Job? = null
    private val _sleepRemainingSeconds = MutableStateFlow<Long?>(null)
    val sleepRemainingSeconds: StateFlow<Long?> = _sleepRemainingSeconds.asStateFlow()

    private val _isEndOfTrackSleepActive = MutableStateFlow(false)
    val isEndOfTrackSleepActive: StateFlow<Boolean> = _isEndOfTrackSleepActive.asStateFlow()

    private val _isBluetoothConnected = MutableStateFlow(false)
    val isBluetoothConnected: StateFlow<Boolean> = _isBluetoothConnected.asStateFlow()

    private val _connectedDeviceName = MutableStateFlow<String?>(null)
    val connectedDeviceName: StateFlow<String?> = _connectedDeviceName.asStateFlow()

    private val _lastBluetoothEvent = MutableStateFlow<String?>("Prêt")
    val lastBluetoothEvent: StateFlow<String?> = _lastBluetoothEvent.asStateFlow()

    init {
        checkAudioOutputDevice()
        exoPlayer.addListener(object : Player.Listener {
            override fun onAudioSessionIdChanged(audioSessionId: Int) {
                audioEffectsManager.initAudioEffects(audioSessionId)
            }

            override fun onIsPlayingChanged(isPlayingNow: Boolean) {
                _isPlaying.value = isPlayingNow
                if (isPlayingNow) {
                    val sessionId = exoPlayer.audioSessionId
                    if (sessionId != androidx.media3.common.C.AUDIO_SESSION_ID_UNSET) {
                        audioEffectsManager.initAudioEffects(sessionId)
                    }
                    startProgressTracker()
                } else {
                    progressJob?.cancel()
                }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    _durationMs.value = exoPlayer.duration.coerceAtLeast(0L)
                } else if (playbackState == Player.STATE_ENDED) {
                    if (_isEndOfTrackSleepActive.value) {
                        exoPlayer.pause()
                        _isPlaying.value = false
                        _isEndOfTrackSleepActive.value = false
                    } else if (_repeatMode.value == Player.REPEAT_MODE_ONE) {
                        seekTo(0L)
                        play()
                    } else if (_repeatMode.value == Player.REPEAT_MODE_OFF && currentIndex >= _queue.value.size - 1) {
                        exoPlayer.pause()
                        _isPlaying.value = false
                    } else {
                        skipToNext()
                    }
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                _isPlaying.value = false
                progressJob?.cancel()
            }
        })
    }

    fun setSleepTimerMinutes(minutes: Int) {
        _isEndOfTrackSleepActive.value = false
        sleepTimerJob?.cancel()
        val totalSecs = minutes * 60L
        _sleepRemainingSeconds.value = totalSecs

        sleepTimerJob = scope.launch {
            var currentSecs = totalSecs
            while (currentSecs > 0 && isActive) {
                delay(1000L)
                currentSecs -= 1
                _sleepRemainingSeconds.value = currentSecs
            }
            if (isActive) {
                exoPlayer.pause()
                _isPlaying.value = false
                _sleepRemainingSeconds.value = null
            }
        }
    }

    fun setSleepAtEndOfTrack() {
        sleepTimerJob?.cancel()
        _sleepRemainingSeconds.value = null
        _isEndOfTrackSleepActive.value = true
    }

    fun cancelSleepTimer() {
        sleepTimerJob?.cancel()
        _sleepRemainingSeconds.value = null
        _isEndOfTrackSleepActive.value = false
    }

    fun moveQueueItem(fromIndex: Int, toIndex: Int) {
        val currentList = _queue.value.toMutableList()
        if (fromIndex in currentList.indices && toIndex in currentList.indices) {
            val item = currentList.removeAt(fromIndex)
            currentList.add(toIndex, item)
            _queue.value = currentList
        }
    }

    fun removeQueueItem(index: Int) {
        val currentList = _queue.value.toMutableList()
        if (index in currentList.indices) {
            val removed = currentList.removeAt(index)
            _queue.value = currentList
            // Si la piste active a été supprimée, passer à la suivante si disponible
            if (removed.id == _activeTrack.value?.id && currentList.isNotEmpty()) {
                val nextIdx = index.coerceAtMost(currentList.size - 1)
                playTrack(currentList[nextIdx])
            }
        }
    }

    fun clearQueue() {
        val active = _activeTrack.value
        _queue.value = if (active != null) listOf(active) else emptyList()
    }

    fun setQueue(tracks: List<TrackEntity>, startIndex: Int = 0) {
        _queue.value = tracks
        if (tracks.isNotEmpty() && startIndex in tracks.indices) {
            currentIndex = startIndex
            playTrack(tracks[startIndex])
        }
    }

    fun playTrack(track: TrackEntity) {
        _activeTrack.value = track
        _durationMs.value = track.durationMs
        _currentPositionMs.value = 0L

        val mediaMetadata = androidx.media3.common.MediaMetadata.Builder()
            .setTitle(track.title)
            .setArtist(track.artist)
            .setAlbumTitle(track.album.ifEmpty { "MusicPro" })
            .setDisplayTitle(track.title)
            .build()

        if (track.path.isNotEmpty()) {
            val mediaItem = MediaItem.Builder()
                .setUri(track.path)
                .setMediaMetadata(mediaMetadata)
                .build()
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.play()
            _isPlaying.value = true
            startProgressTracker()
        }

        try {
            val serviceIntent = android.content.Intent(appContext, com.example.service.MusicPlaybackService::class.java)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                appContext.startForegroundService(serviceIntent)
            } else {
                appContext.startService(serviceIntent)
            }
        } catch (e: Exception) {
            // Fallback si permission manquante
        }
    }

    fun pause() {
        exoPlayer.pause()
        _isPlaying.value = false
    }

    fun play() {
        if (_activeTrack.value != null) {
            exoPlayer.play()
            _isPlaying.value = true
            startProgressTracker()
        }
    }

    fun checkAudioOutputDevice() {
        val audioManager = appContext.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
        if (audioManager != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                val devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
                val btDevice = devices.firstOrNull {
                    it.type == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP ||
                    it.type == AudioDeviceInfo.TYPE_BLUETOOTH_SCO ||
                    (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && (it.type == AudioDeviceInfo.TYPE_BLE_HEADSET || it.type == AudioDeviceInfo.TYPE_BLE_SPEAKER))
                }
                val wiredDevice = devices.firstOrNull {
                    it.type == AudioDeviceInfo.TYPE_WIRED_HEADSET ||
                    it.type == AudioDeviceInfo.TYPE_WIRED_HEADPHONES ||
                    it.type == AudioDeviceInfo.TYPE_USB_HEADSET ||
                    it.type == AudioDeviceInfo.TYPE_USB_DEVICE
                }

                if (btDevice != null) {
                    _isBluetoothConnected.value = true
                    val name = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        btDevice.productName?.toString()?.takeIf { it.isNotBlank() } ?: "Casque Bluetooth A2DP"
                    } else {
                        "Casque Bluetooth A2DP"
                    }
                    _connectedDeviceName.value = name
                    _lastBluetoothEvent.value = "Connecté : $name"
                } else if (wiredDevice != null) {
                    _isBluetoothConnected.value = true
                    val name = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        wiredDevice.productName?.toString()?.takeIf { it.isNotBlank() } ?: "Casque audio filaire / USB"
                    } else {
                        "Casque Filaire"
                    }
                    _connectedDeviceName.value = name
                    _lastBluetoothEvent.value = "Connecté : $name"
                } else {
                    _isBluetoothConnected.value = false
                    _connectedDeviceName.value = null
                    _lastBluetoothEvent.value = "Haut-parleur de l'appareil"
                }
            } catch (e: Exception) {
                _isBluetoothConnected.value = false
                _connectedDeviceName.value = null
            }
        }
    }

    fun onBluetoothDeviceConnected(name: String) {
        _isBluetoothConnected.value = true
        _connectedDeviceName.value = name
        _lastBluetoothEvent.value = "Connecté à $name"
    }

    fun onBluetoothDeviceDisconnected(name: String) {
        _isBluetoothConnected.value = false
        _connectedDeviceName.value = null
        _lastBluetoothEvent.value = "Déconnecté ($name) • Lecture mise en pause automatique"
    }

    fun togglePlayPause() {
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
            _isPlaying.value = false
        } else {
            if (_activeTrack.value != null) {
                exoPlayer.play()
                _isPlaying.value = true
                startProgressTracker()
            }
        }
    }

    fun updateActiveTrackFavorite(isFavorite: Boolean) {
        _activeTrack.value = _activeTrack.value?.copy(isFavorite = isFavorite)
    }

    fun setRepeatMode(mode: Int) {
        _repeatMode.value = mode
        exoPlayer.repeatMode = mode
    }

    fun cycleRepeatMode(): Int {
        val nextMode = when (_repeatMode.value) {
            Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE // Répéter 1 morceau
            Player.REPEAT_MODE_ONE -> Player.REPEAT_MODE_OFF // Désactivé
            else -> Player.REPEAT_MODE_ALL // Répéter tout
        }
        setRepeatMode(nextMode)
        return nextMode
    }

    fun toggleShuffle() {
        val newShuffle = !_isShuffleEnabled.value
        _isShuffleEnabled.value = newShuffle
        exoPlayer.shuffleModeEnabled = newShuffle
    }

    fun setCrossfadeSeconds(seconds: Int) {
        crossfadeDurationMs = (seconds * 1000L).coerceAtLeast(0L)
    }

    fun seekTo(positionMs: Long) {
        exoPlayer.seekTo(positionMs)
        _currentPositionMs.value = positionMs
    }

    fun skipToNext() {
        if (crossfadeDurationMs > 0L) {
            scope.launch {
                fadeVolume(down = true)
                doSkipToNext()
                fadeVolume(down = false)
            }
        } else {
            doSkipToNext()
        }
    }

    private fun doSkipToNext() {
        val currentQueue = _queue.value
        if (currentQueue.isNotEmpty()) {
            currentIndex = (currentIndex + 1) % currentQueue.size
            playTrack(currentQueue[currentIndex])
        }
    }

    fun skipToPrevious() {
        if (crossfadeDurationMs > 0L) {
            scope.launch {
                fadeVolume(down = true)
                doSkipToPrevious()
                fadeVolume(down = false)
            }
        } else {
            doSkipToPrevious()
        }
    }

    private fun doSkipToPrevious() {
        val currentQueue = _queue.value
        if (currentQueue.isNotEmpty()) {
            currentIndex = if (currentIndex - 1 < 0) currentQueue.size - 1 else currentIndex - 1
            playTrack(currentQueue[currentIndex])
        }
    }

    private suspend fun fadeVolume(down: Boolean) {
        val steps = 6
        val stepDelay = (crossfadeDurationMs / (steps * 2)).coerceIn(10L, 80L)
        for (i in 1..steps) {
            val volume = if (down) (steps - i) / steps.toFloat() else i / steps.toFloat()
            exoPlayer.volume = volume
            delay(stepDelay)
        }
        if (!down) {
            exoPlayer.volume = 1f
        }
    }

    private fun startProgressTracker() {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (isActive) {
                if (exoPlayer.isPlaying) {
                    _currentPositionMs.value = exoPlayer.currentPosition
                    _durationMs.value = exoPlayer.duration.coerceAtLeast(_durationMs.value)
                } else if (_isPlaying.value) {
                    // Progression pas-à-pas si en lecture
                    _currentPositionMs.value = (_currentPositionMs.value + 500L).coerceAtMost(
                        if (_durationMs.value > 0) _durationMs.value else 200_000L
                    )
                }
                delay(500L)
            }
        }
    }

    fun release() {
        progressJob?.cancel()
        exoPlayer.release()
    }
}
