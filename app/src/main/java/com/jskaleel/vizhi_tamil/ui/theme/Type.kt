package com.jskaleel.vizhi_tamil.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.jskaleel.vizhi_tamil.R

// Noto Sans Tamil is a variable font; we expose the weights the type scale uses.
// On API < 26 the variation axis is ignored and the regular master renders — Tamil
// glyph coverage is unaffected, only weight differentiation degrades gracefully.
@OptIn(ExperimentalTextApi::class)
private fun notoSansTamil(weight: FontWeight) = Font(
    resId = R.font.noto_sans_tamil,
    weight = weight,
    style = FontStyle.Normal,
    variationSettings = FontVariation.Settings(FontVariation.weight(weight.weight)),
)

val NotoSansTamil = FontFamily(
    notoSansTamil(FontWeight.Normal),
    notoSansTamil(FontWeight.Medium),
    notoSansTamil(FontWeight.SemiBold),
    notoSansTamil(FontWeight.Bold),
)

// Tamil stacks vowel signs above/below the base glyph, so line heights are set
// generously (~1.4–1.5x) to avoid clipping and keep dense scans readable.
val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = NotoSansTamil,
        fontWeight = FontWeight.Normal,
        fontSize = 57.sp,
        lineHeight = 68.sp,
        letterSpacing = (-0.25).sp,
    ),
    displayMedium = TextStyle(
        fontFamily = NotoSansTamil,
        fontWeight = FontWeight.Normal,
        fontSize = 45.sp,
        lineHeight = 56.sp,
    ),
    displaySmall = TextStyle(
        fontFamily = NotoSansTamil,
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp,
        lineHeight = 46.sp,
    ),
    headlineLarge = TextStyle(
        fontFamily = NotoSansTamil,
        fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp,
        lineHeight = 42.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = NotoSansTamil,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 38.sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = NotoSansTamil,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 34.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = NotoSansTamil,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 30.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = NotoSansTamil,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp,
    ),
    titleSmall = TextStyle(
        fontFamily = NotoSansTamil,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = NotoSansTamil,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.5.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = NotoSansTamil,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.25.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = NotoSansTamil,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.4.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = NotoSansTamil,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = NotoSansTamil,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = NotoSansTamil,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
    ),
)
