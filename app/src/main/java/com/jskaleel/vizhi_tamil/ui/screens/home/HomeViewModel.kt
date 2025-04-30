package com.jskaleel.vizhi_tamil.ui.screens.home

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import com.jskaleel.vizhi_tamil.ui.utils.mutableNavigationState
import com.jskaleel.vizhi_tamil.ui.utils.navigate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {
    var navigation by mutableNavigationState<HomeNavigationState>()
        private set

    private val viewModelState = MutableStateFlow(HomeViewModelState())

    val uiState = viewModelState.map { it.toUiState() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = viewModelState.value.toUiState()
        )

    fun onNewClick() {
        navigation = navigate(HomeNavigationState.DocumentScanner)
    }

    fun handleScanResult(scanningResult: GmsDocumentScanningResult?) {
        scanningResult?.pages?.let { pages ->
            val imageUri = pages[0].imageUri
            Log.d("HomeViewModel", "handleScanResult: $imageUri")
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