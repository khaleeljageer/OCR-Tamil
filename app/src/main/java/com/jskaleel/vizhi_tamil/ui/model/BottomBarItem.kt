package com.jskaleel.vizhi_tamil.ui.model

import androidx.compose.ui.graphics.painter.Painter

data class BottomBarItem(
    val title: String,
    val icon: Painter,
    val route: String
)