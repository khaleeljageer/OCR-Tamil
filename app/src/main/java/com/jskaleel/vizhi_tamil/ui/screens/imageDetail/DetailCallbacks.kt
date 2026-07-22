package com.jskaleel.vizhi_tamil.ui.screens.imageDetail

/** Bundles the detail screen's event handlers to keep the composable signature flat. */
data class DetailCallbacks(
    val onBack: () -> Unit = {},
    val onStartEdit: () -> Unit = {},
    val onCancelEdit: () -> Unit = {},
    val onSave: (String) -> Unit = {},
    val onDelete: () -> Unit = {},
)
