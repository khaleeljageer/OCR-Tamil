package com.jskaleel.vizhi_tamil.domain.model

data class DownloadProgress(
    val progress: Float, // 0.0 to 1.0
    val bytesDownloaded: Long,
    val fileSize: String,
    val fileCompleted: Boolean
)