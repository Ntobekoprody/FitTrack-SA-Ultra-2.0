package com.fittracksa.app.data.preferences

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val settings: Flow<UserSettings>
    suspend fun toggleDarkMode()
    suspend fun setLanguage(language: UserSettings.Language)
    suspend fun setNotifications(enabled: Boolean)
    suspend fun setDisplayName(name: String)
    suspend fun setProfileImage(uri: String?)
    suspend fun areNotificationsEnabled(): Boolean
}
