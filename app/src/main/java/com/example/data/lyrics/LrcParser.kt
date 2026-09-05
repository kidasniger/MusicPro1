package com.example.data.lyrics

import java.util.regex.Pattern

data class LyricLine(
    val timeFormatted: String,
    val timeMs: Long,
    val text: String,
    val translatedText: String? = null
)

object LrcParser {
    // Regex pour [mm:ss.xx] ou [mm:ss.xxx] ou [mm:ss]
    private val LRC_LINE_PATTERN = Pattern.compile("\\[(\\d{1,2}):(\\d{2})(?:\\.(\\d{1,3}))?\\](.*)")

    fun parse(lrcContent: String): List<LyricLine> {
        val lines = lrcContent.lines()
        val result = mutableListOf<LyricLine>()

        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.isEmpty()) continue

            val matcher = LRC_LINE_PATTERN.matcher(trimmed)
            if (matcher.matches()) {
                val minStr = matcher.group(1) ?: "0"
                val secStr = matcher.group(2) ?: "0"
                val fracStr = matcher.group(3) ?: "0"
                val text = matcher.group(4)?.trim() ?: ""

                val minutes = minStr.toLongOrNull() ?: 0L
                val seconds = secStr.toLongOrNull() ?: 0L
                val ms = when (fracStr.length) {
                    1 -> (fracStr.toLongOrNull() ?: 0L) * 100
                    2 -> (fracStr.toLongOrNull() ?: 0L) * 10
                    3 -> fracStr.toLongOrNull() ?: 0L
                    else -> 0L
                }

                val totalMs = minutes * 60_000L + seconds * 1_000L + ms
                val formatted = String.format("%02d:%02d", minutes, seconds)

                result.add(
                    LyricLine(
                        timeFormatted = formatted,
                        timeMs = totalMs,
                        text = text
                    )
                )
            }
        }

        return result.sortedBy { it.timeMs }
    }

    fun toLrc(lines: List<LyricLine>): String {
        val sb = StringBuilder()
        for (line in lines) {
            val totalSeconds = line.timeMs / 1000
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            val msPart = (line.timeMs % 1000) / 10
            sb.append(String.format("[%02d:%02d.%02d]%s\n", minutes, seconds, msPart, line.text))
        }
        return sb.toString()
    }
}
