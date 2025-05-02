package com.jskaleel.vizhi_tamil.ui.screens.imageDetail

import android.text.Spanned
import android.widget.TextView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.viewinterop.AndroidView


@Composable
fun ImageOCRDetailScreen(text: Spanned, accuracy: String) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = accuracy, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End)
        HtmlText(text = text)
    }
}

@Composable
fun HtmlText(text: Spanned) {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { TextView(it) },
        update = { it.text = text }
    )
}