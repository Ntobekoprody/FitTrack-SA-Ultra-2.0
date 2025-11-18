package com.fittracksa.app.data.preferences

data class UserSettings(
    val isDarkMode: Boolean = true,
    val language: Language = Language.ENGLISH,
    val notificationsEnabled: Boolean = true,
    val displayName: String = DEFAULT_DISPLAY_NAME,
    val profileImageUri: String? = null
) {
    enum class Language { ENGLISH, ISIZULU }

    companion object {
        const val DEFAULT_DISPLAY_NAME = "FitTrack Athlete"
    }
}
