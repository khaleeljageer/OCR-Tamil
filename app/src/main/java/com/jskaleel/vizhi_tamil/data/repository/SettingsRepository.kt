package com.jskaleel.vizhi_tamil.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.jskaleel.vizhi_tamil.domain.model.AppSettings
import com.jskaleel.vizhi_tamil.domain.model.ExportFormat
import com.jskaleel.vizhi_tamil.domain.model.OcrLanguage
import com.jskaleel.vizhi_tamil.domain.model.PageSegmentation
import com.jskaleel.vizhi_tamil.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface SettingsRepository {
    val settings: Flow<AppSettings>
    suspend fun setThemeMode(mode: ThemeMode)
    suspend fun setOcrLanguage(language: OcrLanguage)
    suspend fun setPageSegmentation(mode: PageSegmentation)
    suspend fun setExportFormat(format: ExportFormat)
}

class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : SettingsRepository {

    override val settings: Flow<AppSettings> = dataStore.data.map { prefs ->
        AppSettings(
            themeMode = prefs.readEnum(KEY_THEME, ThemeMode.SYSTEM),
            ocrLanguage = prefs.readEnum(KEY_LANGUAGE, OcrLanguage.TAMIL_ENGLISH),
            pageSegMode = prefs.readEnum(KEY_PSM, PageSegmentation.AUTO_OSD),
            exportFormat = prefs.readEnum(KEY_EXPORT, ExportFormat.TXT),
        )
    }

    override suspend fun setThemeMode(mode: ThemeMode) = putEnum(KEY_THEME, mode)

    override suspend fun setOcrLanguage(language: OcrLanguage) = putEnum(KEY_LANGUAGE, language)

    override suspend fun setPageSegmentation(mode: PageSegmentation) = putEnum(KEY_PSM, mode)

    override suspend fun setExportFormat(format: ExportFormat) = putEnum(KEY_EXPORT, format)

    private suspend fun <T : Enum<T>> putEnum(key: Preferences.Key<String>, value: T) {
        dataStore.edit { it[key] = value.name }
    }

    private inline fun <reified T : Enum<T>> Preferences.readEnum(
        key: Preferences.Key<String>,
        default: T,
    ): T = this[key]?.let { stored ->
        runCatching { enumValueOf<T>(stored) }.getOrDefault(default)
    } ?: default

    companion object {
        private val KEY_THEME = stringPreferencesKey("theme_mode")
        private val KEY_LANGUAGE = stringPreferencesKey("ocr_language")
        private val KEY_PSM = stringPreferencesKey("page_seg_mode")
        private val KEY_EXPORT = stringPreferencesKey("export_format")
    }
}
