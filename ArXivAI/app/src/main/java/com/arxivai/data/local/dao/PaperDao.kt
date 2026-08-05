package com.arxivai.data.local.dao

import androidx.room.*
import com.arxivai.data.local.entity.PaperEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PaperDao {
    @Query("SELECT * FROM papers ORDER BY lastAccessed DESC")
    fun getAllPapers(): Flow<List<PaperEntity>>

    @Query("SELECT * FROM papers WHERE categories LIKE '%' || :category || '%' ORDER BY publishedDate DESC")
    fun getPapersByCategory(category: String): Flow<List<PaperEntity>>

    @Query("SELECT * FROM papers WHERE id = :id")
    suspend fun getPaperById(id: String): PaperEntity?

    @Query("SELECT * FROM papers WHERE title LIKE '%' || :query || '%' OR authors LIKE '%' || :query || '%' OR abstract LIKE '%' || :query || '%'")
    fun searchPapers(query: String): Flow<List<PaperEntity>>

    @Upsert
    suspend fun upsertPaper(paper: PaperEntity)

    @Upsert
    suspend fun upsertPapers(papers: List<PaperEntity>)

    @Query("UPDATE papers SET isBookmarked = :bookmarked WHERE id = :paperId")
    suspend fun updateBookmarkStatus(paperId: String, bookmarked: Boolean)

    @Query("UPDATE papers SET lastAccessed = :timestamp WHERE id = :paperId")
    suspend fun updateLastAccessed(paperId: String, timestamp: Long = System.currentTimeMillis())

    @Query("DELETE FROM papers WHERE id = :paperId")
    suspend fun deletePaper(paperId: String)

    @Query("DELETE FROM papers")
    suspend fun deleteAll()
}