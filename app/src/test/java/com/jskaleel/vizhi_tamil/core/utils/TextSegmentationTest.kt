package com.jskaleel.vizhi_tamil.core.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TextSegmentationTest {

    @Test
    fun `blank text yields no segments`() {
        assertTrue("".speechSegments().isEmpty())
        assertTrue("   \n  ".speechSegments().isEmpty())
    }

    @Test
    fun `single sentence without terminator is one segment covering the whole string`() {
        val text = "hello world"
        val segments = text.speechSegments()
        assertEquals(1, segments.size)
        assertEquals(text, text.substring(segments[0].first, segments[0].last + 1))
    }

    @Test
    fun `sentences are split on terminators`() {
        val text = "First. Second! Third?"
        val segments = text.speechSegments()
        assertEquals(3, segments.size)
    }

    @Test
    fun `segment offsets index into the original string for TTS mapping`() {
        val text = "One.\nTwo."
        val segments = text.speechSegments()
        // Every segment must be a valid, non-blank slice of the original text.
        segments.forEach { range ->
            val slice = text.substring(range.first, range.last + 1)
            assertTrue(slice.isNotBlank())
        }
    }

    @Test
    fun `tamil sentences split on tamil punctitation`() {
        val text = "வணக்கம். நன்றி."
        assertEquals(2, text.speechSegments().size)
    }
}
