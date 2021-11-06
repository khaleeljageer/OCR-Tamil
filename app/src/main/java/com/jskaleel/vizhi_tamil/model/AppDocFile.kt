package com.jskaleel.vizhi_tamil.model

import android.net.Uri
import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class AppDocFile(val uri: Uri, val name: String? = "") : Parcelable