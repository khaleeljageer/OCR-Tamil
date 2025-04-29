package com.jskaleel.vizhi_tamil.ui.screens.setup.download

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jskaleel.vizhi_tamil.core.model.onError
import com.jskaleel.vizhi_tamil.core.model.onSuccess
import com.jskaleel.vizhi_tamil.domain.usecase.SetupUseCase
import com.jskaleel.vizhi_tamil.ui.utils.mutableNavigationState
import com.jskaleel.vizhi_tamil.ui.utils.navigate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DownloadViewModel @Inject constructor(
    val setupUseCase: SetupUseCase
) : ViewModel() {

    var navigation by mutableNavigationState<DownloadNavigationState>()
        private set

    private val viewModelState = MutableStateFlow(DownloadViewModelState())

    val uiState = viewModelState.map { it.toUiState() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = viewModelState.value.toUiState()
        )

    init {
        checkUpdate()
    }

    private fun checkUpdate() {
        viewModelScope.launch(Dispatchers.IO) {
            viewModelState.update { it.copy(loading = true) }
            setupUseCase.checkForModelUpdate()
                .onSuccess { it ->
                    if (it.updateData) {
                        downloadNewModel()
                    } else {
                        checkModelExists()
                    }
                }.onError { code, error ->
                    checkModelExists()
                }
        }
    }

    private suspend fun checkModelExists() {
        downloadNewModel()
    }

    private fun downloadNewModel() {
        viewModelScope.launch(Dispatchers.IO) {
            setupUseCase.downloadModel().collect { result ->
                viewModelState.update { it.copy(loading = false) }
                result.onSuccess { progress ->
                    viewModelState.update {
                        it.copy(
                            progress = progress.progress,
                            fileSize = progress.fileSize,
                        )
                    }
                    if (progress.fileCompleted) {
                        viewModelState.update { it.copy(loading = true) }
                        delay(800)
                        navigation = navigate(DownloadNavigationState.DownloadCompleted)
                    }
                }.onError { _, _ ->

                }
            }
        }
    }
}

private data class DownloadViewModelState(
    val loading: Boolean = true,
    val progress: Float = 0.1f,
    val fileSize: String = ""
) {
    fun toUiState() = if (loading) {
        DownloadUiState.Loading
    } else {
        DownloadUiState.DownloadStatus(
            progress = progress,
            fileSize = fileSize,
        )
    }
}

sealed interface DownloadUiState {
    data object Loading : DownloadUiState
    data class DownloadStatus(
        val progress: Float,
        val fileSize: String,
    ) : DownloadUiState
}

sealed interface DownloadNavigationState {
    object DownloadCompleted : DownloadNavigationState
}