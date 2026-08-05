package com.arxivai.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey val paperId: String,
    val title: String,
    val authors: String,
    val categories: String,
    val addedAt: Long = System.currentTimeMillis(),
    val notes: String = ""
)