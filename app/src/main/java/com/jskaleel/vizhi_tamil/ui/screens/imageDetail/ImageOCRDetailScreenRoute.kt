package com.jskaleel.vizhi_tamil.ui.screens.imageDetail

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jskaleel.vizhi_tamil.R
import com.jskaleel.vizhi_tamil.ui.components.ErrorState
import com.jskaleel.vizhi_tamil.ui.components.LoadingIndicator

@Composable
fun ImageOCRDetailScreenRoute(
    onBack: () -> Unit,
    viewModel: ImageOCRDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                DetailEvent.Deleted -> onBack()
                is DetailEvent.Share -> context.shareFile(event.uri, event.mimeType)
                DetailEvent.ExportFailed ->
                    Toast.makeText(
                        context,
                        context.getString(R.string.detail_export_failed),
                        Toast.LENGTH_SHORT,
                    ).show()
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
                onExport = viewModel::onExport,
            ),
        )

        is ImageOCRDetailUiState.Error -> ErrorState(
            message = state.message,
            onRetry = viewModel::retry,
        )
    }
}

private fun Context.shareFile(uri: Uri, mimeType: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = mimeType
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    startActivity(Intent.createChooser(intent, getString(R.string.action_export)))
}
