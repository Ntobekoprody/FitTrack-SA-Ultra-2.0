package com.fittracksa.app.ui.screens.login

import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import com.fittracksa.app.ui.AppStrings
import com.fittracksa.app.ui.auth.AuthViewModel
import com.fittracksa.app.ui.screens.common.FitButton
import com.fittracksa.app.ui.theme.Black
import com.fittracksa.app.ui.theme.White
import com.fittracksa.app.ui.theme.Lime

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

    val viewModel: AuthViewModel = viewModel()
    val state by viewModel.state.collectAsState()

    val biometricHelper = remember(context) {
        (context as? FragmentActivity)?.let { BiometricAuthenticator(it) }
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

        Spacer(Modifier.height(32.dp))

        OutlinedTextField(
            value = state.email,
            onValueChange = viewModel::updateEmail,
            label = { Text(strings.email) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = state.password,
            onValueChange = viewModel::updatePassword,
            visualTransformation = PasswordVisualTransformation(),
            label = { Text(strings.password) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        FitButton(label = strings.emailLogin) {
            viewModel.login {
                Toast.makeText(context, strings.loginSuccess, Toast.LENGTH_SHORT).show()
                onLoginSuccess()
            }
        }

        if (state.loading) {
            Spacer(Modifier.height(16.dp))
            CircularProgressIndicator(color = Lime)
        }

        state.error?.let {
            Spacer(Modifier.height(16.dp))
            Text(text = it, color = Lime, fontSize = 14.sp)
        }

        Spacer(Modifier.height(24.dp))

        FitButton(label = strings.biometric) {
            val helper = biometricHelper
            if (helper == null || !helper.canAuthenticate()) {
                Toast.makeText(context, strings.biometricUnavailable, Toast.LENGTH_SHORT).show()
                return@FitButton
            }

            helper.authenticate(
                title = strings.biometric,
                subtitle = strings.biometricSubtitle,
                negativeButtonText = strings.cancel,
                onSuccess = {
                    // In real use: load saved email/password from CredentialStore
                    onLoginSuccess()
                },
                onError = { msg ->
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
            )
        }

        Spacer(Modifier.height(16.dp))

        FitButton(label = strings.register) {
            onNavigateToRegister()
        }
    }
}
