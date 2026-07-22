package com.jskaleel.vizhi_tamil.domain.model

data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val ocrLanguage: OcrLanguage = OcrLanguage.TAMIL_ENGLISH,
    val pageSegMode: PageSegmentation = PageSegmentation.AUTO_OSD,
    val exportFormat: ExportFormat = ExportFormat.TXT,
)

enum class ThemeMode { SYSTEM, LIGHT, DARK }

/** OCR language; [code] is the Tesseract traineddata identifier. */
enum class OcrLanguage(val code: String) {
    TAMIL("tam"),
    ENGLISH("eng"),
    TAMIL_ENGLISH("tam+eng"),
}

/** Tesseract page-segmentation modes exposed to the user. */
enum class PageSegmentation { AUTO_OSD, AUTO, SINGLE_BLOCK, SINGLE_LINE }

enum class ExportFormat(val extension: String, val mimeType: String) {
    TXT("txt", "text/plain"),
    PDF("pdf", "application/pdf"),
}
