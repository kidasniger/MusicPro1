package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tracks",
    indices = [Index(value = ["path"], unique = true)]
)
data class TrackEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val artist: String,
    val album: String,
    val durationMs: Long,
    val durationFormatted: String,
    val path: String = "",
    val format: String = "MP3",
    val bitrate: String = "320kbps",
    val size: String = "",
    val year: Int = 2024,
    val coverGradient: String = "from-[#22D3EE] to-[#A855F7]",
    val coverArtUri: String? = null,
    val embeddedLyrics: String? = null,
    val isFavorite: Boolean = false,
    val dateAdded: Long = System.currentTimeMillis()
)
