package com.jskaleel.vizhi_tamil.ui.screens.setup.download

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jskaleel.vizhi_tamil.core.utils.CallBack
import com.jskaleel.vizhi_tamil.ui.utils.consume

@Composable
fun DownloadScreenRoute(
    onDownloadComplete: CallBack,
    viewModel: DownloadViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    viewModel.navigation.consume { state ->
        when (state) {
            DownloadNavigationState.DownloadCompleted -> {
                onDownloadComplete()
            }
        }
    }

    when (uiState) {
        DownloadUiState.Loading -> FullScreenLoader()
        is DownloadUiState.DownloadStatus -> {
            val state = uiState as DownloadUiState.DownloadStatus
            DownloadScreen(
                progress = state.progress,
                fileSize = state.fileSize
            )
        }
    }
}