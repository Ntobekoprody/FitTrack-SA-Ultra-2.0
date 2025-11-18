package com.fittracksa.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fittracksa.app.data.AppContainer
import com.fittracksa.app.data.preferences.SettingsRepository
import com.fittracksa.app.data.preferences.UserSettings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val settings = settingsRepository.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UserSettings())

    fun toggleDarkMode() {
        viewModelScope.launch { settingsRepository.toggleDarkMode() }
    }

    fun setLanguage(language: UserSettings.Language) {
        viewModelScope.launch { settingsRepository.setLanguage(language) }
    }

    fun setNotifications(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setNotifications(enabled) }
    }

    fun setDisplayName(name: String) {
        viewModelScope.launch { settingsRepository.setDisplayName(name) }
    }

    fun setProfileImage(uri: String?) {
        viewModelScope.launch { settingsRepository.setProfileImage(uri) }
    }

    companion object {
        fun factory(container: AppContainer): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val repo = container.settingsRepository
                    @Suppress("UNCHECKED_CAST")
                    return SettingsViewModel(repo) as T
                }
            }
    }
}
