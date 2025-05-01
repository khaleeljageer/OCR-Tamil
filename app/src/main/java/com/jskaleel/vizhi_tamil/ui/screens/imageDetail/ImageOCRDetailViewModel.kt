package com.jskaleel.vizhi_tamil.ui.screens.imageDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jskaleel.vizhi_tamil.core.model.onSuccess
import com.jskaleel.vizhi_tamil.domain.usecase.OCRUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImageOCRDetailViewModel @Inject constructor(
    private val ocrUseCase: OCRUseCase
) : ViewModel() {

    fun setup(imagePath: String) {
        viewModelScope.launch(Dispatchers.IO) {
            ocrUseCase.fetchTextFromImage(imagePath = imagePath)
                .onSuccess {

                }
        }
    }
}