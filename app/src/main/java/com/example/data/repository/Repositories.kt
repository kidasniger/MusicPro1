package com.example.data.repository

import com.example.data.local.MusicDatabase
import com.example.data.local.dao.HistoryWithTrack
import com.example.data.local.entities.FavoriteEntity
import com.example.data.local.entities.HistoryEntity
import com.example.data.local.entities.PlaylistEntity
import com.example.data.local.entities.PlaylistTrackCrossRef
import com.example.data.local.entities.TrackEntity
import com.example.data.remote.groq.GroqApi
import com.example.data.remote.groq.GroqTranscriptionResponse
import com.example.data.remote.lrclib.LrclibApi
import com.example.data.remote.lrclib.LrclibResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

interface MusicRepository {
    fun getAllTracks(): Flow<List<TrackEntity>>
    fun getFavoriteTracks(): Flow<List<TrackEntity>>
    fun getRecentHistory(): Flow<List<TrackEntity>>
    fun getHistoryWithTracks(): Flow<List<HistoryWithTrack>>
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>
    fun getTracksForPlaylist(playlistId: Long): Flow<List<TrackEntity>>
    fun searchTracks(query: String): Flow<List<TrackEntity>>
    suspend fun getTrackById(id: Long): TrackEntity?
    suspend fun getPlaylistById(playlistId: Long): PlaylistEntity?
    suspend fun insertTracks(tracks: List<TrackEntity>)
    suspend fun toggleFavorite(trackId: Long, isFavorite: Boolean)
    suspend fun recordHistory(trackId: Long)
    suspend fun clearHistory()
    suspend fun createPlaylist(name: String, gradient: String): Long
    suspend fun renamePlaylist(playlistId: Long, newName: String)
    suspend fun deletePlaylist(playlistId: Long)
    suspend fun addTrackToPlaylist(playlistId: Long, trackId: Long)
    suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long)
    suspend fun reorderPlaylistTracks(playlistId: Long, orderedTrackIds: List<Long>)
}

class DefaultMusicRepository(
    private val database: MusicDatabase
) : MusicRepository {
    private val trackDao = database.trackDao()
    private val playlistDao = database.playlistDao()
    private val favoriteDao = database.favoriteDao()
    private val historyDao = database.historyDao()

    override fun getAllTracks(): Flow<List<TrackEntity>> = trackDao.getAllTracks()

    override fun getFavoriteTracks(): Flow<List<TrackEntity>> = trackDao.getFavoriteTracks()

    override fun getRecentHistory(): Flow<List<TrackEntity>> = historyDao.getRecentHistory()

    override fun getHistoryWithTracks(): Flow<List<HistoryWithTrack>> = historyDao.getHistoryWithTracks()

    override fun getAllPlaylists(): Flow<List<PlaylistEntity>> = playlistDao.getAllPlaylists()

    override fun getTracksForPlaylist(playlistId: Long): Flow<List<TrackEntity>> =
        playlistDao.getTracksForPlaylist(playlistId)

    override fun searchTracks(query: String): Flow<List<TrackEntity>> = trackDao.searchTracks(query)

    override suspend fun getTrackById(id: Long): TrackEntity? = trackDao.getTrackById(id)

    override suspend fun getPlaylistById(playlistId: Long): PlaylistEntity? =
        playlistDao.getPlaylistById(playlistId)

    override suspend fun insertTracks(tracks: List<TrackEntity>) = trackDao.insertTracks(tracks)

    override suspend fun toggleFavorite(trackId: Long, isFavorite: Boolean) {
        trackDao.setFavorite(trackId, isFavorite)
        if (isFavorite) {
            favoriteDao.addFavorite(FavoriteEntity(trackId = trackId))
        } else {
            favoriteDao.removeFavorite(trackId)
        }
    }

    override suspend fun recordHistory(trackId: Long) {
        historyDao.insertHistory(HistoryEntity(trackId = trackId, playedAt = System.currentTimeMillis()))
    }

    override suspend fun clearHistory() {
        historyDao.clearHistory()
    }

    override suspend fun createPlaylist(name: String, gradient: String): Long {
        return playlistDao.insertPlaylist(
            PlaylistEntity(
                name = name,
                trackCount = 0,
                gradient = gradient,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    override suspend fun renamePlaylist(playlistId: Long, newName: String) {
        playlistDao.renamePlaylist(playlistId, newName)
    }

    override suspend fun deletePlaylist(playlistId: Long) {
        playlistDao.deletePlaylist(playlistId)
    }

    override suspend fun addTrackToPlaylist(playlistId: Long, trackId: Long) {
        playlistDao.addTrackToPlaylist(
            PlaylistTrackCrossRef(playlistId = playlistId, trackId = trackId)
        )
    }

    override suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long) {
        playlistDao.removeTrackFromPlaylist(playlistId, trackId)
    }

    override suspend fun reorderPlaylistTracks(playlistId: Long, orderedTrackIds: List<Long>) {
        playlistDao.reorderPlaylistTracks(playlistId, orderedTrackIds)
    }
}

interface LyricsRepository {
    suspend fun fetchLrclibLyrics(
        trackName: String,
        artistName: String,
        albumName: String? = null,
        durationSeconds: Int? = null
    ): Result<LrclibResponse>

    suspend fun searchLrclib(query: String): Result<List<LrclibResponse>>

    suspend fun transcribeAudioWithGroq(
        audioFile: File,
        apiKey: String,
        model: String = "whisper-large-v3",
        language: String? = null
    ): Result<GroqTranscriptionResponse>
}

class DefaultLyricsRepository(
    private val lrclibApi: LrclibApi,
    private val groqApi: GroqApi
) : LyricsRepository {

    override suspend fun fetchLrclibLyrics(
        trackName: String,
        artistName: String,
        albumName: String?,
        durationSeconds: Int?
    ): Result<LrclibResponse> = withContext(Dispatchers.IO) {
        try {
            val response = lrclibApi.getLyrics(
                trackName = trackName,
                artistName = artistName,
                albumName = albumName,
                durationSeconds = durationSeconds
            )
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun searchLrclib(query: String): Result<List<LrclibResponse>> = withContext(Dispatchers.IO) {
        try {
            val response = lrclibApi.searchLyrics(query)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun transcribeAudioWithGroq(
        audioFile: File,
        apiKey: String,
        model: String,
        language: String?
    ): Result<GroqTranscriptionResponse> = withContext(Dispatchers.IO) {
        try {
            val requestFile = audioFile.asRequestBody("audio/*".toMediaTypeOrNull())
            val filePart = MultipartBody.Part.createFormData("file", audioFile.name, requestFile)
            val modelBody = model.toRequestBody("text/plain".toMediaTypeOrNull())
            val formatBody = "verbose_json".toRequestBody("text/plain".toMediaTypeOrNull())
            val langBody = language?.toRequestBody("text/plain".toMediaTypeOrNull())

            val authHeader = if (apiKey.startsWith("Bearer ")) apiKey else "Bearer $apiKey"
            val response = groqApi.transcribeAudio(
                authorization = authHeader,
                file = filePart,
                model = modelBody,
                responseFormat = formatBody,
                language = langBody
            )
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
