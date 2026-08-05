package com.arxivai.ui.bookmarks

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.arxivai.data.local.AppDatabase
import com.arxivai.data.local.entity.BookmarkEntity
import com.arxivai.data.remote.ArXivApiService
import com.arxivai.data.repository.PaperRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class BookmarksUiState(
    val bookmarks: List<BookmarkEntity> = emptyList(),
    val isLoading: Boolean = false
)

class BookmarksViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val repository = PaperRepository(
        ArXivApiService.create(),
        db.paperDao(),
        db.bookmarkDao(),
        db.historyDao()
    )

    private val _uiState = MutableStateFlow(BookmarksUiState())
    val uiState: StateFlow<BookmarksUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAllBookmarks().collect { bookmarks ->
                _uiState.update { it.copy(bookmarks = bookmarks, isLoading = false) }
            }
        }
    }

    fun removeBookmark(paperId: String) {
        viewModelScope.launch {
            repository.removeBookmark(paperId)
        }
    }
}