package com.jskaleel.vizhi_tamil.ui.screens.home

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.RESULT_FORMAT_JPEG
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.SCANNER_MODE_FULL
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult

@Composable
fun HomeScreenRoute(
    onOpenDetail: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val activity = LocalActivity.current
    val context = LocalContext.current

    val scannerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult(),
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val scanningResult = GmsDocumentScanningResult.fromActivityResultIntent(result.data)
            scanningResult?.pages?.firstOrNull()?.imageUri?.path?.let(onOpenDetail)
        }
    }

    HomeScreen(
        uiState = uiState,
        callbacks = HomeCallbacks(
            onScanClick = { startDocumentScan(activity, context, scannerLauncher) },
            onScanItemClick = onOpenDetail,
            onSearchChange = viewModel::onSearchChange,
            onClearSearch = viewModel::onClearSearch,
            onToggleSelection = viewModel::onToggleSelection,
            onClearSelection = viewModel::onClearSelection,
            onDeleteSelected = viewModel::onDeleteSelected,
            onDeleteScan = viewModel::onDeleteScan,
        ),
    )
}

private fun startDocumentScan(
    activity: Activity?,
    context: Context,
    launcher: ActivityResultLauncher<IntentSenderRequest>,
) {
    if (activity == null) return
    val options = GmsDocumentScannerOptions.Builder()
        .setScannerMode(SCANNER_MODE_FULL)
        .setPageLimit(1)
        .setGalleryImportAllowed(true)
        .setResultFormats(RESULT_FORMAT_JPEG)
        .build()

    GmsDocumentScanning.getClient(options)
        .getStartScanIntent(activity)
        .addOnSuccessListener { intentSender ->
            launcher.launch(IntentSenderRequest.Builder(intentSender).build())
        }
        .addOnFailureListener { e ->
            Toast.makeText(context, "Scan failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
}
