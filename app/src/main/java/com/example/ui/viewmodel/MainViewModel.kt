package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.MediaStoreAudioScanner
import com.example.data.local.entities.TrackEntity
import com.example.data.local.preferences.UserPreferencesRepository
import com.example.data.lyrics.LyricLine
import com.example.data.lyrics.LrcParser
import com.example.data.remote.lrclib.LrclibResponse
import com.example.data.repository.LyricsRepository
import com.example.data.repository.MusicRepository
import com.example.player.MusicPlayerManager
import com.example.util.NetworkMonitor
import java.io.File
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class MainUiState(
    val activeTrack: TrackEntity? = null,
    val isPlaying: Boolean = false,
    val currentPositionMs: Long = 0L,
    val durationMs: Long = 0L,
    val currentLyricIndex: Int = 0,
    val lyrics: List<LyricLine> = emptyList(),
    val lyricsSource: String = "LRCLIB • 98% match",
    val isLyricsLoading: Boolean = false,
    val lyricsError: String? = null,
    val lyricsOffsetMs: Long = 0L,
    val isTranslated: Boolean = false,
    val lrclibSearchResults: List<LrclibResponse> = emptyList(),
    val isSearchingLrclib: Boolean = false,
    val isGroqTranscribing: Boolean = false,
    val groqTranscriptionProgress: String = "",
    val searchQuery: String = "",
    val activeTab: String = "Titres"
)

class MainViewModel(
    private val musicRepository: MusicRepository,
    private val lyricsRepository: LyricsRepository,
    private val playerManager: MusicPlayerManager,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val networkMonitor: NetworkMonitor? = null,
    private val mediaStoreScanner: MediaStoreAudioScanner? = null
) : ViewModel() {

    val isOnboardingCompleted: StateFlow<Boolean?> = userPreferencesRepository.isOnboardingCompleted
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null
        )

    val groqApiKey: StateFlow<String> = userPreferencesRepository.groqApiKey
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ""
        )

    val groqModel: StateFlow<String> = userPreferencesRepository.groqModel
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "whisper-large-v3"
        )

    val themeMode: StateFlow<String> = userPreferencesRepository.themeMode
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "Dark Cyberpunk"
        )

    val audioQuality: StateFlow<String> = userPreferencesRepository.audioQuality
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "Hi-Res Lossless (FLAC 24-bit)"
        )

    val gaplessPlayback: StateFlow<Boolean> = userPreferencesRepository.gaplessPlayback
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

    val crossfadeSeconds: StateFlow<Int> = userPreferencesRepository.crossfadeSeconds
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 3
        )

    val autoDownloadLyrics: StateFlow<Boolean> = userPreferencesRepository.autoDownloadLyrics
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

    val lyricsCacheCount: StateFlow<Int> = userPreferencesRepository.lyricsCacheCount
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 18
        )

    val allTracks: StateFlow<List<TrackEntity>> = musicRepository.getAllTracks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val favoriteTracks: StateFlow<List<TrackEntity>> = musicRepository.getFavoriteTracks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allPlaylists: StateFlow<List<com.example.data.local.entities.PlaylistEntity>> = musicRepository.getAllPlaylists()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val historyWithTracks: StateFlow<List<com.example.data.local.dao.HistoryWithTrack>> = musicRepository.getHistoryWithTracks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val activeTrack: StateFlow<TrackEntity?> = playerManager.activeTrack
    val isPlaying: StateFlow<Boolean> = playerManager.isPlaying
    val currentPositionMs: StateFlow<Long> = playerManager.currentPositionMs
    val durationMs: StateFlow<Long> = playerManager.durationMs
    val queue: StateFlow<List<TrackEntity>> = playerManager.queue
    val sleepRemainingSeconds: StateFlow<Long?> = playerManager.sleepRemainingSeconds
    val isEndOfTrackSleepActive: StateFlow<Boolean> = playerManager.isEndOfTrackSleepActive
    val isBluetoothConnected: StateFlow<Boolean> = playerManager.isBluetoothConnected
    val connectedDeviceName: StateFlow<String?> = playerManager.connectedDeviceName
    val lastBluetoothEvent: StateFlow<String?> = playerManager.lastBluetoothEvent

    private val _isOffline = MutableStateFlow(false)
    val isOffline: StateFlow<Boolean> = _isOffline.asStateFlow()

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        scanLocalMusic()
        observePlaybackForLyrics()
        observeNetworkState()
    }

    private fun observeNetworkState() {
        if (networkMonitor != null) {
            _isOffline.value = !networkMonitor.isCurrentlyConnected()
            viewModelScope.launch {
                networkMonitor.isOnline.collect { online ->
                    _isOffline.value = !online
                }
            }
        }
    }

    private fun observePlaybackForLyrics() {
        // Observe les changements de position pour ajuster la ligne active
        viewModelScope.launch {
            combine(currentPositionMs, _uiState) { pos, state ->
                Pair(pos, state)
            }.collect { (pos, state) ->
                if (state.lyrics.isNotEmpty()) {
                    val effectivePos = pos + state.lyricsOffsetMs
                    // Trouve la dernière ligne dont le timeMs <= effectivePos
                    var targetIndex = -1
                    for (i in state.lyrics.indices) {
                        if (state.lyrics[i].timeMs <= effectivePos) {
                            targetIndex = i
                        } else {
                            break
                        }
                    }
                    val validIndex = targetIndex.coerceAtLeast(0)
                    if (validIndex != state.currentLyricIndex) {
                        _uiState.value = _uiState.value.copy(currentLyricIndex = validIndex)
                    }
                }
            }
        }

        // Observe le changement de morceau pour charger les paroles
        viewModelScope.launch {
            activeTrack.collect { track ->
                if (track != null) {
                    loadLyricsForTrack(track)
                } else {
                    _uiState.value = _uiState.value.copy(
                        lyrics = emptyList(),
                        lyricsSource = "",
                        currentLyricIndex = 0
                    )
                }
            }
        }
    }

    fun playTrack(track: TrackEntity) {
        playerManager.playTrack(track)
        viewModelScope.launch {
            musicRepository.recordHistory(track.id)
        }
    }

    fun togglePlayPause() {
        playerManager.togglePlayPause()
    }

    fun seekTo(positionMs: Long) {
        playerManager.seekTo(positionMs)
    }

    fun next() {
        playerManager.skipToNext()
    }

    fun previous() {
        playerManager.skipToPrevious()
    }

    fun toggleFavorite(track: TrackEntity) {
        viewModelScope.launch {
            musicRepository.toggleFavorite(track.id, !track.isFavorite)
        }
    }

    // File d'attente (Queue)
    fun moveQueueItem(fromIndex: Int, toIndex: Int) {
        playerManager.moveQueueItem(fromIndex, toIndex)
    }

    fun removeQueueItem(index: Int) {
        playerManager.removeQueueItem(index)
    }

    fun clearQueue() {
        playerManager.clearQueue()
    }

    // Minuteur de sommeil (Sleep Timer)
    fun setSleepTimerMinutes(minutes: Int) {
        playerManager.setSleepTimerMinutes(minutes)
    }

    fun setSleepAtEndOfTrack() {
        playerManager.setSleepAtEndOfTrack()
    }

    fun cancelSleepTimer() {
        playerManager.cancelSleepTimer()
    }

    // Playlists & History Management
    fun getTracksForPlaylist(playlistId: Long): kotlinx.coroutines.flow.Flow<List<TrackEntity>> {
        return musicRepository.getTracksForPlaylist(playlistId)
    }

    fun createPlaylist(name: String, gradient: String = "from-[#5B21B6] to-[#22D3EE]") {
        viewModelScope.launch {
            musicRepository.createPlaylist(name, gradient)
        }
    }

    fun renamePlaylist(playlistId: Long, newName: String) {
        viewModelScope.launch {
            musicRepository.renamePlaylist(playlistId, newName)
        }
    }

    fun deletePlaylist(playlistId: Long) {
        viewModelScope.launch {
            musicRepository.deletePlaylist(playlistId)
        }
    }

    fun addTrackToPlaylist(playlistId: Long, trackId: Long) {
        viewModelScope.launch {
            musicRepository.addTrackToPlaylist(playlistId, trackId)
        }
    }

    fun removeTrackFromPlaylist(playlistId: Long, trackId: Long) {
        viewModelScope.launch {
            musicRepository.removeTrackFromPlaylist(playlistId, trackId)
        }
    }

    fun reorderPlaylistTracks(playlistId: Long, orderedTrackIds: List<Long>) {
        viewModelScope.launch {
            musicRepository.reorderPlaylistTracks(playlistId, orderedTrackIds)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            musicRepository.clearHistory()
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            userPreferencesRepository.setOnboardingCompleted(true)
        }
    }

    fun refreshNetworkStatus() {
        if (networkMonitor != null) {
            _isOffline.value = !networkMonitor.isCurrentlyConnected()
        }
    }

    fun getNetworkTypeName(): String {
        return networkMonitor?.getNetworkTypeName() ?: if (_isOffline.value) "Hors-ligne" else "Connecté"
    }

    fun refreshAudioDevices() {
        playerManager.checkAudioOutputDevice()
    }

    fun scanLocalMusic() {
        viewModelScope.launch {
            if (mediaStoreScanner != null) {
                val scanned = mediaStoreScanner.scanAudioFiles()
                if (scanned.isNotEmpty()) {
                    musicRepository.insertTracks(scanned)
                    if (playerManager.activeTrack.value == null) {
                        playerManager.setQueue(scanned, startIndex = 0)
                    }
                }
            }
        }
    }

    // Gestion des Paroles (LRCLIB, Groq, Décalage, Traduction)
    fun loadLyricsForTrack(track: TrackEntity) {
        viewModelScope.launch {
            if (_isOffline.value) {
                _uiState.value = _uiState.value.copy(
                    lyrics = emptyList(),
                    lyricsSource = "",
                    isLyricsLoading = false,
                    lyricsError = "Mode hors-ligne actif : connexion requise pour joindre LRCLIB."
                )
                return@launch
            }

            _uiState.value = _uiState.value.copy(
                isLyricsLoading = true,
                lyricsError = null
            )

            val result = lyricsRepository.fetchLrclibLyrics(
                trackName = track.title,
                artistName = track.artist,
                albumName = track.album.ifEmpty { null },
                durationSeconds = (track.durationMs / 1000).toInt()
            )

            result.onSuccess { response ->
                val syncedLrc = response.syncedLyrics
                val plainLrc = response.plainLyrics

                if (!syncedLrc.isNullOrBlank()) {
                    val parsed = LrcParser.parse(syncedLrc)
                    _uiState.value = _uiState.value.copy(
                        lyrics = parsed,
                        lyricsSource = "LRCLIB • Synchronisé",
                        isLyricsLoading = false,
                        lyricsError = null
                    )
                } else if (!plainLrc.isNullOrBlank()) {
                    val lines = plainLrc.lines().filter { it.isNotBlank() }.mapIndexed { idx, line ->
                        LyricLine(
                            timeFormatted = "--:--",
                            timeMs = idx * 4000L,
                            text = line.trim()
                        )
                    }
                    _uiState.value = _uiState.value.copy(
                        lyrics = lines,
                        lyricsSource = "LRCLIB • Non synchronisé",
                        isLyricsLoading = false,
                        lyricsError = null
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        lyrics = emptyList(),
                        lyricsSource = "",
                        isLyricsLoading = false,
                        lyricsError = "Aucune parole trouvée pour ce morceau."
                    )
                }
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    lyrics = emptyList(),
                    lyricsSource = "",
                    isLyricsLoading = false,
                    lyricsError = "Impossible de récupérer les paroles (${error.localizedMessage ?: "Erreur réseau"})."
                )
            }
        }
    }

    fun searchLrclibManually(query: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSearchingLrclib = true)
            val result = lyricsRepository.searchLrclib(query)
            result.onSuccess { list ->
                _uiState.value = _uiState.value.copy(
                    lrclibSearchResults = list,
                    isSearchingLrclib = false
                )
            }.onFailure {
                _uiState.value = _uiState.value.copy(
                    lrclibSearchResults = emptyList(),
                    isSearchingLrclib = false
                )
            }
        }
    }

    fun applyLrclibResult(result: LrclibResponse) {
        val synced = result.syncedLyrics ?: result.plainLyrics ?: ""
        if (synced.isNotBlank()) {
            val parsed = LrcParser.parse(synced)
            _uiState.value = _uiState.value.copy(
                lyrics = parsed,
                lyricsSource = "LRCLIB • ${result.artistName ?: "Manuel"}",
                lyricsError = null
            )
        }
    }

    fun adjustLyricsOffset(deltaMs: Long) {
        _uiState.value = _uiState.value.copy(
            lyricsOffsetMs = _uiState.value.lyricsOffsetMs + deltaMs
        )
    }

    fun resetLyricsOffset() {
        _uiState.value = _uiState.value.copy(lyricsOffsetMs = 0L)
    }

    fun toggleTranslation() {
        _uiState.value = _uiState.value.copy(
            isTranslated = !_uiState.value.isTranslated
        )
    }

    fun setGroqApiKey(apiKey: String) {
        viewModelScope.launch {
            userPreferencesRepository.setGroqApiKey(apiKey)
        }
    }

    fun setGroqModel(model: String) {
        viewModelScope.launch {
            userPreferencesRepository.setGroqModel(model)
        }
    }

    fun setThemeMode(theme: String) {
        viewModelScope.launch {
            userPreferencesRepository.setThemeMode(theme)
        }
    }

    fun setAudioQuality(quality: String) {
        viewModelScope.launch {
            userPreferencesRepository.setAudioQuality(quality)
        }
    }

    fun setGaplessPlayback(enabled: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setGaplessPlayback(enabled)
        }
    }

    fun setCrossfadeSeconds(seconds: Int) {
        viewModelScope.launch {
            userPreferencesRepository.setCrossfadeSeconds(seconds)
        }
    }

    fun setAutoDownloadLyrics(enabled: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setAutoDownloadLyrics(enabled)
        }
    }

    fun clearLyricsCache() {
        viewModelScope.launch {
            userPreferencesRepository.clearLyricsCache()
        }
    }

    fun transcribeTrackWithGroq(
        track: TrackEntity,
        onStatusUpdate: (String) -> Unit,
        onSuccess: (List<LyricLine>) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val apiKey = groqApiKey.value
            if (apiKey.isBlank()) {
                onError("Clé API Groq non configurée. Veuillez renseigner votre clé API dans les Paramètres.")
                return@launch
            }
            if (track.path.isBlank()) {
                onError("Aucun fichier audio physique associé au morceau '${track.title}'.")
                return@launch
            }
            val audioFile = File(track.path)
            if (!audioFile.exists()) {
                onError("Fichier audio introuvable sur le stockage de l'appareil (${track.path}).")
                return@launch
            }

            onStatusUpdate("Envoi du fichier audio à Groq Whisper (${groqModel.value})...")
            val result = lyricsRepository.transcribeAudioWithGroq(
                audioFile = audioFile,
                apiKey = apiKey,
                model = groqModel.value
            )

            result.onSuccess { response ->
                val segments = response.segments
                val lines = if (!segments.isNullOrEmpty()) {
                    segments.map { seg ->
                        val startSec = seg.start ?: 0.0
                        val ms = (startSec * 1000).toLong()
                        val minutes = (ms / 1000) / 60
                        val seconds = (ms / 1000) % 60
                        LyricLine(
                            timeFormatted = String.format("%02d:%02d", minutes, seconds),
                            timeMs = ms,
                            text = seg.text?.trim() ?: ""
                        )
                    }
                } else if (!response.text.isNullOrBlank()) {
                    response.text.lines().filter { it.isNotBlank() }.mapIndexed { idx, line ->
                        LyricLine(
                            timeFormatted = "--:--",
                            timeMs = idx * 4000L,
                            text = line.trim()
                        )
                    }
                } else {
                    emptyList()
                }

                if (lines.isNotEmpty()) {
                    setCustomLyrics(lines, "Groq Whisper • Transcription IA")
                    onSuccess(lines)
                } else {
                    onError("Aucune parole détectée par Groq Whisper.")
                }
            }.onFailure { error ->
                onError("Erreur Groq Whisper : ${error.localizedMessage ?: "Échec requête"}")
            }
        }
    }

    fun setCustomLyrics(lyrics: List<LyricLine>, sourceName: String) {
        _uiState.value = _uiState.value.copy(
            lyrics = lyrics,
            lyricsSource = sourceName,
            lyricsError = null
        )
    }

    override fun onCleared() {
        super.onCleared()
        playerManager.release()
    }
}

class MainViewModelFactory(
    private val musicRepository: MusicRepository,
    private val lyricsRepository: LyricsRepository,
    private val playerManager: MusicPlayerManager,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val networkMonitor: NetworkMonitor? = null,
    private val mediaStoreScanner: MediaStoreAudioScanner? = null
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(
                musicRepository,
                lyricsRepository,
                playerManager,
                userPreferencesRepository,
                networkMonitor,
                mediaStoreScanner
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
