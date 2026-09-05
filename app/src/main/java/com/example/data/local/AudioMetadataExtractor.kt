package com.example.data.local

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
    fun extractMetadata(context: Context, uriString: String, trackId: Long): ExtractedAudioMetadata {
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

            // 1. Extraction de la pochette intégrée (Cover Art)
            val picture = retriever.embeddedPicture
            if (picture != null && picture.isNotEmpty()) {
                val coversDir = File(context.cacheDir, "covers")
                if (!coversDir.exists()) coversDir.mkdirs()
                val coverFile = File(coversDir, "cover_${trackId}.jpg")
                coverFile.writeBytes(picture)
                coverArtUri = coverFile.toURI().toString()
            }
        } catch (e: Exception) {
            // Ignorer si format non supporté par le retriever
        } finally {
            try {
                retriever.release()
            } catch (ignored: Exception) {}
        }

        // 2. Extraction des paroles intégrées (ID3v2 USLT / SYLT / Vorbis)
        try {
            embeddedLyrics = extractLyricsFromStream(context, uriString)
        } catch (e: Exception) {
            // Silencieux
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
            } catch (e: Exception) {
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
            // Signature ID3v2 "ID3"
            if (header[0] == 'I'.code.toByte() && header[1] == 'D'.code.toByte() && header[2] == '3'.code.toByte()) {
                val majorVersion = header[3].toInt()
                val tagSize = ((header[6].toInt() and 0x7F) shl 21) or
                              ((header[7].toInt() and 0x7F) shl 14) or
                              ((header[8].toInt() and 0x7F) shl 7) or
                              (header[9].toInt() and 0x7F)
                val maxRead = minOf(tagSize, 2 * 1024 * 1024)
                val tagBuffer = ByteArray(maxRead)
                var bytesRead = 0
                while (bytesRead < maxRead) {
                    val count = input.read(tagBuffer, bytesRead, maxRead - bytesRead)
                    if (count <= 0) break
                    bytesRead += count
                }

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
