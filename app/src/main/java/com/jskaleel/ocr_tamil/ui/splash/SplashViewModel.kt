package com.jskaleel.ocr_tamil.ui.splash

import androidx.lifecycle.ViewModel
import com.jskaleel.ocr_tamil.utils.FileUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(private val fileUtils: FileUtils) : ViewModel() {

}