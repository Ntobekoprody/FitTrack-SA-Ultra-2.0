package com.fittracksa.app.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.remove
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private const val DATASTORE_NAME = "fittrack_settings"

private val Context.dataStore by preferencesDataStore(name = DATASTORE_NAME)

class SettingsRepositoryImpl(private val context: Context) : SettingsRepository {
    private object Keys {
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val LANGUAGE = stringPreferencesKey("language")
        val NOTIFICATIONS = booleanPreferencesKey("notifications")
        val DISPLAY_NAME = stringPreferencesKey("display_name")
        val PROFILE_IMAGE = stringPreferencesKey("profile_image")
    }

    override val settings: Flow<UserSettings> = context.dataStore.data.map { prefs ->
        UserSettings(
            isDarkMode = prefs[Keys.DARK_MODE] ?: true,
            language = prefs[Keys.LANGUAGE]?.let { stored ->
                runCatching { UserSettings.Language.valueOf(stored) }
                    .getOrDefault(UserSettings.Language.ENGLISH)
            } ?: UserSettings.Language.ENGLISH,
            notificationsEnabled = prefs[Keys.NOTIFICATIONS] ?: true,
            displayName = prefs[Keys.DISPLAY_NAME] ?: UserSettings.DEFAULT_DISPLAY_NAME,
            profileImageUri = prefs[Keys.PROFILE_IMAGE]
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

    override suspend fun setDisplayName(name: String) {
        context.dataStore.edit { prefs ->
            val value = name.ifBlank { UserSettings.DEFAULT_DISPLAY_NAME }
            prefs[Keys.DISPLAY_NAME] = value
        }
    }

    override suspend fun setProfileImage(uri: String?) {
        context.dataStore.edit { prefs ->
            if (uri.isNullOrBlank()) {
                prefs.remove(Keys.PROFILE_IMAGE)
            } else {
                prefs[Keys.PROFILE_IMAGE] = uri
            }
        }
    }

    override suspend fun areNotificationsEnabled(): Boolean {
        val prefs = context.dataStore.data.first()
        return prefs[Keys.NOTIFICATIONS] ?: true
    }
}
