package com.jskaleel.vizhi_tamil.utils

import android.content.SharedPreferences
import timber.log.Timber

class AppPreference(private val sharedPref: SharedPreferences) {
    companion object {
        private val TAG: String = AppPreference::class.java.name
    }

    private var editor: SharedPreferences.Editor? = sharedPref.edit()

    fun put(key: String, value: Any) {
        when (value) {
            is String -> {
                editor?.let {
                    it.putString(key, value)
                    it.apply()
                } ?: Timber.d("null Preference Editor")
            }
            is Int -> {
                editor?.let {
                    it.putInt(key, value)
                    it.apply()
                } ?: Timber.d("null Preference Editor")
            }
            is Boolean -> {
                editor?.let {
                    it.putBoolean(key, value)
                    it.apply()
                } ?: Timber.d("null Preference Editor")
            }
        }
    }

    fun getString(key: String, default: String = ""): String {
        return sharedPref.getString(key, default) ?: ""
    }

    fun getInt(key: String, default: Int = 0): Int {
        return sharedPref.getInt(key, 0)
    }

    fun getBoolean(key: String, default: Boolean = false): Boolean {
        return sharedPref.getBoolean(key, default)
    }
}