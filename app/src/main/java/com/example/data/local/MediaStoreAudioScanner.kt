package com.example.data.local

import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.example.data.local.entities.TrackEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MediaStoreAudioScanner(private val context: Context) {
    suspend fun scanAudioFiles(): List<TrackEntity> = withContext(Dispatchers.IO) {
        val tracks = mutableListOf<TrackEntity>()
        val collection: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.DATA
        )

        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"

        try {
            context.contentResolver.query(
                collection,
                projection,
                selection,
                null,
                "${MediaStore.Audio.Media.TITLE} ASC"
            )?.use { cursor ->
                val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
                val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)

                val gradientOptions = listOf(
                    "from-[#ff3a3a] to-[#7a0a0a]",
                    "from-[#22D3EE] to-[#A855F7]",
                    "from-[#f472b6] to-[#5B21B6]",
                    "from-[#facc15] to-[#ea580c]",
                    "from-[#a78bfa] to-[#1e1b4b]",
                    "from-[#10b981] to-[#064E3B]"
                )
                var gradIdx = 0

                while (cursor.moveToNext()) {
                    val title = cursor.getString(titleColumn) ?: "Morceau inconnu"
                    val artist = cursor.getString(artistColumn) ?: "Artiste inconnu"
                    val album = cursor.getString(albumColumn) ?: "Album inconnu"
                    val durationMs = cursor.getLong(durationColumn)
                    val sizeBytes = cursor.getLong(sizeColumn)
                    val path = cursor.getString(dataColumn) ?: ""

                    if (durationMs > 10_000) {
                        val minutes = (durationMs / 1000) / 60
                        val seconds = (durationMs / 1000) % 60
                        val durationFormatted = String.format("%d:%02d", minutes, seconds)
                        val sizeFormatted = String.format("%.1f MB", sizeBytes.toDouble() / (1024 * 1024))
                        val ext = path.substringAfterLast('.', "MP3").uppercase()

                        tracks.add(
                            TrackEntity(
                                title = title,
                                artist = if (artist == "<unknown>") "Artiste inconnu" else artist,
                                album = if (album == "<unknown>") "" else album,
                                durationMs = durationMs,
                                durationFormatted = durationFormatted,
                                bitrate = "320kbps",
                                format = ext,
                                size = sizeFormatted,
                                path = path,
                                coverGradient = gradientOptions[gradIdx % gradientOptions.size]
                            )
                        )
                        gradIdx++
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        tracks
    }
}
