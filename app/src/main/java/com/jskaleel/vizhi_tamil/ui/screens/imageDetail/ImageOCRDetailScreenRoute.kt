package com.jskaleel.vizhi_tamil.ui.screens.imageDetail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jskaleel.vizhi_tamil.ui.screens.common.FullScreenLoader

@Composable
fun ImageOCRDetailScreenRoute(
    viewModel: ImageOCRDetailViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (uiState) {
        ImageOCRDetailUiState.Loading -> FullScreenLoader()
        is ImageOCRDetailUiState.Result -> {
            val result = uiState as ImageOCRDetailUiState.Result
            ImageOCRDetailScreen(
                text = result.text,
                accuracy = result.accuracy,
            )
        }
    }
}