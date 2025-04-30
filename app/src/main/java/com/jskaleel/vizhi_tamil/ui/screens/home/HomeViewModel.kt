package com.jskaleel.vizhi_tamil.ui.screens.home

import android.text.Html
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.core.text.HtmlCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import com.jskaleel.vizhi_tamil.core.model.onSuccess
import com.jskaleel.vizhi_tamil.domain.usecase.OCRUseCase
import com.jskaleel.vizhi_tamil.ui.utils.mutableNavigationState
import com.jskaleel.vizhi_tamil.ui.utils.navigate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val ocrUseCase: OCRUseCase
) : ViewModel() {
    var navigation by mutableNavigationState<HomeNavigationState>()
        private set

    private val viewModelState = MutableStateFlow(HomeViewModelState())

    val uiState = viewModelState.map { it.toUiState() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = viewModelState.value.toUiState()
        )

    init {
        fetchPreviousScans()
    }

    private fun fetchPreviousScans() {
        viewModelScope.launch(Dispatchers.IO) {

        }
    }

    fun onNewClick() {
        navigation = navigate(HomeNavigationState.DocumentScanner)
    }

    fun handleScanResult(scanningResult: GmsDocumentScanningResult?) {
        scanningResult?.pages?.let { pages ->
            val imageUri = pages[0].imageUri
            Log.d("HomeViewModel", "handleScanResult: $imageUri")
            imageUri.path?.let { path ->
                Log.d("HomeViewModel", "path: $path")
                viewModelScope.launch(Dispatchers.IO) {
                    ocrUseCase.fetchTextFromImage(imagePath = path).onSuccess {
                        Log.d("HomeViewModel", "fetchTextFromImage: ${Html.fromHtml(it.text, Html.FROM_HTML_MODE_LEGACY)}")
                    }
                }
            }
        }
    }
}

private data class HomeViewModelState(
    val loading: Boolean = true,
) {
    fun toUiState() = if (loading) {
        HomeUiState.Loading
    } else {

    }
}

sealed interface HomeUiState {
    data object Loading : HomeUiState
}

sealed interface HomeNavigationState {
    object DocumentScanner : HomeNavigationState
}