package com.arxivai.data.repository

import com.arxivai.data.local.dao.BookmarkDao
import com.arxivai.data.local.dao.HistoryDao
import com.arxivai.data.local.dao.PaperDao
import com.arxivai.data.local.entity.BookmarkEntity
import com.arxivai.data.local.entity.HistoryEntity
import com.arxivai.data.local.entity.PaperEntity
import com.arxivai.data.remote.ArXivApiService
import com.arxivai.data.remote.ArXivEntry
import kotlinx.coroutines.flow.Flow

class PaperRepository(
    private val apiService: ArXivApiService,
    private val paperDao: PaperDao,
    private val bookmarkDao: BookmarkDao,
    private val historyDao: HistoryDao
) {
    // --- Paper operations ---

    fun getAllPapers(): Flow<List<PaperEntity>> = paperDao.getAllPapers()

    fun getPapersByCategory(category: String): Flow<List<PaperEntity>> =
        paperDao.getPapersByCategory(category)

    suspend fun getPaperById(id: String): PaperEntity? = paperDao.getPaperById(id)

    fun searchPapers(query: String): Flow<List<PaperEntity>> = paperDao.searchPapers(query)

    // --- Fetch from arXiv API ---

    suspend fun fetchLatestPapers(category: String = "cs.AI", start: Int = 0, maxResults: Int = 50): Result<List<PaperEntity>> {
        return try {
            val searchQuery = when {
                category.isNotEmpty() -> "cat:$category"
                else -> "all:electron"
            }
            val response = apiService.searchPapers(
                searchQuery = searchQuery,
                start = start,
                maxResults = maxResults
            )
            val papers = response.entries?.map { it.toPaperEntity() } ?: emptyList()
            paperDao.upsertPapers(papers)
            Result.success(papers)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchPaperById(arxivId: String): Result<PaperEntity> {
        return try {
            val response = apiService.getPaperById(idList = arxivId)
            val entry = response.entries?.firstOrNull()
                ?: return Result.failure(Exception("Paper not found"))
            val paper = entry.toPaperEntity()
            paperDao.upsertPaper(paper)
            Result.success(paper)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchArXiv(query: String, start: Int = 0, maxResults: Int = 50): Result<List<PaperEntity>> {
        return try {
            val response = apiService.searchPapers(
                searchQuery = "all:$query",
                start = start,
                maxResults = maxResults
            )
            val papers = response.entries?.map { it.toPaperEntity() } ?: emptyList()
            paperDao.upsertPapers(papers)
            Result.success(papers)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- Bookmark operations ---

    fun getAllBookmarks(): Flow<List<BookmarkEntity>> = bookmarkDao.getAllBookmarks()

    suspend fun addBookmark(paperId: String, title: String, authors: String, categories: String) {
        bookmarkDao.addBookmark(
            BookmarkEntity(
                paperId = paperId,
                title = title,
                authors = authors,
                categories = categories
            )
        )
        paperDao.updateBookmarkStatus(paperId, true)
    }

    suspend fun removeBookmark(paperId: String) {
        bookmarkDao.removeBookmarkById(paperId)
        paperDao.updateBookmarkStatus(paperId, false)
    }

    suspend fun isBookmarked(paperId: String): Boolean {
        return bookmarkDao.getBookmark(paperId) != null
    }

    fun getBookmarkCount(): Flow<Int> = bookmarkDao.getBookmarkCount()

    // --- History operations ---

    fun getAllHistory(): Flow<List<HistoryEntity>> = historyDao.getAllHistory()

    suspend fun addToHistory(paperId: String, title: String, authors: String, categories: String) {
        historyDao.addToHistory(
            HistoryEntity(
                paperId = paperId,
                title = title,
                authors = authors,
                categories = categories
            )
        )
        paperDao.updateLastAccessed(paperId)
    }

    suspend fun clearHistory() = historyDao.clearHistory()

    fun getHistoryCount(): Flow<Int> = historyDao.getHistoryCount()

    // --- Utility ---

    suspend fun savePaperSummary(paperId: String, summary: String) {
        val paper = paperDao.getPaperById(paperId) ?: return
        paperDao.upsertPaper(paper.copy(summary = summary))
    }

    private fun ArXivEntry.toPaperEntity(): PaperEntity {
        return PaperEntity(
            id = extractArxivId(),
            title = title.trim(),
            authors = getAuthorsString(),
            abstract = summary.trim(),
            categories = getCategoriesString(),
            publishedDate = published,
            updatedDate = updated,
            pdfUrl = getPdfUrl(),
            comment = comment ?: "",
            cachedAt = System.currentTimeMillis()
        )
    }
}