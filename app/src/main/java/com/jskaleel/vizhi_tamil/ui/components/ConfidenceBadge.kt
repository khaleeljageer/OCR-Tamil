package com.jskaleel.vizhi_tamil.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jskaleel.vizhi_tamil.ui.theme.ConfidenceHigh
import com.jskaleel.vizhi_tamil.ui.theme.ConfidenceLow
import com.jskaleel.vizhi_tamil.ui.theme.ConfidenceMedium
import com.jskaleel.vizhi_tamil.ui.theme.VizhiTamilTheme

private const val HIGH_THRESHOLD = 85
private const val MEDIUM_THRESHOLD = 60

/** Accent colour for an OCR confidence value (0–100). */
fun confidenceColor(accuracy: Int): Color = when {
    accuracy >= HIGH_THRESHOLD -> ConfidenceHigh
    accuracy >= MEDIUM_THRESHOLD -> ConfidenceMedium
    else -> ConfidenceLow
}

/**
 * Compact pill showing OCR confidence, coloured by band (green / amber / red).
 * The tint is composited over the surface so it reads in light and dark themes.
 */
@Composable
fun ConfidenceBadge(
    accuracy: Int,
    modifier: Modifier = Modifier,
) {
    val accent = confidenceColor(accuracy)
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(accent.copy(alpha = 0.16f).compositeOver(MaterialTheme.colorScheme.surface))
            .padding(horizontal = 10.dp, vertical = 4.dp),
    ) {
        Text(
            text = "$accuracy%",
            style = MaterialTheme.typography.labelMedium,
            color = accent,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ConfidenceBadgePreview() {
    VizhiTamilTheme {
        Row(modifier = Modifier.padding(12.dp)) {
            ConfidenceBadge(accuracy = 94, modifier = Modifier.padding(4.dp))
            ConfidenceBadge(accuracy = 72, modifier = Modifier.padding(4.dp))
            ConfidenceBadge(accuracy = 41, modifier = Modifier.padding(4.dp))
        }
    }
}
