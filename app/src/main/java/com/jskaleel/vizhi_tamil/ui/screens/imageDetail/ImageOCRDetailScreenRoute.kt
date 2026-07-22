package com.jskaleel.vizhi_tamil.ui.screens.imageDetail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jskaleel.vizhi_tamil.ui.components.ErrorState
import com.jskaleel.vizhi_tamil.ui.components.LoadingIndicator

@Composable
fun ImageOCRDetailScreenRoute(
    onBack: () -> Unit,
    viewModel: ImageOCRDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                DetailEvent.Deleted -> onBack()
            }
        }
    }

    when (val state = uiState) {
        ImageOCRDetailUiState.Loading -> LoadingIndicator()

        is ImageOCRDetailUiState.Content -> ImageOCRDetailScreen(
            content = state,
            callbacks = DetailCallbacks(
                onBack = onBack,
                onStartEdit = viewModel::onStartEdit,
                onCancelEdit = viewModel::onCancelEdit,
                onSave = viewModel::onSaveEdit,
                onDelete = viewModel::onDelete,
            ),
        )

        is ImageOCRDetailUiState.Error -> ErrorState(
            message = state.message,
            onRetry = viewModel::retry,
        )
    }
}
