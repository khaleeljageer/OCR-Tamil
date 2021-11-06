package com.jskaleel.vizhi_tamil.utils

object Constants {
    const val ASPECT_RATIO: Double = 0.5625
    const val MAX_PAGE_SIZE: Int = 100
    val MAX_PARALLEL_JOBS = Runtime.getRuntime().availableProcessors()
    const val TESS_DATA_PATH = "fast/tessdata"
    const val DOCUMENT_PDF_PATH = "document/pdf"
    const val TESS_FAST_DATA_PATH = "fast"
    const val TESS_DATA_NAME = "%s.traineddata"
    const val TESSERACT_DATA_DOWNLOAD_URL_FAST =
        "https://github.com/tesseract-ocr/tessdata_fast/raw/4.0.0/%s.traineddata"
    const val STORE_URL = "https://play.google.com/store/apps/details?id=com.jskaleel.vizhi_tamil"
    const val CONFIG_URL =
        "https://raw.githubusercontent.com/khaleeljageer/OCR-Tamil/main/config.json"
    const val GITHUB_BASE_API = "https://api.github.com/"
    const val CONTRIBUTORS_PATH = "repos/khaleeljageer/OCR-Tamil/contributors"

    const val PRIVACY_POLICY_URL = "https://khaleeljageer.github.io/OCR-Tamil/privacy_policy.html"
    const val TERMS_CONDITIONS_URL =
        "https://khaleeljageer.github.io/OCR-Tamil/terms_conditions.html"
}