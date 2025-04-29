package com.jskaleel.vizhi_tamil.data.model

import com.jskaleel.vizhi_tamil.domain.model.DownloadProgress

data class DownloadResponse(
    val progress: Float, // 0.0 to 1.0
    val bytesDownloaded: Long,
    val fileSize: String,
    val fileCompleted: Boolean = false,
    val chunk: ByteArray? = null
) {
    fun toDomain(): DownloadProgress {
        return DownloadProgress(
            progress = progress,
            bytesDownloaded = bytesDownloaded,
            fileSize = fileSize,
            fileCompleted = fileCompleted,
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DownloadResponse

        if (progress != other.progress) return false
        if (bytesDownloaded != other.bytesDownloaded) return false
        if (fileSize != other.fileSize) return false
        if (!chunk.contentEquals(other.chunk)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = progress.hashCode()
        result = 31 * result + bytesDownloaded.hashCode()
        result = 31 * result + fileSize.hashCode()
        result = 31 * result + (chunk?.contentHashCode() ?: 0)
        return result
    }
}