package com.jskaleel.vizhi_tamil.ui.screens.imageDetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.jskaleel.vizhi_tamil.domain.model.ImageOCR
import com.jskaleel.vizhi_tamil.domain.usecase.OCRUseCase
import com.jskaleel.vizhi_tamil.ui.navigation.AppRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImageOCRDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val ocrUseCase: OCRUseCase,
) : ViewModel() {

    private val scanId = savedStateHandle.toRoute<AppRoute.ImageOcrDetail>().scanId

    private val _uiState = MutableStateFlow<ImageOCRDetailUiState>(ImageOCRDetailUiState.Loading)
    val uiState: StateFlow<ImageOCRDetailUiState> = _uiState.asStateFlow()

    private val _events = Channel<DetailEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        load()
    }

    fun retry() = load()

    private fun load() {
        _uiState.value = ImageOCRDetailUiState.Loading
        viewModelScope.launch {
            val scan = ocrUseCase.getScan(scanId)
            _uiState.value = if (scan != null) {
                ImageOCRDetailUiState.Content(scan)
            } else {
                ImageOCRDetailUiState.Error("This scan could not be found.")
            }
        }
    }

    fun onStartEdit() = updateContent { it.copy(isEditing = true) }

    fun onCancelEdit() = updateContent { it.copy(isEditing = false) }

    fun onSaveEdit(newText: String) {
        val current = _uiState.value as? ImageOCRDetailUiState.Content ?: return
        viewModelScope.launch {
            ocrUseCase.updateScanText(scanId, newText)
            _uiState.value = current.copy(
                scan = current.scan.copy(text = newText),
                isEditing = false,
            )
        }
    }

    fun onDelete() {
        val current = _uiState.value as? ImageOCRDetailUiState.Content ?: return
        viewModelScope.launch {
            ocrUseCase.deleteScans(listOf(current.scan))
            _events.send(DetailEvent.Deleted)
        }
    }

    private inline fun updateContent(block: (ImageOCRDetailUiState.Content) -> ImageOCRDetailUiState.Content) {
        (_uiState.value as? ImageOCRDetailUiState.Content)?.let { _uiState.value = block(it) }
    }
}

sealed interface ImageOCRDetailUiState {
    data object Loading : ImageOCRDetailUiState
    data class Content(val scan: ImageOCR, val isEditing: Boolean = false) : ImageOCRDetailUiState
    data class Error(val message: String) : ImageOCRDetailUiState
}

sealed interface DetailEvent {
    data object Deleted : DetailEvent
}
