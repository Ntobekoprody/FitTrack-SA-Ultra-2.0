package com.fittracksa.app.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val DATASTORE_NAME = "fittrack_settings"

private val Context.dataStore by preferencesDataStore(name = DATASTORE_NAME)

class SettingsRepositoryImpl(private val context: Context) : SettingsRepository {
    private object Keys {
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val LANGUAGE = stringPreferencesKey("language")
        val NOTIFICATIONS = booleanPreferencesKey("notifications")
    }

    override val settings: Flow<UserSettings> = context.dataStore.data.map { prefs ->
        UserSettings(
            isDarkMode = prefs[Keys.DARK_MODE] ?: true,
            language = prefs[Keys.LANGUAGE]?.let { stored ->
                runCatching { UserSettings.Language.valueOf(stored) }
                    .getOrDefault(UserSettings.Language.ENGLISH)
            } ?: UserSettings.Language.ENGLISH,
            notificationsEnabled = prefs[Keys.NOTIFICATIONS] ?: true
        )
    }

    override suspend fun toggleDarkMode() {
        context.dataStore.edit { prefs ->
            val current = prefs[Keys.DARK_MODE] ?: true
            prefs[Keys.DARK_MODE] = !current
        }
    }

    override suspend fun setLanguage(language: UserSettings.Language) {
        context.dataStore.edit { prefs ->
            prefs[Keys.LANGUAGE] = language.name
        }
    }

    override suspend fun setNotifications(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.NOTIFICATIONS] = enabled
        }
    }
}
