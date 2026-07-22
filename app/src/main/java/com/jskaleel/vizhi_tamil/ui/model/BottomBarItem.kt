package com.jskaleel.vizhi_tamil.ui.model

import androidx.compose.ui.graphics.painter.Painter
import com.jskaleel.vizhi_tamil.ui.navigation.AppRoute

data class BottomBarItem(
    val title: String,
    val icon: Painter,
    val route: AppRoute,
)
