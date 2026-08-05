package com.arxivai.data.local.dao

import androidx.room.*
import com.arxivai.data.local.entity.BookmarkEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmarks ORDER BY addedAt DESC")
    fun getAllBookmarks(): Flow<List<BookmarkEntity>>

    @Query("SELECT * FROM bookmarks WHERE paperId = :paperId")
    suspend fun getBookmark(paperId: String): BookmarkEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addBookmark(bookmark: BookmarkEntity)

    @Delete
    suspend fun removeBookmark(bookmark: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE paperId = :paperId")
    suspend fun removeBookmarkById(paperId: String)

    @Query("SELECT COUNT(*) FROM bookmarks")
    fun getBookmarkCount(): Flow<Int>
}