package com.jskaleel.vizhi_tamil.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jskaleel.vizhi_tamil.R
import com.jskaleel.vizhi_tamil.domain.model.AppSettings
import com.jskaleel.vizhi_tamil.domain.model.ExportFormat
import com.jskaleel.vizhi_tamil.domain.model.OcrLanguage
import com.jskaleel.vizhi_tamil.domain.model.PageSegmentation
import com.jskaleel.vizhi_tamil.domain.model.ThemeMode

@Composable
fun SettingsScreenRoute(
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val settings by viewModel.uiState.collectAsStateWithLifecycle()
    SettingsScreen(
        settings = settings,
        onThemeChange = viewModel::onThemeChange,
        onLanguageChange = viewModel::onLanguageChange,
        onPageSegChange = viewModel::onPageSegChange,
        onExportFormatChange = viewModel::onExportFormatChange,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settings: AppSettings,
    onThemeChange: (ThemeMode) -> Unit,
    onLanguageChange: (OcrLanguage) -> Unit,
    onPageSegChange: (PageSegmentation) -> Unit,
    onExportFormatChange: (ExportFormat) -> Unit,
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.settings_title)) }) },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState()),
        ) {
            SettingSection(
                title = stringResource(R.string.settings_theme),
                options = ThemeMode.entries,
                selected = settings.themeMode,
                label = { it.label() },
                onSelect = onThemeChange,
            )
            HorizontalDivider()
            SettingSection(
                title = stringResource(R.string.settings_language),
                options = OcrLanguage.entries,
                selected = settings.ocrLanguage,
                label = { it.label() },
                onSelect = onLanguageChange,
            )
            HorizontalDivider()
            SettingSection(
                title = stringResource(R.string.settings_psm),
                options = PageSegmentation.entries,
                selected = settings.pageSegMode,
                label = { it.label() },
                onSelect = onPageSegChange,
            )
            HorizontalDivider()
            SettingSection(
                title = stringResource(R.string.settings_export),
                options = ExportFormat.entries,
                selected = settings.exportFormat,
                label = { it.label() },
                onSelect = onExportFormatChange,
            )
        }
    }
}

@Composable
private fun <T> SettingSection(
    title: String,
    options: List<T>,
    selected: T,
    label: @Composable (T) -> String,
    onSelect: (T) -> Unit,
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )
        options.forEach { option ->
            OptionRow(
                text = label(option),
                selected = option == selected,
                onClick = { onSelect(option) },
            )
        }
    }
}

@Composable
private fun OptionRow(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected = selected, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(selected = selected, onClick = null)
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 12.dp),
        )
    }
}

@Composable
private fun ThemeMode.label(): String = stringResource(
    when (this) {
        ThemeMode.SYSTEM -> R.string.settings_theme_system
        ThemeMode.LIGHT -> R.string.settings_theme_light
        ThemeMode.DARK -> R.string.settings_theme_dark
    },
)

@Composable
private fun OcrLanguage.label(): String = stringResource(
    when (this) {
        OcrLanguage.TAMIL -> R.string.settings_language_tamil
        OcrLanguage.ENGLISH -> R.string.settings_language_english
        OcrLanguage.TAMIL_ENGLISH -> R.string.settings_language_both
    },
)

@Composable
private fun PageSegmentation.label(): String = stringResource(
    when (this) {
        PageSegmentation.AUTO_OSD -> R.string.settings_psm_auto_osd
        PageSegmentation.AUTO -> R.string.settings_psm_auto
        PageSegmentation.SINGLE_BLOCK -> R.string.settings_psm_single_block
        PageSegmentation.SINGLE_LINE -> R.string.settings_psm_single_line
    },
)

@Composable
private fun ExportFormat.label(): String = stringResource(
    when (this) {
        ExportFormat.TXT -> R.string.settings_export_txt
        ExportFormat.PDF -> R.string.settings_export_pdf
    },
)
