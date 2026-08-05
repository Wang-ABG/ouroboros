package com.arxivai.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reading_history")
data class HistoryEntity(
    @PrimaryKey val paperId: String,
    val title: String,
    val authors: String,
    val categories: String,
    val viewedAt: Long = System.currentTimeMillis()
)