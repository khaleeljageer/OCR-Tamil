package com.jskaleel.vizhi_tamil.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jskaleel.vizhi_tamil.data.repository.SettingsRepository
import com.jskaleel.vizhi_tamil.domain.model.AppSettings
import com.jskaleel.vizhi_tamil.domain.model.ExportFormat
import com.jskaleel.vizhi_tamil.domain.model.OcrLanguage
import com.jskaleel.vizhi_tamil.domain.model.PageSegmentation
import com.jskaleel.vizhi_tamil.domain.model.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    val uiState: StateFlow<AppSettings> = settingsRepository.settings
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = AppSettings(),
        )

    fun onThemeChange(mode: ThemeMode) = update { settingsRepository.setThemeMode(mode) }

    fun onLanguageChange(language: OcrLanguage) =
        update { settingsRepository.setOcrLanguage(language) }

    fun onPageSegChange(mode: PageSegmentation) =
        update { settingsRepository.setPageSegmentation(mode) }

    fun onExportFormatChange(format: ExportFormat) =
        update { settingsRepository.setExportFormat(format) }

    private inline fun update(crossinline block: suspend () -> Unit) {
        viewModelScope.launch { block() }
    }

    companion object {
        private const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
