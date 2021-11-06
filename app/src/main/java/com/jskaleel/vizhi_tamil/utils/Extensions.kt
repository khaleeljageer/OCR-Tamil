package com.jskaleel.vizhi_tamil.utils

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.view.View
import java.text.SimpleDateFormat
import java.util.*


fun Long.toMB(): String {
    val size = (this / 1048576L).toDouble()
    val result = String.format("%.2f", size)
    return "$result MB"
}

fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager?
    if (connectivityManager != null) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                        return true
                    }
                }
            }
        } else {
            try {
                val activeNetworkInfo = connectivityManager.activeNetworkInfo
                if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                    return true
                }
            } catch (e: Exception) {
            }
        }
    }
    return false
}

fun toReadableDate(timeStamp: Long): String {
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd EEE hh:mm a", Locale.getDefault())
        val netDate = Date(timeStamp)
        sdf.format(netDate)
    } catch (e: Exception) {
        "$timeStamp"
    }
}

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.hideView() {
    this.visibility = View.GONE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}


fun openUrl(context: Context, url: String) {
    val shareIntent: Intent = Intent().apply {
        action = Intent.ACTION_VIEW
        data = Uri.parse(url)
    }
    shareIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    context.startActivity(shareIntent)
}