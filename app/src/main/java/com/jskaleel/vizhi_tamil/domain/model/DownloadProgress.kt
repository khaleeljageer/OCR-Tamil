package com.jskaleel.vizhi_tamil.domain.model

data class DownloadProgress(
    val fileName: String,
    val progress: Float, // 0.0 to 1.0
    val bytesDownloaded: Long,
    val totalBytes: Long?
)