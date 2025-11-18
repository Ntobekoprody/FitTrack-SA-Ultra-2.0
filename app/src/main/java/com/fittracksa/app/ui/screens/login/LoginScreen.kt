package com.fittracksa.app.ui.screens.login

import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fittracksa.app.biometrics.BiometricAuthenticator
import com.fittracksa.app.notifications.MessagingHelper
import com.fittracksa.app.services.CredentialStore
import com.fittracksa.app.ui.AppStrings
import com.fittracksa.app.ui.auth.AuthViewModel
import com.fittracksa.app.ui.screens.common.FitButton
import com.fittracksa.app.ui.theme.Black
import com.fittracksa.app.ui.theme.Lime
import com.fittracksa.app.ui.theme.White

@Composable
fun LoginScreen(
    strings: AppStrings,
    isDarkMode: Boolean,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val surface = if (isDarkMode) Black else White
    val titleColor = if (isDarkMode) Lime else Black
    val context = LocalContext.current

    val authViewModel: AuthViewModel = viewModel()
    val state by authViewModel.state.collectAsState()

    val biometricAuthenticator = remember(context) {
        (context as? ComponentActivity)?.let { BiometricAuthenticator(it) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(surface)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = strings.loginTitle,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = titleColor,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // EMAIL FIELD
        OutlinedTextField(
            value = state.email,
            onValueChange = authViewModel::updateEmail,
            label = { Text(strings.email) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // PASSWORD FIELD
        OutlinedTextField(
            value = state.password,
            onValueChange = authViewModel::updatePassword,
            visualTransformation = PasswordVisualTransformation(),
            label = { Text(strings.password) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // EMAIL/PASSWORD LOGIN BUTTON
        FitButton(label = strings.emailLogin) {
            authViewModel.login {
                // Save FCM token after login
                MessagingHelper.fetchAndSaveToken()

                // Optional: Enable biometric auto-login by saving credentials:
                CredentialStore.saveCredentials(context, state.email, state.password)

                Toast.makeText(context, strings.loginSuccess, Toast.LENGTH_SHORT).show()
                onLoginSuccess()
            }
        }

        // LOADING SPINNER
        if (state.loading) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator(color = Lime)
        }

        // ERROR MESSAGE
        state.error?.let { err ->
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = err, color = Lime, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // BIOMETRIC LOGIN BUTTON
        FitButton(label = strings.biometric) {
            val helper = biometricAuthenticator
            if (helper == null || !helper.canAuthenticate()) {
                Toast.makeText(context, strings.biometricUnavailable, Toast.LENGTH_SHORT).show()
                return@FitButton
            }

            helper.authenticate(
                title = strings.biometric,
                subtitle = strings.biometricSubtitle,
                negativeButtonText = strings.cancel,
                onSuccess = {
                    // Load saved encrypted credentials
                    val creds = CredentialStore.getCredentials(context)
                    if (creds == null) {
                        Toast.makeText(context, "No saved login credentials found", Toast.LENGTH_SHORT).show()
                        return@authenticate
                    }

                    val (savedEmail, savedPass) = creds

                    // Apply to ViewModel
                    authViewModel.updateEmail(savedEmail)
                    authViewModel.updatePassword(savedPass)

                    // Sign in using Firebase
                    authViewModel.login {
                        MessagingHelper.fetchAndSaveToken()
                        Toast.makeText(context, strings.loginSuccess, Toast.LENGTH_SHORT).show()
                        onLoginSuccess()
                    }
                },
                onError = { message ->
                    val text = if (message == "BIOMETRIC_FAILED") strings.biometricFailed else message
                    Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // NAVIGATE TO REGISTRATION SCREEN
        FitButton(label = strings.register) {
            onNavigateToRegister()
        }
    }
}
