package com.jskaleel.vizhi_tamil.ui.navigation

import android.net.Uri
import android.os.Bundle
import com.jskaleel.vizhi_tamil.core.utils.valueOrDefault

sealed class Screen(val route: String) {
    object Download : Screen("download")
    object Home : Screen("home")
    object About : Screen("about")
    object ImageOCRDetail : Screen("image_ocr_detail") {
        object Link {
            private const val IMAGE_PATH = "image_path"
            val link = "$route/{$IMAGE_PATH}"
            fun create(imagePath: String): String {
                return "$route/${Uri.encode(imagePath)}"
            }

            fun get(bundle: Bundle?): String {
                return bundle?.getString(IMAGE_PATH).valueOrDefault()
            }
        }
    }
}

sealed class Route(val name: String) {
    object Setup : Route("Route_Setup")
    object Main : Route("Route_Main")
}