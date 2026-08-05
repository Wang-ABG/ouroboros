package com.arxivai.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.arxivai.data.local.dao.BookmarkDao
import com.arxivai.data.local.dao.HistoryDao
import com.arxivai.data.local.dao.PaperDao
import com.arxivai.data.local.entity.BookmarkEntity
import com.arxivai.data.local.entity.HistoryEntity
import com.arxivai.data.local.entity.PaperEntity

@Database(
    entities = [PaperEntity::class, BookmarkEntity::class, HistoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun paperDao(): PaperDao
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun historyDao(): HistoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "arxivai_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}