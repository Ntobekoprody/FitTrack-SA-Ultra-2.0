package com.fittracksa.app.ui.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fittracksa.app.SettingsViewModel
import com.fittracksa.app.data.preferences.UserSettings
import com.fittracksa.app.ui.AppStrings
import com.fittracksa.app.ui.screens.common.FitButton
import com.fittracksa.app.ui.screens.common.FitSecondaryButton
import com.fittracksa.app.ui.theme.Black
import com.fittracksa.app.ui.theme.Lime
import com.fittracksa.app.ui.theme.White

@Composable
fun ProfileSetupScreen(
    strings: AppStrings,
    isDarkMode: Boolean,
    settingsViewModel: SettingsViewModel,
    onRegistrationComplete: (String) -> Unit,
    onBackToLogin: () -> Unit
) {
    val settings by settingsViewModel.settings.collectAsState()
    var displayName by remember(settings.displayName) {
        mutableStateOf(
            settings.displayName.takeUnless { it == UserSettings.DEFAULT_DISPLAY_NAME } ?: ""
        )
    }
    var emailInput by remember(settings.email) {
        mutableStateOf(
            settings.email.takeUnless { it == UserSettings.DEFAULT_EMAIL } ?: ""
        )
    }

    val surface = if (isDarkMode) Black else White
    val textColor = if (isDarkMode) Lime else Black
    val containerColor = if (isDarkMode) Black else White
    val fieldColors = TextFieldDefaults.colors(
        focusedIndicatorColor = textColor,
        unfocusedIndicatorColor = textColor.copy(alpha = 0.4f),
        focusedTextColor = textColor,
        unfocusedTextColor = textColor,
        cursorColor = textColor,
        focusedContainerColor = containerColor,
        unfocusedContainerColor = containerColor
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(surface)
            .padding(horizontal = 24.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = strings.register,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = strings.registerSubtitle,
            color = textColor.copy(alpha = 0.8f),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(32.dp))
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(
                value = displayName,
                onValueChange = { displayName = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text(strings.profileNameLabel, color = textColor) },
                colors = fieldColors
            )
            OutlinedTextField(
                value = emailInput,
                onValueChange = { emailInput = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text(strings.profileEmailLabel, color = textColor) },
                colors = fieldColors,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
        FitButton(
            label = strings.registerCta,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
        ) {
            settingsViewModel.setDisplayName(displayName)
            settingsViewModel.setEmail(emailInput)
            val nameForGreeting = displayName.ifBlank { UserSettings.DEFAULT_DISPLAY_NAME }
            onRegistrationComplete(nameForGreeting)
        }
        Spacer(modifier = Modifier.height(16.dp))
        FitSecondaryButton(
            label = strings.registerBackToLogin,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            onBackToLogin()
        }
    }
}
