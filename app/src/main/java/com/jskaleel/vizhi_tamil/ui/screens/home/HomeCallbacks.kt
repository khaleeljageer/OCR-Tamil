package com.jskaleel.vizhi_tamil.ui.screens.home

import com.jskaleel.vizhi_tamil.domain.model.ImageOCR

/** Bundles the Home screen's event handlers so the composable signature stays flat. */
data class HomeCallbacks(
    val onScanClick: () -> Unit = {},
    val onScanItemClick: (Int) -> Unit = {},
    val onSearchChange: (String) -> Unit = {},
    val onClearSearch: () -> Unit = {},
    val onToggleSelection: (Int) -> Unit = {},
    val onClearSelection: () -> Unit = {},
    val onDeleteSelected: () -> Unit = {},
    val onDeleteScan: (ImageOCR) -> Unit = {},
)
