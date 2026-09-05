package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.data.local.entities.FavoriteEntity
import com.example.data.local.entities.HistoryEntity
import com.example.data.local.entities.PlaylistEntity
import com.example.data.local.entities.PlaylistTrackCrossRef
import com.example.data.local.entities.TrackEntity
import kotlinx.coroutines.flow.Flow

data class HistoryWithTrack(
    val id: Long,
    val trackId: Long,
    val playedAt: Long,
    val title: String,
    val artist: String,
    val album: String,
    val durationMs: Long,
    val durationFormatted: String,
    val bitrate: String,
    val format: String,
    val size: String,
    val year: Int,
    val isFavorite: Boolean,
    val coverGradient: String,
    val coverArtUri: String? = null
)

@Dao
interface PlaylistDao {
    @Query("SELECT * FROM playlists ORDER BY createdAt DESC")
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>

    @Query("SELECT * FROM playlists WHERE id = :playlistId")
    suspend fun getPlaylistById(playlistId: Long): PlaylistEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity): Long

    @Query("UPDATE playlists SET name = :newName WHERE id = :playlistId")
    suspend fun renamePlaylist(playlistId: Long, newName: String)

    @Query("DELETE FROM playlists WHERE id = :playlistId")
    suspend fun deletePlaylist(playlistId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTrackToPlaylist(crossRef: PlaylistTrackCrossRef)

    @Query("DELETE FROM playlist_tracks WHERE playlistId = :playlistId AND trackId = :trackId")
    suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long)

    @Query("DELETE FROM playlist_tracks WHERE playlistId = :playlistId")
    suspend fun clearTracksForPlaylist(playlistId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylistTracks(refs: List<PlaylistTrackCrossRef>)

    @Transaction
    suspend fun reorderPlaylistTracks(playlistId: Long, orderedTrackIds: List<Long>) {
        clearTracksForPlaylist(playlistId)
        val refs = orderedTrackIds.mapIndexed { index, trackId ->
            PlaylistTrackCrossRef(playlistId = playlistId, trackId = trackId, orderIndex = index)
        }
        insertPlaylistTracks(refs)
        updateTrackCount(playlistId, orderedTrackIds.size)
    }

    @Query("UPDATE playlists SET trackCount = :count WHERE id = :playlistId")
    suspend fun updateTrackCount(playlistId: Long, count: Int)

    @Query("""
        SELECT t.* FROM tracks t
        INNER JOIN playlist_tracks pt ON t.id = pt.trackId
        WHERE pt.playlistId = :playlistId
        ORDER BY pt.orderIndex ASC
    """)
    fun getTracksForPlaylist(playlistId: Long): Flow<List<TrackEntity>>
}

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites ORDER BY addedAt DESC")
    fun getAllFavorites(): Flow<List<FavoriteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE trackId = :trackId")
    suspend fun removeFavorite(trackId: Long)

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE trackId = :trackId)")
    suspend fun isFavorite(trackId: Long): Boolean
}

@Dao
interface HistoryDao {
    @Query("""
        SELECT h.id, h.trackId, h.playedAt,
               t.title, t.artist, t.album, t.durationMs, t.durationFormatted,
               t.bitrate, t.format, t.size, t.year, t.isFavorite, t.coverGradient, t.coverArtUri
        FROM history h
        INNER JOIN tracks t ON h.trackId = t.id
        ORDER BY h.playedAt DESC
        LIMIT 100
    """)
    fun getHistoryWithTracks(): Flow<List<HistoryWithTrack>>

    @Query("""
        SELECT t.* FROM tracks t
        INNER JOIN history h ON t.id = h.trackId
        ORDER BY h.playedAt DESC
        LIMIT 50
    """)
    fun getRecentHistory(): Flow<List<TrackEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: HistoryEntity)

    @Query("DELETE FROM history WHERE id = :historyId")
    suspend fun deleteHistoryEntry(historyId: Long)

    @Query("DELETE FROM history")
    suspend fun clearHistory()
}
