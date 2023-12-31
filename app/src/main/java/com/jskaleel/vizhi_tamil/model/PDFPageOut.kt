package com.jskaleel.vizhi_tamil.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class PDFPageOut(val output: String, val accuracy: Int) : Parcelable