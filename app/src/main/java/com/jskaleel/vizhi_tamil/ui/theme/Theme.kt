package com.jskaleel.vizhi_tamil.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val LightColorScheme = lightColorScheme(
    primary = Saffron40,
    secondary = Teal40,
    tertiary = Coral40,
    onPrimary = Color.White,
    primaryContainer = SaffronContainer,
    onPrimaryContainer = DarkGray,
    onSecondary = Color.White,
    secondaryContainer = TealContainer,
    onSecondaryContainer = DarkGray,
    onTertiary = Color.White,
    tertiaryContainer = CoralContainer,
    onTertiaryContainer = DarkGray,
    background = OffWhite,
    onBackground = DarkGray,
    surface = LightCream,
    onSurface = DarkGray,
    surfaceVariant = LightGray,
    onSurfaceVariant = MediumGray,
    error = ErrorRed,
    onError = Color.White,
    errorContainer = LightRed,
    onErrorContainer = ErrorRed
)

@Composable
fun VizhiTamilTheme(
    content: @Composable () -> Unit
) {

    MaterialTheme(
        colorScheme = LightColorScheme, typography = Typography, content = content
    )
}