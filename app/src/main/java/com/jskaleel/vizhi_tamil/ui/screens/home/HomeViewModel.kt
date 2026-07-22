package com.jskaleel.vizhi_tamil.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jskaleel.vizhi_tamil.domain.model.ImageOCR
import com.jskaleel.vizhi_tamil.domain.usecase.OCRUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val ocrUseCase: OCRUseCase,
) : ViewModel() {

    private val searchQuery = MutableStateFlow("")
    private val selectedIds = MutableStateFlow<Set<Int>>(emptySet())

    // null until the first DB emission, so we can show Loading before Empty.
    private val allScans: StateFlow<List<ImageOCR>?> = ocrUseCase.getRecentScans()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = null,
        )

    val uiState: StateFlow<HomeUiState> = combine(
        allScans,
        searchQuery,
        selectedIds,
    ) { scans, query, selected ->
        if (scans == null) {
            HomeUiState.Loading
        } else if (scans.isEmpty()) {
            HomeUiState.Empty
        } else {
            // A row can be deleted while selected; keep selection in sync with data.
            val liveIds = scans.mapTo(mutableSetOf()) { it.id }
            HomeUiState.Content(
                scans = scans.filter { it.matches(query) },
                query = query,
                selectedIds = selected.intersect(liveIds),
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = HomeUiState.Loading,
    )

    fun onSearchChange(query: String) {
        searchQuery.value = query
    }

    fun onClearSearch() {
        searchQuery.value = ""
    }

    fun onToggleSelection(id: Int) {
        selectedIds.update { current ->
            if (id in current) current - id else current + id
        }
    }

    fun onClearSelection() {
        selectedIds.value = emptySet()
    }

    fun onDeleteSelected() {
        val ids = selectedIds.value
        if (ids.isEmpty()) return
        deleteWhere { it.id in ids }
        selectedIds.value = emptySet()
    }

    fun onDeleteScan(scan: ImageOCR) {
        deleteWhere { it.id == scan.id }
    }

    private fun deleteWhere(predicate: (ImageOCR) -> Boolean) {
        val toDelete = allScans.value.orEmpty().filter(predicate)
        if (toDelete.isEmpty()) return
        viewModelScope.launch { ocrUseCase.deleteScans(toDelete) }
    }

    private fun ImageOCR.matches(query: String): Boolean =
        query.isBlank() || text.contains(query.trim(), ignoreCase = true)

    companion object {
        private const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data object Empty : HomeUiState
    data class Content(
        val scans: List<ImageOCR>,
        val query: String = "",
        val selectedIds: Set<Int> = emptySet(),
    ) : HomeUiState {
        val inSelectionMode: Boolean get() = selectedIds.isNotEmpty()
    }
}
