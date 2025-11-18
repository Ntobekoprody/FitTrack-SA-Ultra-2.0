package com.fittracksa.app.ui.screens.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.fittracksa.app.SettingsViewModel
import com.fittracksa.app.data.preferences.UserSettings
import com.fittracksa.app.services.CredentialStore
import com.fittracksa.app.ui.AppStrings
import com.fittracksa.app.ui.auth.AuthViewModel
import com.fittracksa.app.ui.screens.common.FitButton
import com.fittracksa.app.ui.screens.common.FitSecondaryButton
import com.fittracksa.app.ui.theme.Black
import com.fittracksa.app.ui.theme.Lime
import com.fittracksa.app.ui.theme.White
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    strings: AppStrings,
    isDarkMode: Boolean,
    settingsViewModel: SettingsViewModel,
    onSignOut: () -> Unit
) {
    val context = LocalContext.current
    val settings by settingsViewModel.settings.collectAsState()
    val background = if (isDarkMode) Black else White
    val textColor = if (isDarkMode) Lime else androidx.compose.ui.graphics.Color.Black
    val cardColor = if (isDarkMode) Black else White
    val coroutineScope = rememberCoroutineScope()

    val authViewModel: AuthViewModel = androidx.lifecycle.viewmodel.compose.viewModel()

    var displayNameInput by remember(settings.displayName) { mutableStateOf(settings.displayName) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri -> settingsViewModel.setProfileImage(uri?.toString()) }

    // DIALOG STATE for enabling biometrics
    var showBiometricDialog by remember { mutableStateOf(false) }

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

        // PROFILE SECTION -----------------------------------------------------
        item {
            Text(strings.profileSectionTitle, fontWeight = FontWeight.SemiBold, color = textColor)
        }

        item {
            ProfileCard(
                nameInput = displayNameInput,
                onNameChange = { displayNameInput = it },
                onSaveProfile = {
                    coroutineScope.launch {
                        authViewModel.updateUsername(
                            newName = displayNameInput,
                            onSuccess = { settingsViewModel.setDisplayName(displayNameInput) },
                            onFailure = {}
                        )
                    }
                },
                onChangePhoto = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                onRemovePhoto = { settingsViewModel.setProfileImage(null) },
                profileImageUri = settings.profileImageUri,
                strings = strings,
                textColor = textColor,
                isDarkMode = isDarkMode,
                cardColor = cardColor
            )
        }

        // PREFERENCES ---------------------------------------------------------
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
                    val next = if (settings.language == UserSettings.Language.ENGLISH)
                        UserSettings.Language.ISIZULU else UserSettings.Language.ENGLISH
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

        // *** BIOMETRIC LOGIN TOGGLE ***
        item {
            SettingsToggleRow(
                icon = Icons.Rounded.Fingerprint,
                title = "Biometric Login",
                description = "Use fingerprint / face ID to sign in",
                isChecked = CredentialStore.isBiometricEnabled(context),
                onToggle = {
                    if (CredentialStore.isBiometricEnabled(context)) {
                        // Turning OFF
                        CredentialStore.clearCredentials(context)
                        CredentialStore.setBiometricEnabled(context, false)
                    } else {
                        // Turning ON → show dialog
                        showBiometricDialog = true
                    }
                },
                isDarkMode = isDarkMode
            )
        }

        // DATA & PRIVACY ------------------------------------------------------
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
                icon = Icons.Rounded.Shield,
                title = "Privacy",
                description = "Your data is stored locally and synced securely",
                isDarkMode = isDarkMode
            )
        }

        item {
            FitSecondaryButton(label = strings.signOut, leadingIcon = Icons.Rounded.Logout) {
                authViewModel.logout()
                onSignOut()
            }
        }
    }

    // ---------------------- BIOMETRIC CONFIRMATION DIALOG ----------------------
    if (showBiometricDialog) {
        AlertDialog(
            onDismissRequest = { showBiometricDialog = false },
            title = { Text("Enable Biometric Login?") },
            text = { Text("This will allow signing in using your device’s biometrics.") },
            confirmButton = {
                TextButton(onClick = {
                    showBiometricDialog = false

                    // Save credentials (if available)
                    val email = authViewModel.state.value.email
                    val pass = authViewModel.state.value.password

                    if (email.isNotBlank() && pass.isNotBlank()) {
                        CredentialStore.saveCredentials(context, email, pass)
                    }

                    CredentialStore.setBiometricEnabled(context, true)
                }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBiometricDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
