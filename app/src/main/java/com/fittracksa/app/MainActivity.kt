package com.fittracksa.app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.appcompat.app.AppCompatActivity
import com.fittracksa.app.ui.AppRoot
import com.fittracksa.app.ui.theme.FitTrackTheme

class MainActivity : AppCompatActivity() {

    private val appContainer by lazy { (application as FitTrackApp).container }

    private val dataViewModel: SharedDataViewModel by viewModels {
        SharedDataViewModel.factory(appContainer)
    }

    private val settingsViewModel: SettingsViewModel by viewModels {
        SettingsViewModel.factory(appContainer)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val settingsState by settingsViewModel.settings.collectAsState()
            FitTrackTheme(darkTheme = settingsState.isDarkMode) {
                AppRoot(
                    dataViewModel = dataViewModel,
                    settingsViewModel = settingsViewModel,
                    userSettings = settingsState,
                    notifier = appContainer.notifier
                )
            }
        }
    }
}
