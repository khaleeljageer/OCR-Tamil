package com.jskaleel.vizhi_tamil.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jskaleel.vizhi_tamil.domain.model.ImageOCR
import com.jskaleel.vizhi_tamil.domain.usecase.OCRUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    ocrUseCase: OCRUseCase,
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = ocrUseCase.getRecentScans()
        .map { scans ->
            if (scans.isEmpty()) HomeUiState.Empty else HomeUiState.Content(scans)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = HomeUiState.Loading,
        )

    companion object {
        private const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data object Empty : HomeUiState
    data class Content(val scans: List<ImageOCR>) : HomeUiState
}
