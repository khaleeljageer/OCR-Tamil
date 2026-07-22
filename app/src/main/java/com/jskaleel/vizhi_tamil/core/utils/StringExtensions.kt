package com.jskaleel.vizhi_tamil.core.utils

import androidx.core.text.HtmlCompat

fun String?.valueOrDefault(default: String = ""): String = this ?: default

/**
 * Flattens hOCR/HTML OCR output to plain text for list snippets and search.
 * Temporary: Phase 3 switches storage to plain UTF-8 text and this becomes a no-op.
 */
fun String.stripHtml(): String =
    HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_LEGACY).toString().trim()
