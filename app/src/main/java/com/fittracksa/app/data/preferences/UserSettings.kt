package com.fittracksa.app.data.preferences

data class UserSettings(
    val isDarkMode: Boolean = true,
    val language: Language = Language.ENGLISH,
    val notificationsEnabled: Boolean = true
) {
    enum class Language { ENGLISH, ISIZULU }
}
