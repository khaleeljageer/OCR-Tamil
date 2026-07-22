package com.jskaleel.vizhi_tamil.ui.screens.home

import androidx.compose.foundation.clickable
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
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat
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

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onScanClick: () -> Unit,
    onScanItemClick: (String) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        when (uiState) {
            HomeUiState.Loading -> LoadingIndicator()

            HomeUiState.Empty -> EmptyState(
                icon = painterResource(R.drawable.rounded_scanner_24),
                title = "No scans yet",
                description = "Tap Scan to capture a document and extract Tamil text.",
            )

            is HomeUiState.Content -> RecentScanList(
                scans = uiState.scans,
                onScanItemClick = onScanItemClick,
            )
        }

        // The FAB is hidden on the empty state, where the EmptyState offers its own affordance.
        if (uiState !is HomeUiState.Empty) {
            ExtendedFloatingActionButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                onClick = onScanClick,
                icon = {
                    Icon(
                        painter = painterResource(R.drawable.rounded_scanner_24),
                        contentDescription = null,
                    )
                },
                text = { Text(text = "Scan") },
            )
        }
    }
}

@Composable
private fun RecentScanList(
    scans: List<ImageOCR>,
    onScanItemClick: (String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(items = scans, key = { it.imagePath + it.timeStamp }) { scan ->
            RecentScanItem(scan = scan, onClick = { onScanItemClick(scan.imagePath) })
        }
    }
}

@Composable
private fun RecentScanItem(
    scan: ImageOCR,
    onClick: () -> Unit,
) {
    val context = LocalContext.current
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
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
                    text = plainSnippet(scan.text),
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

/** hOCR output is HTML; strip tags for the list snippet. */
private fun plainSnippet(html: String): String =
    HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY).toString().trim()

@Preview(showSystemUi = true, showBackground = true, device = "spec:parent=pixel_6")
@Composable
private fun HomeScreenContentPreview() {
    VizhiTamilTheme {
        HomeScreen(
            uiState = HomeUiState.Content(
                scans = listOf(
                    ImageOCR("இது ஒரு எடுத்துக்காட்டு உரை", 92, "2 hours ago", "/tmp/a.jpg"),
                    ImageOCR("மற்றொரு ஸ்கேன்", 68, "yesterday", "/tmp/b.jpg"),
                ),
            ),
            onScanClick = {},
            onScanItemClick = {},
        )
    }
}
