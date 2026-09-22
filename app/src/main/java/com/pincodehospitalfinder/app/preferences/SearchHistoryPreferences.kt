package com.pincodehospitalfinder.app.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.historyDataStore by preferencesDataStore(name = "search_history_prefs")

class SearchHistoryPreferences(private val context: Context) {

    companion object {
        private val HISTORY_KEY = stringPreferencesKey("recent_pincodes")
        private const val MAX_ITEMS = 5
    }

    val recentPinCodes: Flow<List<String>> = context.historyDataStore.data.map { prefs ->
        prefs[HISTORY_KEY]?.split(",")?.filter { it.isNotBlank() } ?: emptyList()
    }

    suspend fun addPinCode(pinCode: String) {
        context.historyDataStore.edit { prefs ->
            val current = prefs[HISTORY_KEY]?.split(",")?.filter { it.isNotBlank() } ?: emptyList()
            val updated = (listOf(pinCode) + current.filter { it != pinCode }).take(MAX_ITEMS)
            prefs[HISTORY_KEY] = updated.joinToString(",")
        }
    }

    suspend fun clearHistory() {
        context.historyDataStore.edit { prefs ->
            prefs[HISTORY_KEY] = ""
        }
    }
}
