package com.example.data.local

import android.content.ContentUris
import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import java.io.File
import java.io.InputStream

data class ExtractedAudioMetadata(
    val coverArtUri: String? = null,
    val embeddedLyrics: String? = null
)

object AudioMetadataExtractor {
    fun extractMetadata(context: Context, uriString: String, trackId: Long, albumId: Long? = null): ExtractedAudioMetadata {
        var coverArtUri: String? = null
        var embeddedLyrics: String? = null
        val retriever = MediaMetadataRetriever()
        try {
            val uri = Uri.parse(uriString)
            if (uriString.startsWith("content://")) {
                retriever.setDataSource(context, uri)
            } else {
                retriever.setDataSource(uriString)
            }

            // 1. Extraction de la pochette intégrée (Cover Art directe du fichier)
            val picture = retriever.embeddedPicture
            if (picture != null && picture.isNotEmpty()) {
                val coversDir = File(context.cacheDir, "covers")
                if (!coversDir.exists()) coversDir.mkdirs()
                val coverFile = File(coversDir, "cover_${trackId}.jpg")
                coverFile.writeBytes(picture)
                coverArtUri = Uri.fromFile(coverFile).toString()
            }
        } catch (_: Exception) {
        } finally {
            try {
                retriever.release()
            } catch (_: Exception) {}
        }

        // Si pas de pochette embarquée directe, tenter via MediaStore AlbumArt
        if (coverArtUri == null && albumId != null && albumId > 0) {
            try {
                val artworkUri = ContentUris.withAppendedId(
                    Uri.parse("content://media/external/audio/albumart"),
                    albumId
                )
                context.contentResolver.openInputStream(artworkUri)?.use {
                    coverArtUri = artworkUri.toString()
                }
            } catch (_: Exception) {
                // Pas d'artwork dans MediaStore
            }
        }

        // 2. Extraction des paroles intégrées (ID3v2 USLT / SYLT / Vorbis Comment / MP4)
        try {
            embeddedLyrics = extractLyricsFromStream(context, uriString)
        } catch (_: Exception) {
        }

        return ExtractedAudioMetadata(
            coverArtUri = coverArtUri,
            embeddedLyrics = embeddedLyrics
        )
    }

    private fun extractLyricsFromStream(context: Context, uriString: String): String? {
        val uri = Uri.parse(uriString)
        val stream: InputStream? = if (uriString.startsWith("content://")) {
            try {
                context.contentResolver.openInputStream(uri)
            } catch (_: Exception) {
                null
            }
        } else {
            val file = File(uriString)
            if (file.exists()) file.inputStream() else null
        }
        if (stream == null) return null

        stream.use { input ->
            val header = ByteArray(10)
            val read = input.read(header)
            if (read < 10) return null

            // 1) Signature ID3v2 "ID3"
            if (header[0] == 'I'.code.toByte() && header[1] == 'D'.code.toByte() && header[2] == '3'.code.toByte()) {
                val majorVersion = header[3].toInt()
                val tagSize = ((header[6].toInt() and 0x7F) shl 21) or
                              ((header[7].toInt() and 0x7F) shl 14) or
                              ((header[8].toInt() and 0x7F) shl 7) or
                              (header[9].toInt() and 0x7F)
                val maxRead = minOf(tagSize, 3 * 1024 * 1024)
                val tagBuffer = ByteArray(maxRead)
                var bytesRead = 0
                while (bytesRead < maxRead) {
                    val count = input.read(tagBuffer, bytesRead, maxRead - bytesRead)
                    if (count <= 0) break
                    bytesRead += count
                }

                // Recherche USLT (Unsynchronised lyrics)
                val usltIndex = indexOfSubarray(tagBuffer, "USLT".toByteArray(Charsets.ISO_8859_1))
                if (usltIndex >= 0 && usltIndex + 10 < bytesRead) {
                    val frameSize = if (majorVersion == 4) {
                        ((tagBuffer[usltIndex + 4].toInt() and 0x7F) shl 21) or
                        ((tagBuffer[usltIndex + 5].toInt() and 0x7F) shl 14) or
                        ((tagBuffer[usltIndex + 6].toInt() and 0x7F) shl 7) or
                        (tagBuffer[usltIndex + 7].toInt() and 0x7F)
                    } else {
                        ((tagBuffer[usltIndex + 4].toInt() and 0xFF) shl 24) or
                        ((tagBuffer[usltIndex + 5].toInt() and 0xFF) shl 16) or
                        ((tagBuffer[usltIndex + 6].toInt() and 0xFF) shl 8) or
                        (tagBuffer[usltIndex + 7].toInt() and 0xFF)
                    }
                    val frameStart = usltIndex + 10
                    val frameEnd = minOf(frameStart + frameSize, bytesRead)
                    if (frameEnd > frameStart + 4) {
                        val contentBytes = tagBuffer.copyOfRange(frameStart, frameEnd)
                        val text = parseUsltText(contentBytes)
                        if (!text.isNullOrBlank()) return text
                    }
                }

                // Recherche SYLT (Synchronised lyrics)
                val syltIndex = indexOfSubarray(tagBuffer, "SYLT".toByteArray(Charsets.ISO_8859_1))
                if (syltIndex >= 0 && syltIndex + 10 < bytesRead) {
                    val frameStart = syltIndex + 10
                    val frameEnd = minOf(frameStart + 10000, bytesRead)
                    if (frameEnd > frameStart + 4) {
                        val contentBytes = tagBuffer.copyOfRange(frameStart, frameEnd)
                        val text = parseUsltText(contentBytes)
                        if (!text.isNullOrBlank()) return text
                    }
                }
            } else {
                // Recherche Vorbis Comment / FLAC / MP4 lyrics
                val buffer = ByteArray(64 * 1024)
                System.arraycopy(header, 0, buffer, 0, 10)
                val extraRead = input.read(buffer, 10, buffer.size - 10)
                val totalRead = if (extraRead > 0) 10 + extraRead else 10
                val stringContent = String(buffer, 0, totalRead, Charsets.ISO_8859_1)

                val lyricsMarker = listOf("LYRICS=", "unsyncedlyrics=", "©lyr")
                for (marker in lyricsMarker) {
                    val markerPos = stringContent.indexOf(marker, ignoreCase = true)
                    if (markerPos >= 0) {
                        val start = markerPos + marker.length
                        val end = stringContent.indexOf('\u0000', start).let { if (it >= 0) it else minOf(start + 4000, totalRead) }
                        val lyricsText = stringContent.substring(start, end).trim()
                        if (lyricsText.length > 10) {
                            return lyricsText
                        }
                    }
                }
            }
        }
        return null
    }

    private fun indexOfSubarray(array: ByteArray, target: ByteArray): Int {
        if (target.isEmpty()) return 0
        outer@ for (i in 0..(array.size - target.size)) {
            for (j in target.indices) {
                if (array[i + j] != target[j]) continue@outer
            }
            return i
        }
        return -1
    }

    private fun parseUsltText(bytes: ByteArray): String? {
        if (bytes.size <= 4) return null
        val encoding = bytes[0].toInt()
        val charset = when (encoding) {
            1 -> Charsets.UTF_16
            2 -> Charsets.UTF_16BE
            3 -> Charsets.UTF_8
            else -> Charsets.ISO_8859_1
        }
        val textBytes = bytes.copyOfRange(4, bytes.size)
        val fullString = String(textBytes, charset)
        val nullIndex = fullString.indexOf('\u0000')
        val candidate = if (nullIndex >= 0 && nullIndex + 1 < fullString.length) {
            fullString.substring(nullIndex + 1).trim()
        } else {
            fullString.trim()
        }
        return candidate.replace("\u0000", "").trim().ifBlank { null }
    }
}
