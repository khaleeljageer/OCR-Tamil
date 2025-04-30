package com.jskaleel.vizhi_tamil.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme1 = lightColorScheme(
    primary = RustRed,
    onPrimary = OnRustRed,
    background = Cream,
    onBackground = OnCream,
    surface = Cream,
    onSurface = OnCream,
    secondary = RustRed,
    onSecondary = OnRustRed
)

@Composable
fun VizhiTamilTheme(
    content: @Composable () -> Unit
) {

    MaterialTheme(
        colorScheme = LightColorScheme1, typography = Typography, content = content
    )
}