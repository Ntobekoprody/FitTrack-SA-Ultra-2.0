package com.fittracksa.app.data

import com.fittracksa.app.data.local.FitTrackDatabase
import com.fittracksa.app.data.preferences.SettingsRepository
import com.fittracksa.app.domain.FitTrackRepository
import com.fittracksa.app.notifications.FitTrackNotifier

interface AppContainer {
    val repository: FitTrackRepository
    val settingsRepository: SettingsRepository
    val notifier: FitTrackNotifier
}
