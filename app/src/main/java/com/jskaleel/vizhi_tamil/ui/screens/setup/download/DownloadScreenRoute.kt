package com.jskaleel.vizhi_tamil.ui.screens.setup.download

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jskaleel.vizhi_tamil.ui.utils.consume

@Composable
fun DownloadScreenRoute(viewModel: DownloadViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    viewModel.navigation.consume { state ->

    }

    DownloadScreen(
        loading = uiState.loading
    )
}