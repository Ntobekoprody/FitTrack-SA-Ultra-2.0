package com.fittracksa.app.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fittracksa.app.SettingsViewModel
import com.fittracksa.app.data.preferences.UserSettings
import com.fittracksa.app.ui.AppStrings
import com.fittracksa.app.ui.screens.common.FitButton
import com.fittracksa.app.ui.theme.Black
import com.fittracksa.app.ui.theme.Lime
import com.fittracksa.app.ui.theme.White

@Composable
fun SettingsScreen(
    strings: AppStrings,
    isDarkMode: Boolean,
    settingsViewModel: SettingsViewModel,
    onSignOut: () -> Unit
) {
    val settings by settingsViewModel.settings.collectAsState()
    val surface = if (isDarkMode) Black else White
    val textColor = if (isDarkMode) Lime else Black

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(surface)
            .padding(24.dp)
    ) {
        Text(strings.settings, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = textColor)
        Spacer(modifier = Modifier.height(16.dp))

        ToggleRow(
            label = strings.darkMode,
            labelColor = textColor,
            checked = settings.isDarkMode,
            onCheckedChange = { settingsViewModel.toggleDarkMode() }
        )
        Spacer(modifier = Modifier.height(12.dp))
        ToggleRow(
            label = strings.languageToggle,
            labelColor = textColor,
            checked = settings.language == UserSettings.Language.ISIZULU,
            onCheckedChange = {
                val next = if (settings.language == UserSettings.Language.ENGLISH) {
                    UserSettings.Language.ISIZULU
                } else {
                    UserSettings.Language.ENGLISH
                }
                settingsViewModel.setLanguage(next)
            }
        )
        Spacer(modifier = Modifier.height(12.dp))
        ToggleRow(
            label = strings.notifications,
            labelColor = textColor,
            checked = settings.notificationsEnabled,
            onCheckedChange = { settingsViewModel.setNotifications(!settings.notificationsEnabled) }
        )
        Spacer(modifier = Modifier.height(24.dp))
        FitButton(label = "${strings.manageData} → Review RoomDB cache & sync queue") {}
        Spacer(modifier = Modifier.height(12.dp))
        FitButton(label = "${strings.signOut} → Back to Login") { onSignOut() }
    }
}

@Composable
private fun ToggleRow(
    label: String,
    labelColor: Color = Black,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Column(Modifier.fillMaxWidth()) {
        Text(text = label, color = labelColor, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(4.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Lime,
                checkedTrackColor = Black,
                uncheckedThumbColor = Black,
                uncheckedTrackColor = Lime
            )
        )
    }
}
