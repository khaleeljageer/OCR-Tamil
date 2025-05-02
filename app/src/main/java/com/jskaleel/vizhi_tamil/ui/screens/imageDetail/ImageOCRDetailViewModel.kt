package com.jskaleel.vizhi_tamil.ui.screens.imageDetail

import android.text.Spanned
import android.util.Log
import androidx.core.text.HtmlCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jskaleel.vizhi_tamil.core.model.onError
import com.jskaleel.vizhi_tamil.core.model.onSuccess
import com.jskaleel.vizhi_tamil.domain.usecase.OCRUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImageOCRDetailViewModel @Inject constructor(
    private val ocrUseCase: OCRUseCase
) : ViewModel() {
    private val viewModelState = MutableStateFlow(ImageOCRDetailViewModelState())

    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = viewModelState.value.toUiState()
        )

    fun setup(imagePath: String) {
        viewModelScope.launch(Dispatchers.IO) {
            viewModelState.update {
                it.copy(loading = true, text = "")
            }
            ocrUseCase.fetchTextFromImage(imagePath = imagePath)
                .onSuccess { result ->
                    viewModelState.update {
                        it.copy(
                            loading = false,
                            text = result.text,
                            accuracy = result.accuracy
                        )
                    }
                }.onError { _, it ->
                    Log.d("ImageOCRDetailViewModel", "onError: $it")
                }
        }
    }
}

data class ImageOCRDetailViewModelState(
    val loading: Boolean = true,
    val text: String = "",
    val accuracy: Int = 0
) {
    fun toUiState() = if (!loading) {
        Log.d("ImageOCRDetailViewModel", "toUiState: $text")
        ImageOCRDetailUiState.Result(
            text = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY),
            accuracy = "Accuracy: $accuracy%"
        )
    } else {
        ImageOCRDetailUiState.Loading
    }
}

sealed interface ImageOCRDetailUiState {
    data object Loading : ImageOCRDetailUiState
    data class Result(val text: Spanned, val accuracy: String) : ImageOCRDetailUiState
}