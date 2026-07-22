package com.jskaleel.vizhi_tamil.core.utils

/**
 * Splits recognised text into speech segments (roughly sentences). The returned
 * ranges index into the original string, so a future highlighted-TTS feature can
 * map [android.speech.tts.TextToSpeech]'s `onRangeStart(start, end)` offsets
 * straight onto the displayed text. This is the seam the AnnotatedString builds on;
 * it exists precisely because storage is plain UTF-8 text (not HTML).
 */
fun String.speechSegments(): List<IntRange> {
    if (isBlank()) return emptyList()
    val segments = mutableListOf<IntRange>()
    var start = 0
    forEachIndexed { index, char ->
        if (char in SEGMENT_TERMINATORS) {
            if (index >= start) segments += start..index
            start = index + 1
        }
    }
    if (start <= lastIndex) segments += start..lastIndex
    return segments.filter { range -> substring(range.first, range.last + 1).isNotBlank() }
}

private val SEGMENT_TERMINATORS = setOf('.', '!', '?', '\n', '।')
