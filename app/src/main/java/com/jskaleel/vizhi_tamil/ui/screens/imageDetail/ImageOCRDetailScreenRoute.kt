package com.jskaleel.vizhi_tamil.ui.screens.imageDetail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jskaleel.vizhi_tamil.ui.components.ErrorState
import com.jskaleel.vizhi_tamil.ui.components.LoadingIndicator

@Composable
fun ImageOCRDetailScreenRoute(
    viewModel: ImageOCRDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        ImageOCRDetailUiState.Loading -> LoadingIndicator(message = "Recognising text…")

        is ImageOCRDetailUiState.Content -> ImageOCRDetailScreen(
            text = state.text,
            accuracy = state.accuracy,
        )

        is ImageOCRDetailUiState.Error -> ErrorState(
            message = state.message,
            onRetry = viewModel::retry,
        )
    }
}
