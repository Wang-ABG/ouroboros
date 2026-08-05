package com.arxivai.ui.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.arxivai.data.local.AppDatabase
import com.arxivai.data.local.entity.PaperEntity
import com.arxivai.data.remote.ArXivApiService
import com.arxivai.data.repository.PaperRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SearchUiState(
    val query: String = "",
    val results: List<PaperEntity> = emptyList(),
    val localResults: List<PaperEntity> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val hasSearched: Boolean = false
)

class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val repository = PaperRepository(
        ArXivApiService.create(),
        db.paperDao(),
        db.bookmarkDao(),
        db.historyDao()
    )

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    fun onQueryChanged(query: String) {
        _uiState.update { it.copy(query = query) }

        // Debounce search
        searchJob?.cancel()
        if (query.length >= 2) {
            searchJob = viewModelScope.launch {
                delay(300) // debounce
                searchLocal(query)
                searchArXiv(query)
            }
        } else {
            _uiState.update { it.copy(results = emptyList(), localResults = emptyList(), hasSearched = false) }
        }
    }

    private fun searchLocal(query: String) {
        viewModelScope.launch {
            repository.searchPapers(query).collect { results ->
                _uiState.update { it.copy(localResults = results) }
            }
        }
    }

    private suspend fun searchArXiv(query: String) {
        _uiState.update { it.copy(isLoading = true, error = null) }
        val result = repository.searchArXiv(query)
        result.fold(
            onSuccess = { papers ->
                _uiState.update {
                    it.copy(
                        results = papers,
                        isLoading = false,
                        hasSearched = true
                    )
                }
            },
            onFailure = { e ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message,
                        hasSearched = true
                    )
                }
            }
        )
    }

    fun toggleBookmark(paper: PaperEntity) {
        viewModelScope.launch {
            if (paper.isBookmarked) {
                repository.removeBookmark(paper.id)
            } else {
                repository.addBookmark(paper.id, paper.title, paper.authors, paper.categories)
            }
        }
    }
}