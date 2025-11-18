package com.fittracksa.app.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import java.io.File

private const val DATASTORE_NAME = "fittrack_settings"

class SettingsRepositoryImpl(private val context: Context) : SettingsRepository {
    private val dataStore: DataStore<Preferences> = PreferenceDataStoreFactory.create(
        scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    ) {
        File(context.filesDir, "datastore/$DATASTORE_NAME.preferences_pb")
    }

    private object Keys {
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val LANGUAGE = stringPreferencesKey("language")
        val NOTIFICATIONS = booleanPreferencesKey("notifications")
        val DISPLAY_NAME = stringPreferencesKey("display_name")
        val PROFILE_IMAGE = stringPreferencesKey("profile_image")
        val EMAIL = stringPreferencesKey("email")
    }

    override val settings: Flow<UserSettings> = dataStore.data.map { prefs ->
        UserSettings(
            isDarkMode = prefs[Keys.DARK_MODE] ?: true,
            language = prefs[Keys.LANGUAGE]?.let { stored ->
                runCatching { UserSettings.Language.valueOf(stored) }
                    .getOrDefault(UserSettings.Language.ENGLISH)
            } ?: UserSettings.Language.ENGLISH,
            notificationsEnabled = prefs[Keys.NOTIFICATIONS] ?: true,
            displayName = prefs[Keys.DISPLAY_NAME] ?: UserSettings.DEFAULT_DISPLAY_NAME,
            profileImageUri = prefs[Keys.PROFILE_IMAGE],
            email = prefs[Keys.EMAIL] ?: UserSettings.DEFAULT_EMAIL
        )
    }

    override suspend fun toggleDarkMode() {
        dataStore.edit { prefs ->
            val current = prefs[Keys.DARK_MODE] ?: true
            prefs[Keys.DARK_MODE] = !current
        }
    }

    override suspend fun setLanguage(language: UserSettings.Language) {
        dataStore.edit { prefs ->
            prefs[Keys.LANGUAGE] = language.name
        }
    }

    override suspend fun setNotifications(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[Keys.NOTIFICATIONS] = enabled
        }
    }

    override suspend fun setDisplayName(name: String) {
        dataStore.edit { prefs ->
            val value = name.ifBlank { UserSettings.DEFAULT_DISPLAY_NAME }
            prefs[Keys.DISPLAY_NAME] = value
        }
    }

    override suspend fun setEmail(email: String) {
        dataStore.edit { prefs ->
            val sanitized = email.trim().ifBlank { UserSettings.DEFAULT_EMAIL }
            prefs[Keys.EMAIL] = sanitized
        }
    }

    override suspend fun setProfileImage(uri: String?) {
        dataStore.edit { prefs ->
            if (uri.isNullOrBlank()) {
                prefs -= Keys.PROFILE_IMAGE
            } else {
                prefs[Keys.PROFILE_IMAGE] = uri
            }
        }
    }

    override suspend fun areNotificationsEnabled(): Boolean {
        val prefs = dataStore.data.first()
        return prefs[Keys.NOTIFICATIONS] ?: true
    }
}
