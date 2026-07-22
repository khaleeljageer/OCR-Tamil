package com.jskaleel.vizhi_tamil.core.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Long.toRelativeTimeStamp(): String {
    val now = System.currentTimeMillis()
    val diff = now - this

    return when {
        diff < 60_000 -> "one minute ago"
        diff < 60 * 60_000 -> "${diff / 60_000} minutes ago"
        diff < 24 * 60 * 60_000 -> "${diff / (60 * 60_000)} hours ago"
        diff < 7 * 24 * 60 * 60_000 -> "${diff / (24 * 60 * 60_000)} days ago"
        else -> {
            val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            sdf.format(Date(this))
        }
    }
}
