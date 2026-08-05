package com.arxivai.data.local.dao

import androidx.room.*
import com.arxivai.data.local.entity.HistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Query("SELECT * FROM reading_history ORDER BY viewedAt DESC")
    fun getAllHistory(): Flow<List<HistoryEntity>>

    @Query("SELECT * FROM reading_history WHERE paperId = :paperId")
    suspend fun getHistoryEntry(paperId: String): HistoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToHistory(entry: HistoryEntity)

    @Query("DELETE FROM reading_history WHERE paperId = :paperId")
    suspend fun removeFromHistory(paperId: String)

    @Query("DELETE FROM reading_history")
    suspend fun clearHistory()

    @Query("SELECT COUNT(*) FROM reading_history")
    fun getHistoryCount(): Flow<Int>
}