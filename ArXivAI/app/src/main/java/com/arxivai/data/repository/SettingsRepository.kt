package com.arxivai.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {

    companion object {
        private val KEY_THEME_MODE = stringPreferencesKey("theme_mode")
        private val KEY_DEFAULT_CATEGORY = stringPreferencesKey("default_category")
        private val KEY_AUTO_SUMMARIZE = booleanPreferencesKey("auto_summarize")
        private val KEY_MAX_RESULTS = intPreferencesKey("max_results")
        private val KEY_AI_SUMMARY_ENABLED = booleanPreferencesKey("ai_summary_enabled")
        private val KEY_OPEN_LINKS_IN_APP = booleanPreferencesKey("open_links_in_app")
    }

    val themeMode: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_THEME_MODE] ?: "system"
    }

    val defaultCategory: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_DEFAULT_CATEGORY] ?: "cs.AI"
    }

    val autoSummarize: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_AUTO_SUMMARIZE] ?: true
    }

    val maxResults: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[KEY_MAX_RESULTS] ?: 50
    }

    val aiSummaryEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_AI_SUMMARY_ENABLED] ?: true
    }

    val openLinksInApp: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_OPEN_LINKS_IN_APP] ?: true
    }

    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_THEME_MODE] = mode
        }
    }

    suspend fun setDefaultCategory(category: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_DEFAULT_CATEGORY] = category
        }
    }

    suspend fun setAutoSummarize(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_AUTO_SUMMARIZE] = enabled
        }
    }

    suspend fun setMaxResults(max: Int) {
        context.dataStore.edit { prefs ->
            prefs[KEY_MAX_RESULTS] = max
        }
    }

    suspend fun setAiSummaryEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_AI_SUMMARY_ENABLED] = enabled
        }
    }

    suspend fun setOpenLinksInApp(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_OPEN_LINKS_IN_APP] = enabled
        }
    }
}