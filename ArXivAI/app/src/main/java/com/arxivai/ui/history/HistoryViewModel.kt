package com.arxivai.ui.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.arxivai.data.local.AppDatabase
import com.arxivai.data.local.entity.HistoryEntity
import com.arxivai.data.remote.ArXivApiService
import com.arxivai.data.repository.PaperRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class HistoryUiState(
    val history: List<HistoryEntity> = emptyList(),
    val isLoading: Boolean = false
)

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val repository = PaperRepository(
        ArXivApiService.create(),
        db.paperDao(),
        db.bookmarkDao(),
        db.historyDao()
    )

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAllHistory().collect { history ->
                _uiState.update { it.copy(history = history, isLoading = false) }
            }
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }
}