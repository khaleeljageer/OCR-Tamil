package com.jskaleel.vizhi_tamil.ui.screens.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.jskaleel.vizhi_tamil.R
import com.jskaleel.vizhi_tamil.domain.model.ImageOCR
import com.jskaleel.vizhi_tamil.ui.components.ConfidenceBadge
import com.jskaleel.vizhi_tamil.ui.components.EmptyState
import com.jskaleel.vizhi_tamil.ui.components.LoadingIndicator
import com.jskaleel.vizhi_tamil.ui.theme.VizhiTamilTheme
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    callbacks: HomeCallbacks,
) {
    val selectionMode = (uiState as? HomeUiState.Content)?.inSelectionMode == true

    // Back exits selection mode before leaving the screen.
    BackHandler(enabled = selectionMode) { callbacks.onClearSelection() }

    Scaffold(
        topBar = {
            HomeTopBar(
                selectedCount = (uiState as? HomeUiState.Content)?.selectedIds?.size ?: 0,
                onClearSelection = callbacks.onClearSelection,
                onDeleteSelected = callbacks.onDeleteSelected,
            )
        },
        floatingActionButton = {
            if (uiState is HomeUiState.Content && !selectionMode) {
                ExtendedFloatingActionButton(
                    onClick = callbacks.onScanClick,
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.rounded_scanner_24),
                            contentDescription = null,
                        )
                    },
                    text = { Text(text = stringResource(R.string.action_scan)) },
                )
            }
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            when (uiState) {
                HomeUiState.Loading -> LoadingIndicator()

                HomeUiState.Empty -> EmptyState(
                    icon = painterResource(R.drawable.rounded_scanner_24),
                    title = stringResource(R.string.home_empty_title),
                    description = stringResource(R.string.home_empty_desc),
                )

                is HomeUiState.Content -> ContentBody(
                    state = uiState,
                    callbacks = callbacks,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopBar(
    selectedCount: Int,
    onClearSelection: () -> Unit,
    onDeleteSelected: () -> Unit,
) {
    if (selectedCount > 0) {
        TopAppBar(
            title = {
                Text(text = stringResource(R.string.home_selected_count, selectedCount))
            },
            navigationIcon = {
                IconButton(onClick = onClearSelection) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = stringResource(R.string.action_clear_selection),
                    )
                }
            },
            actions = {
                IconButton(onClick = onDeleteSelected) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = stringResource(R.string.action_delete),
                    )
                }
            },
        )
    } else {
        TopAppBar(title = { Text(text = stringResource(R.string.app_name)) })
    }
}

@Composable
private fun ContentBody(
    state: HomeUiState.Content,
    callbacks: HomeCallbacks,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        SearchField(
            query = state.query,
            onQueryChange = callbacks.onSearchChange,
            onClear = callbacks.onClearSearch,
        )
        if (state.scans.isEmpty()) {
            NoResults(query = state.query)
        } else {
            RecentScanList(state = state, callbacks = callbacks)
        }
    }
}

@Composable
private fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        placeholder = { Text(text = stringResource(R.string.home_search_hint)) },
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = onClear) {
                    Icon(
                        imageVector = Icons.Filled.Clear,
                        contentDescription = stringResource(R.string.action_clear_search),
                    )
                }
            }
        },
        singleLine = true,
    )
}

@Composable
private fun NoResults(query: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.home_no_results, query),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(24.dp),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecentScanList(
    state: HomeUiState.Content,
    callbacks: HomeCallbacks,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 4.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(items = state.scans, key = { it.id }) { scan ->
            val selected = scan.id in state.selectedIds
            val dismissState = rememberSwipeToDismissBoxState(
                confirmValueChange = { value ->
                    val dismissed = value != SwipeToDismissBoxValue.Settled
                    if (dismissed) callbacks.onDeleteScan(scan)
                    dismissed
                },
            )
            SwipeToDismissBox(
                state = dismissState,
                enableDismissFromStartToEnd = !state.inSelectionMode,
                enableDismissFromEndToStart = !state.inSelectionMode,
                backgroundContent = { SwipeDeleteBackground() },
            ) {
                RecentScanItem(
                    scan = scan,
                    selected = selected,
                    onClick = {
                        if (state.inSelectionMode) {
                            callbacks.onToggleSelection(scan.id)
                        } else {
                            callbacks.onScanItemClick(scan.imagePath)
                        }
                    },
                    onLongClick = { callbacks.onToggleSelection(scan.id) },
                )
            }
        }
    }
}

@Composable
private fun SwipeDeleteBackground() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.errorContainer)
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.CenterEnd,
    ) {
        Icon(
            imageVector = Icons.Filled.Delete,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onErrorContainer,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecentScanItem(
    scan: ImageOCR,
    selected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    val context = LocalContext.current
    val containerColor = if (selected) {
        MaterialTheme.colorScheme.secondaryContainer
    } else {
        Color.Unspecified
    }
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(onClick = onClick, onLongClick = onLongClick),
    ) {
        Row(
            modifier = Modifier
                .then(if (selected) Modifier.background(containerColor) else Modifier)
                .padding(12.dp),
        ) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(File(scan.imagePath))
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp)),
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = scan.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ConfidenceBadge(accuracy = scan.accuracy)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = scan.timeStamp,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true, device = "spec:parent=pixel_6")
@Composable
private fun HomeScreenContentPreview() {
    VizhiTamilTheme {
        HomeScreen(
            uiState = HomeUiState.Content(
                scans = listOf(
                    ImageOCR("இது ஒரு எடுத்துக்காட்டு உரை", 92, "2 hours ago", "/tmp/a.jpg", 1),
                    ImageOCR("மற்றொரு ஸ்கேன்", 68, "yesterday", "/tmp/b.jpg", 2),
                ),
            ),
            callbacks = HomeCallbacks(),
        )
    }
}
