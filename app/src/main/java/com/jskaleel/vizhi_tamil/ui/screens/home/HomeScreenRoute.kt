package com.jskaleel.vizhi_tamil.ui.screens.home

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.RESULT_FORMAT_JPEG
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.SCANNER_MODE_FULL
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import com.jskaleel.vizhi_tamil.ui.utils.consume

@Composable
fun HomeScreenRoute(
    imageOCRDetail: (String) -> Unit,
    viewModel: HomeViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val activity = LocalActivity.current
    val context = LocalContext.current
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val scanningResult =
                    GmsDocumentScanningResult.fromActivityResultIntent(result.data)
                viewModel.handleScanResult(scanningResult)
            }
        }

    viewModel.navigation.consume {
        when (it) {
            HomeNavigationState.DocumentScanner -> {
                val scannerOptions = GmsDocumentScannerOptions.Builder()
                    .setScannerMode(SCANNER_MODE_FULL)
                    .setPageLimit(1)
                    .setGalleryImportAllowed(true)
                    .setResultFormats(RESULT_FORMAT_JPEG)
                    .build()

                val mlScanner = GmsDocumentScanning.getClient(scannerOptions)
                mlScanner.getStartScanIntent(activity!!)
                    .addOnSuccessListener { intent ->
                        launcher.launch(
                            IntentSenderRequest.Builder(intent).build()
                        )
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(context, "Scan failed: ${e.message}", Toast.LENGTH_SHORT)
                            .show()
                    }
            }

            is HomeNavigationState.ImageOCRDetail -> {
                imageOCRDetail(it.imageUri)
            }
        }
    }

    HomeScreen(
        onNewClick = viewModel::onNewClick
    )
}