package com.arxivai

import android.app.Application
import com.arxivai.data.local.AppDatabase

class ArXivAIApp : Application() {

    lateinit var database: AppDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.getInstance(this)
    }
}