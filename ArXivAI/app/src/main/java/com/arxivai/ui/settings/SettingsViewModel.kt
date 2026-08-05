package com.arxivai.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.arxivai.data.local.AppDatabase
import com.arxivai.data.remote.ArXivApiService
import com.arxivai.data.repository.PaperRepository
import com.arxivai.data.repository.SettingsRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SettingsUiState(
    val themeMode: String = "system",
    val defaultCategory: String = "cs.AI",
    val autoSummarize: Boolean = true,
    val aiSummaryEnabled: Boolean = true,
    val openLinksInApp: Boolean = true,
    val maxResults: Int = 50
)

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsRepo = SettingsRepository(application)

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepo.themeMode.collect { _uiState.update { s -> s.copy(themeMode = it) } }
        }
        viewModelScope.launch {
            settingsRepo.defaultCategory.collect { _uiState.update { s -> s.copy(defaultCategory = it) } }
        }
        viewModelScope.launch {
            settingsRepo.autoSummarize.collect { _uiState.update { s -> s.copy(autoSummarize = it) } }
        }
        viewModelScope.launch {
            settingsRepo.aiSummaryEnabled.collect { _uiState.update { s -> s.copy(aiSummaryEnabled = it) } }
        }
        viewModelScope.launch {
            settingsRepo.openLinksInApp.collect { _uiState.update { s -> s.copy(openLinksInApp = it) } }
        }
        viewModelScope.launch {
            settingsRepo.maxResults.collect { _uiState.update { s -> s.copy(maxResults = it) } }
        }
    }

    fun setThemeMode(mode: String) {
        viewModelScope.launch { settingsRepo.setThemeMode(mode) }
    }

    fun setDefaultCategory(category: String) {
        viewModelScope.launch { settingsRepo.setDefaultCategory(category) }
    }

    fun setAutoSummarize(enabled: Boolean) {
        viewModelScope.launch { settingsRepo.setAutoSummarize(enabled) }
    }

    fun setAiSummaryEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepo.setAiSummaryEnabled(enabled) }
    }

    fun setOpenLinksInApp(enabled: Boolean) {
        viewModelScope.launch { settingsRepo.setOpenLinksInApp(enabled) }
    }

    fun setMaxResults(max: Int) {
        viewModelScope.launch { settingsRepo.setMaxResults(max) }
    }
}