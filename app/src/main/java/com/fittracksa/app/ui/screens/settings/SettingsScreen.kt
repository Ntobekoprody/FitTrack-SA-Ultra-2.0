package com.fittracksa.app.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fittracksa.app.SettingsViewModel
import com.fittracksa.app.data.preferences.UserSettings
import com.fittracksa.app.ui.AppStrings
import com.fittracksa.app.ui.screens.common.FitSecondaryButton
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
    val background = if (isDarkMode) Black else White
    val textColor = if (isDarkMode) Lime else Black
    val cardColor = if (isDarkMode) Black else White

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Text(strings.settings, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = textColor)
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Rounded.Person, contentDescription = null, tint = Lime)
                    Column {
                        Text("John Doe", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = textColor)
                        Text("john@example.com", color = textColor.copy(alpha = 0.7f), fontSize = 14.sp)
                    }
                }
            }
        }

        item {
            Text("Preferences", fontWeight = FontWeight.SemiBold, color = textColor)
        }

        item {
            SettingsToggleRow(
                icon = Icons.Rounded.DarkMode,
                title = strings.darkMode,
                description = "Black & Lime theme",
                isChecked = settings.isDarkMode,
                onToggle = { settingsViewModel.toggleDarkMode() },
                isDarkMode = isDarkMode
            )
        }

        item {
            SettingsToggleRow(
                icon = Icons.Rounded.Language,
                title = strings.languageToggle,
                description = if (settings.language == UserSettings.Language.ENGLISH) "English" else "isiZulu",
                isChecked = settings.language == UserSettings.Language.ISIZULU,
                onToggle = {
                    val next = if (settings.language == UserSettings.Language.ENGLISH) {
                        UserSettings.Language.ISIZULU
                    } else {
                        UserSettings.Language.ENGLISH
                    }
                    settingsViewModel.setLanguage(next)
                },
                isDarkMode = isDarkMode
            )
        }

        item {
            SettingsToggleRow(
                icon = Icons.Rounded.Notifications,
                title = strings.notifications,
                description = "Reminders and updates",
                isChecked = settings.notificationsEnabled,
                onToggle = { settingsViewModel.setNotifications(!settings.notificationsEnabled) },
                isDarkMode = isDarkMode
            )
        }

        item {
            Text("Data & Privacy", fontWeight = FontWeight.SemiBold, color = textColor)
        }

        item {
            SettingsLinkRow(
                icon = Icons.Rounded.Storage,
                title = strings.manageData,
                description = "Sync settings and offline storage",
                isDarkMode = isDarkMode
            )
        }

        item {
            SettingsLinkRow(
                icon = Icons.Rounded.Notifications,
                title = "Privacy",
                description = "Your data is stored locally and synced securely",
                isDarkMode = isDarkMode
            )
        }

        item {
            FitSecondaryButton(label = strings.signOut, leadingIcon = Icons.Rounded.Logout) { onSignOut() }
        }
    }
}

@Composable
private fun SettingsToggleRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    isChecked: Boolean,
    onToggle: () -> Unit,
    isDarkMode: Boolean
) {
    val textColor = if (isDarkMode) Lime else Black
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = Lime)
            Column {
                Text(title, fontWeight = FontWeight.SemiBold, color = textColor)
                Text(description, color = textColor.copy(alpha = 0.7f), fontSize = 12.sp)
            }
        }
        Switch(
            checked = isChecked,
            onCheckedChange = { onToggle() },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Black,
                checkedTrackColor = Lime,
                uncheckedThumbColor = White,
                uncheckedTrackColor = Black.copy(alpha = 0.1f)
            )
        )
    }
}

@Composable
private fun SettingsLinkRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    isDarkMode: Boolean
) {
    val textColor = if (isDarkMode) Lime else Black
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(icon, contentDescription = null, tint = Lime)
        Column(Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.SemiBold, color = textColor)
            Text(description, color = textColor.copy(alpha = 0.7f), fontSize = 12.sp)
        }
    }
}
