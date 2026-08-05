package com.arxivai.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "papers")
data class PaperEntity(
    @PrimaryKey val id: String,  // arXiv ID like "2301.12345"
    val title: String,
    val authors: String,  // comma-separated
    val abstract: String,
    val summary: String = "",  // AI-generated summary
    val categories: String,  // comma-separated
    val publishedDate: String,
    val updatedDate: String,
    val pdfUrl: String,
    val comment: String = "",
    val isBookmarked: Boolean = false,
    val lastAccessed: Long = System.currentTimeMillis(),
    val cachedAt: Long = System.currentTimeMillis()
)