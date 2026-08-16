package com.example.codebox.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.codebox.domain.TextCaseStyle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    val textCaseStyle: Flow<TextCaseStyle> = dataStore.data.map { prefs ->
        val name = prefs[STYLE_KEY] ?: TextCaseStyle.NORMAL.name
        TextCaseStyle.valueOf(name)
    }

    suspend fun setTextCaseStyle(style: TextCaseStyle) {
        dataStore.edit { it[STYLE_KEY] = style.name }
    }

    companion object {
        private val STYLE_KEY = stringPreferencesKey("text_case_style")
    }
}