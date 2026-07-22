package com.jskaleel.vizhi_tamil.ui.screens.imageDetail

import android.text.Spanned
import androidx.core.text.HtmlCompat
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.jskaleel.vizhi_tamil.core.model.onError
import com.jskaleel.vizhi_tamil.core.model.onSuccess
import com.jskaleel.vizhi_tamil.domain.usecase.OCRUseCase
import com.jskaleel.vizhi_tamil.ui.navigation.AppRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImageOCRDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val ocrUseCase: OCRUseCase,
) : ViewModel() {

    private val route = savedStateHandle.toRoute<AppRoute.ImageOcrDetail>()

    private val _uiState = MutableStateFlow<ImageOCRDetailUiState>(ImageOCRDetailUiState.Loading)
    val uiState: StateFlow<ImageOCRDetailUiState> = _uiState.asStateFlow()

    init {
        runOcr()
    }

    fun retry() = runOcr()

    private fun runOcr() {
        _uiState.value = ImageOCRDetailUiState.Loading
        viewModelScope.launch {
            ocrUseCase.fetchTextFromImage(imagePath = route.imagePath)
                .onSuccess { result ->
                    _uiState.value = ImageOCRDetailUiState.Content(
                        text = HtmlCompat.fromHtml(result.text, HtmlCompat.FROM_HTML_MODE_LEGACY),
                        accuracy = "Accuracy: ${result.accuracy}%",
                    )
                }
                .onError { _, message ->
                    _uiState.value = ImageOCRDetailUiState.Error(
                        message = message ?: "Couldn't read text from this image.",
                    )
                }
        }
    }
}

sealed interface ImageOCRDetailUiState {
    data object Loading : ImageOCRDetailUiState
    data class Content(val text: Spanned, val accuracy: String) : ImageOCRDetailUiState
    data class Error(val message: String) : ImageOCRDetailUiState
}
