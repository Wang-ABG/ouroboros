package com.arxivai.ui.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.arxivai.ai.Summarizer
import com.arxivai.data.local.AppDatabase
import com.arxivai.data.local.entity.PaperEntity
import com.arxivai.data.remote.ArXivApiService
import com.arxivai.data.repository.PaperRepository
import com.arxivai.data.repository.SettingsRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class DetailUiState(
    val paper: PaperEntity? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isBookmarked: Boolean = false,
    val aiSummary: String = "",
    val keywords: List<String> = emptyList(),
    val isSummarizing: Boolean = false
)

class DetailViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val repository = PaperRepository(
        ArXivApiService.create(),
        db.paperDao(),
        db.bookmarkDao(),
        db.historyDao()
    )
    private val settingsRepo = SettingsRepository(application)
    private val summarizer = Summarizer()

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    fun loadPaper(paperId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            // Try local first
            val localPaper = repository.getPaperById(paperId)
            if (localPaper != null) {
                _uiState.update {
                    it.copy(
                        paper = localPaper,
                        isLoading = false,
                        isBookmarked = localPaper.isBookmarked,
                        aiSummary = localPaper.summary
                    )
                }
                // Add to history
                repository.addToHistory(
                    localPaper.id,
                    localPaper.title,
                    localPaper.authors,
                    localPaper.categories
                )
            }

            // Fetch from API for fresh data
            val result = repository.fetchPaperById(paperId)
            result.fold(
                onSuccess = { paper ->
                    val isBookmarked = repository.isBookmarked(paperId)
                    _uiState.update {
                        it.copy(
                            paper = paper,
                            isLoading = false,
                            isBookmarked = isBookmarked,
                            aiSummary = paper.summary
                        )
                    }
                    // Add to history
                    repository.addToHistory(
                        paper.id, paper.title, paper.authors, paper.categories
                    )

                    // Generate AI summary if not yet done
                    if (paper.summary.isEmpty()) {
                        generateSummary(paper)
                    }
                    // Extract keywords
                    extractKeywords(paper)
                },
                onFailure = { e ->
                    if (localPaper == null) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = e.message ?: "Failed to load paper"
                            )
                        }
                    }
                }
            )
        }
    }

    fun toggleBookmark() {
        val paper = _uiState.value.paper ?: return
        viewModelScope.launch {
            if (_uiState.value.isBookmarked) {
                repository.removeBookmark(paper.id)
                _uiState.update { it.copy(isBookmarked = false) }
            } else {
                repository.addBookmark(
                    paper.id, paper.title, paper.authors, paper.categories
                )
                _uiState.update { it.copy(isBookmarked = true) }
            }
        }
    }

    private fun generateSummary(paper: PaperEntity) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSummarizing = true) }
            try {
                val summary = summarizer.summarize(paper.abstract)
                repository.savePaperSummary(paper.id, summary)
                _uiState.update { it.copy(aiSummary = summary, isSummarizing = false) }
            } catch (_: Exception) {
                _uiState.update { it.copy(isSummarizing = false) }
            }
        }
    }

    private fun extractKeywords(paper: PaperEntity) {
        viewModelScope.launch {
            try {
                val keywords = summarizer.extractKeywords(paper.abstract)
                _uiState.update { it.copy(keywords = keywords) }
            } catch (_: Exception) { }
        }
    }
}