package com.jskaleel.vizhi_tamil.ui.screens.imageDetail

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.jskaleel.vizhi_tamil.R
import com.jskaleel.vizhi_tamil.core.utils.speechSegments
import com.jskaleel.vizhi_tamil.domain.model.ImageOCR
import com.jskaleel.vizhi_tamil.ui.components.ConfidenceBadge
import java.io.File

@Composable
fun ImageOCRDetailScreen(
    content: ImageOCRDetailUiState.Content,
    callbacks: DetailCallbacks,
) {
    val scan = content.scan
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    var editText by remember(scan.id, content.isEditing) { mutableStateOf(scan.text) }

    Scaffold(
        topBar = {
            DetailTopBar(
                isEditing = content.isEditing,
                onBack = callbacks.onBack,
                onSave = { callbacks.onSave(editText) },
                onCancel = callbacks.onCancelEdit,
                menu = {
                    DetailActionsMenu(
                        onCopy = {
                            clipboard.setText(AnnotatedString(scan.text))
                            context.toast(R.string.detail_copied)
                        },
                        onShare = { context.shareText(scan.text) },
                        onExport = callbacks.onExport,
                        onEdit = callbacks.onStartEdit,
                        onDelete = callbacks.onDelete,
                    )
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        ) {
            ScanImage(imagePath = scan.imagePath)
            Spacer(Modifier.height(12.dp))
            MetaRow(scan = scan)
            Spacer(Modifier.height(16.dp))
            if (content.isEditing) {
                OutlinedTextField(
                    value = editText,
                    onValueChange = { editText = it },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodyLarge,
                )
            } else {
                SelectionContainer {
                    Text(
                        text = rememberSpeechAnnotatedString(scan.text),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailTopBar(
    isEditing: Boolean,
    onBack: () -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    menu: @Composable () -> Unit,
) {
    TopAppBar(
        title = { Text(text = stringResource(R.string.detail_title)) },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.action_back),
                )
            }
        },
        actions = {
            if (isEditing) {
                IconButton(onClick = onSave) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = stringResource(R.string.action_save),
                    )
                }
                IconButton(onClick = onCancel) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = stringResource(R.string.action_cancel),
                    )
                }
            } else {
                menu()
            }
        },
    )
}

@Composable
private fun ScanImage(imagePath: String) {
    val context = LocalContext.current
    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(File(imagePath))
            .crossfade(true)
            .build(),
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(DEFAULT_PREVIEW_RATIO)
            .clip(RoundedCornerShape(12.dp)),
    )
}

@Composable
private fun MetaRow(scan: ImageOCR) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        ConfidenceBadge(accuracy = scan.accuracy)
        Spacer(Modifier.width(8.dp))
        Text(
            text = scan.timeStamp,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun DetailActionsMenu(
    onCopy: () -> Unit,
    onShare: () -> Unit,
    onExport: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    IconButton(onClick = { expanded = true }) {
        Icon(
            imageVector = Icons.Filled.MoreVert,
            contentDescription = stringResource(R.string.action_more),
        )
    }
    // Each action closes the menu, then runs. Wrapped so the dismiss is centralised.
    fun runAndClose(action: () -> Unit): () -> Unit = {
        expanded = false
        action()
    }
    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
        MenuAction(R.string.action_copy, runAndClose(onCopy))
        MenuAction(R.string.action_share, runAndClose(onShare))
        MenuAction(R.string.action_export, runAndClose(onExport))
        MenuAction(R.string.action_edit, runAndClose(onEdit))
        MenuAction(R.string.action_delete, runAndClose(onDelete))
    }
}

@Composable
private fun MenuAction(labelRes: Int, onClick: () -> Unit) {
    DropdownMenuItem(
        text = { Text(stringResource(labelRes)) },
        onClick = onClick,
    )
}

/**
 * Builds the selectable text and tags each speech segment with its offset range,
 * so a future highlighted-TTS feature can drive per-segment highlighting.
 */
@Composable
private fun rememberSpeechAnnotatedString(text: String): AnnotatedString =
    remember(text) {
        buildAnnotatedString {
            append(text)
            text.speechSegments().forEachIndexed { index, range ->
                addStringAnnotation(
                    tag = SPEECH_TAG,
                    annotation = index.toString(),
                    start = range.first,
                    end = range.last + 1,
                )
            }
        }
    }

private fun Context.toast(resId: Int) {
    Toast.makeText(this, getString(resId), Toast.LENGTH_SHORT).show()
}

private fun Context.shareText(text: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    startActivity(Intent.createChooser(intent, getString(R.string.action_share)))
}

private const val SPEECH_TAG = "speech"
private const val DEFAULT_PREVIEW_RATIO = 4f / 3f
