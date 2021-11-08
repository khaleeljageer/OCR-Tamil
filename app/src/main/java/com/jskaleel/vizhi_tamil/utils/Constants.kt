package com.jskaleel.vizhi_tamil.utils

import com.jskaleel.vizhi_tamil.model.ThirdParty

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

    const val PRIVACY_POLICY_URL =
        "https://khaleeljageer.github.io/OCR-Tamil/docs/privacy_policy.html"
    const val TERMS_CONDITIONS_URL =
        "https://khaleeljageer.github.io/OCR-Tamil/docs/terms_conditions.html"

    val libraries: List<ThirdParty> = listOf(
        ThirdParty(
            "Tesseract4Android",
            "cz.adaptech:tesseract4android:3.0.0",
            "https://github.com/adaptech-cz/Tesseract4Android"
        ),
        ThirdParty("Okhttp3", "com.squareup.okhttp3:4.9.2", "https://github.com/square/okhttp"),
        ThirdParty(
            "Coroutines",
            "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.2",
            "https://kotlinlang.org/docs/coroutines-guide.html"
        ),
        ThirdParty(
            "Hilt Android",
            "com.google.dagger:hilt-android:2.39.1",
            "https://dagger.dev/hilt/"
        ),
        ThirdParty(
            "Multidex",
            "androidx.multidex:multidex:2.0.1",
            "https://developer.android.com/studio/build/multidex"
        ),
        ThirdParty(
            "Easy Permissions",
            "pub.devrel:easypermissions:3.0.0",
            "https://github.com/googlesamples/easypermissions"
        ),
        ThirdParty(
            "ImagePicker",
            "com.github.Drjacky:ImagePicker:2.1.13",
            "https://github.com/Dhaval2404/ImagePicker"
        ),
        ThirdParty("Coil", "io.coil-kt:coil:1.4.0", "https://coil-kt.github.io/coil/"),
        ThirdParty(
            "Storage",
            "com.anggrayudi:storage:0.13.0",
            "https://github.com/anggrayudi/SimpleStorage"
        ),
        ThirdParty("Gson", "com.google.code.gson:gson:2.8.8", "https://github.com/google/gson"),
        ThirdParty(
            "Timber",
            "com.jakewharton.timber:timber:5.0.1",
            "https://github.com/JakeWharton/timber"
        ),
        ThirdParty(
            "Pdfbox Android",
            "com.tom-roush:pdfbox-android:2.0.7.0",
            "https://github.com/TomRoush/PdfBox-Android"
        ),
    )
}