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
import androidx.compose.material.icons.automirrored.rounded.Logout
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
import com.fittracksa.app.ui.theme.White
import com.fittracksa.app.ui.theme.Lime
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
    val coroutine = rememberCoroutineScope()

    val textColor = if (isDarkMode) Lime else Black
    val backgroundColor = if (isDarkMode) Black else White
    val cardColor = if (isDarkMode) Black else White

    val authViewModel: AuthViewModel = androidx.lifecycle.viewmodel.compose.viewModel()

    var displayNameInput by remember(settings.displayName) { mutableStateOf(settings.displayName) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        settingsViewModel.setProfileImage(uri?.toString())
    }

    var showBiometricDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        // SETTINGS TITLE
        item {
            Text(
                text = strings.settings,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                color = textColor
            )
        }

        // PROFILE HEADER
        item {
            Text(strings.profileSectionTitle, fontWeight = FontWeight.SemiBold, color = textColor)
        }

        // PROFILE CARD
        item {
            ProfileCard(
                nameInput = displayNameInput,
                onNameChange = { displayNameInput = it },
                onSaveProfile = {
                    coroutine.launch {
                        authViewModel.updateUsername(
                            newName = displayNameInput,
                            onSuccess = {
                                settingsViewModel.setDisplayName(displayNameInput)
                            },
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
                cardColor = cardColor,
                isDarkMode = isDarkMode
            )
        }

        // PREFERENCES HEADER
        item {
            Text("Preferences", fontWeight = FontWeight.SemiBold, color = textColor)
        }

        // DARK MODE TOGGLE
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

        // LANGUAGE TOGGLE
        item {
            SettingsToggleRow(
                icon = Icons.Rounded.Language,
                title = strings.languageToggle,
                description = if (settings.language == UserSettings.Language.ENGLISH) "English" else "isiZulu",
                isChecked = settings.language == UserSettings.Language.ISIZULU,
                onToggle = {
                    val newLang =
                        if (settings.language == UserSettings.Language.ENGLISH)
                            UserSettings.Language.ISIZULU else UserSettings.Language.ENGLISH

                    settingsViewModel.setLanguage(newLang)
                },
                isDarkMode = isDarkMode
            )
        }

        // NOTIFICATIONS
        item {
            SettingsToggleRow(
                icon = Icons.Rounded.Notifications,
                title = strings.notifications,
                description = "Reminders and updates",
                isChecked = settings.notificationsEnabled,
                onToggle = {
                    settingsViewModel.setNotifications(!settings.notificationsEnabled)
                },
                isDarkMode = isDarkMode
            )
        }

        // BIOMETRIC LOGIN
        item {
            SettingsToggleRow(
                icon = Icons.Rounded.Fingerprint,
                title = "Biometric Login",
                description = "Use fingerprint or face scan to sign in",
                isChecked = CredentialStore.isBiometricEnabled(context),
                onToggle = {
                    if (CredentialStore.isBiometricEnabled(context)) {
                        CredentialStore.clearCredentials(context)
                        CredentialStore.setBiometricEnabled(context, false)
                    } else {
                        showBiometricDialog = true
                    }
                },
                isDarkMode = isDarkMode
            )
        }

        // DATA & PRIVACY HEADER
        item {
            Text("Data & Privacy", fontWeight = FontWeight.SemiBold, color = textColor)
        }

        // DATA LINK
        item {
            SettingsLinkRow(
                icon = Icons.Rounded.Storage,
                title = strings.manageData,
                description = "Sync settings & offline storage",
                isDarkMode = isDarkMode
            )
        }

        // PRIVACY LINK
        item {
            SettingsLinkRow(
                icon = Icons.Rounded.Shield,
                title = "Privacy",
                description = "Your data is stored locally & securely",
                isDarkMode = isDarkMode
            )
        }

        // SIGN OUT
        item {
            FitSecondaryButton(label = strings.signOut, leadingIcon = Icons.AutoMirrored.Rounded.Logout) {
                authViewModel.logout()
                onSignOut()
            }
        }
    }

    // BIOMETRIC ENABLE DIALOG
    if (showBiometricDialog) {
        AlertDialog(
            onDismissRequest = { showBiometricDialog = false },
            title = { Text("Enable Biometric Login?") },
            text = { Text("This will allow signing in using your device biometrics.") },
            confirmButton = {
                TextButton(onClick = {
                    showBiometricDialog = false

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

@Composable
private fun ProfileCard(
    nameInput: String,
    onNameChange: (String) -> Unit,
    onSaveProfile: () -> Unit,
    onChangePhoto: () -> Unit,
    onRemovePhoto: () -> Unit,
    profileImageUri: String?,
    strings: AppStrings,
    textColor: androidx.compose.ui.graphics.Color,
    cardColor: androidx.compose.ui.graphics.Color,
    isDarkMode: Boolean
) {
    val fieldColors = TextFieldDefaults.colors(
        focusedIndicatorColor = textColor,
        unfocusedIndicatorColor = textColor.copy(alpha = 0.4f),
        focusedTextColor = textColor,
        unfocusedTextColor = textColor,
        cursorColor = textColor,
        focusedContainerColor = cardColor,
        unfocusedContainerColor = cardColor
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // TOP ROW
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ProfileAvatar(profileImageUri, isDarkMode)

                Column {
                    Text(
                        nameInput.ifBlank { strings.profileNameLabel },
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = textColor
                    )
                    Text(
                        strings.profileEmailPlaceholder,
                        color = textColor.copy(alpha = 0.7f),
                        fontSize = 14.sp
                    )
                }
            }

            // NAME FIELD
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(strings.profileNameLabel, fontWeight = FontWeight.SemiBold, color = textColor)

                OutlinedTextField(
                    value = nameInput,
                    onValueChange = onNameChange,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = fieldColors
                )
            }

            // PHOTO BUTTONS
            FitSecondaryButton(label = strings.changePhoto, leadingIcon = Icons.Rounded.PhotoCamera) {
                onChangePhoto()
            }

            if (profileImageUri != null) {
                FitSecondaryButton(label = strings.removePhoto, leadingIcon = Icons.Rounded.Delete) {
                    onRemovePhoto()
                }
            }

            // SAVE PROFILE BUTTON
            FitButton(label = strings.saveProfile, leadingIcon = Icons.Rounded.Person) {
                onSaveProfile()
            }
        }
    }
}

@Composable
private fun ProfileAvatar(
    photoUri: String?,
    isDarkMode: Boolean
) {
    val placeholderColor =
        if (isDarkMode) White.copy(alpha = 0.1f) else Black.copy(alpha = 0.05f)

    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(placeholderColor),
        contentAlignment = Alignment.Center
    ) {
        if (photoUri != null) {
            AsyncImage(
                model = photoUri,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                imageVector = Icons.Rounded.Person,
                contentDescription = null,
                tint = Lime,
                modifier = Modifier.size(32.dp)
            )
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
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = Lime)
            Spacer(Modifier.width(12.dp))

            Column {
                Text(title, fontWeight = FontWeight.SemiBold, color = textColor)
                Text(
                    description,
                    color = textColor.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
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
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = Lime)
        Spacer(Modifier.width(12.dp))

        Column {
            Text(title, fontWeight = FontWeight.SemiBold, color = textColor)
            Text(description, color = textColor.copy(alpha = 0.7f), fontSize = 12.sp)
        }
    }
}
