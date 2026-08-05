package com.arxivai.ui.feed

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

data class FeedUiState(
    val papers: List<PaperEntity> = emptyList(),
    val selectedCategory: String = "",
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null
)

class FeedViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val repository = PaperRepository(
        ArXivApiService.create(),
        db.paperDao(),
        db.bookmarkDao(),
        db.historyDao()
    )
    private val settingsRepo = SettingsRepository(application)
    private val summarizer = Summarizer()

    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    init {
        // Observe papers from local DB
        viewModelScope.launch {
            repository.getAllPapers().collect { papers ->
                _uiState.update { it.copy(papers = papers) }
            }
        }

        // Load default category and fetch
        viewModelScope.launch {
            settingsRepo.defaultCategory.collect { category ->
                _uiState.update { it.copy(selectedCategory = category) }
                fetchPapers(category)
            }
        }
    }

    fun selectCategory(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
        viewModelScope.launch {
            if (category.isEmpty()) {
                repository.getAllPapers().collect { papers ->
                    _uiState.update { it.copy(papers = papers) }
                }
            } else {
                repository.getPapersByCategory(category).collect { papers ->
                    _uiState.update { it.copy(papers = papers) }
                }
            }
        }
        fetchPapers(category)
    }

    fun fetchPapers(category: String = _uiState.value.selectedCategory) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = repository.fetchLatestPapers(category = category)
            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false, isRefreshing = false) }
                    // Auto-generate summaries for new papers
                    if (category.isEmpty()) {
                        repository.getAllPapers().first().let { papers ->
                            papers.forEach { paper ->
                                if (paper.summary.isEmpty()) {
                                    generateSummary(paper)
                                }
                            }
                        }
                    }
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            error = e.message ?: "Failed to load papers"
                        )
                    }
                }
            )
        }
    }

    fun refresh() {
        _uiState.update { it.copy(isRefreshing = true) }
        fetchPapers()
    }

    fun toggleBookmark(paper: PaperEntity) {
        viewModelScope.launch {
            if (paper.isBookmarked) {
                repository.removeBookmark(paper.id)
            } else {
                repository.addBookmark(
                    paper.id,
                    paper.title,
                    paper.authors,
                    paper.categories
                )
            }
        }
    }

    private fun generateSummary(paper: PaperEntity) {
        viewModelScope.launch {
            try {
                val summary = summarizer.summarize(paper.abstract)
                repository.savePaperSummary(paper.id, summary)
            } catch (_: Exception) {
                // Silently fail - summary is optional
            }
        }
    }
}